package me.theinfamous1.thegremlinmod.common.item;

import me.theinfamous1.thegremlinmod.Config;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.client.ClientSoundHandler;
import me.theinfamous1.thegremlinmod.client.SunbeamItemRenderer;
import me.theinfamous1.thegremlinmod.common.util.TGMTags;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class SunbeamItem extends Item implements GeoItem {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final Set<UUID> USE_SOUND_PLAYED = new HashSet<>();

    public SunbeamItem(Properties properties) {
        super(properties);
    }

    // Utilise our own render hook to define our custom renderer
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<SunbeamItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new SunbeamItemRenderer(SunbeamItem.this);

                return this.renderer;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        playSwitchSound(level, player);
        boolean switchValue = getSwitchValue(itemstack);
        setSwitchValue(itemstack, !switchValue);
        if(getSwitchValue(itemstack) && getTimer(itemstack) <= 0){
            setTimer(itemstack, getMaxSunbeamUseTimeTicks());
            USE_SOUND_PLAYED.remove(player.getUUID());
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemstack);
    }

    public static void setSwitchValue(ItemStack itemstack, boolean switchValue) {
        itemstack.set(TheGremlinMod.SWITCH, switchValue);
    }

    public static Boolean getSwitchValue(ItemStack itemstack) {
        return itemstack.get(TheGremlinMod.SWITCH.get());
    }

    public static void setTimer(ItemStack itemstack, int timer) {
        itemstack.set(TheGremlinMod.TIMER, timer);
    }

    public static Integer getTimer(ItemStack itemstack) {
        return itemstack.get(TheGremlinMod.TIMER.get());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity user, int slotId, boolean isSelected) {
        if(getSwitchValue(stack) && getTimer(stack) > 0){
            if(level.isClientSide && user instanceof LivingEntity livingUser && !USE_SOUND_PLAYED.contains(user.getUUID())){
                ClientSoundHandler.playSunbeamSound(livingUser);
                USE_SOUND_PLAYED.add(user.getUUID());
            }
            if(!level.isClientSide){
                switch (Config.SUNBEAM_DETECTION_MODE.get()){
                    case PINPOINT -> {
                        HitResult pick = pick(user, 0);
                        if (pick instanceof EntityHitResult entityHitResult) {
                            Entity hitEntity = entityHitResult.getEntity();
                            applySunbeamDamage(level, user, hitEntity);
                        }
                    }
                    case FOCUSED -> {
                        double maxDistance = Config.SUNBEAM_RANGE.get();
                        double focusAngle = Mth.clamp(Config.SUNBEAM_FOCUS_ANGLE.get(), 0, 360);
                        for (Entity hitEntity : level.getEntities(user, user.getBoundingBox().inflate(maxDistance), e -> !e.isSpectator() && e.isPickable())) {
                            if (isWithinCoverage(user, hitEntity, maxDistance, focusAngle)) {
                                applySunbeamDamage(level, user, hitEntity);
                            }
                        }
                    }
                    default -> {
                        Vec3 eyePos = user.getEyePosition(0);
                        double maxDistance = Config.SUNBEAM_RANGE.get();
                        for (Entity hitEntity : level.getEntities(user, user.getBoundingBox().inflate(maxDistance), e -> !e.isSpectator() && e.isPickable())) {
                            if (isWithinCoverage(user, hitEntity, eyePos, maxDistance)) {
                                applySunbeamDamage(level, user, hitEntity);
                            }
                        }
                    }
                }

            }
            setTimer(stack, Math.max(0, getTimer(stack) - 1));
            if(getTimer(stack) <= 0){
                playSwitchSound(level, user);
                setSwitchValue(stack, false);
                USE_SOUND_PLAYED.remove(user.getUUID());
                if(user instanceof Player player){
                    player.getCooldowns().addCooldown(stack.getItem(), getSunbeamUseCooldownTimeTicks());
                }
            }
        }
    }

    private static void playSwitchSound(Level level, Entity user) {
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
    }

    private static boolean isWithinCoverage(Entity user, Entity hitEntity, Vec3 eyePos, double maxDistance){
        Vec3 targetPos = hitEntity.position().add(0, hitEntity.getBbHeight() * 0.5D, 0);
        Vec3 vectorToTarget = eyePos.vectorTo(targetPos);
        return isWithinDistance(user, eyePos, maxDistance, targetPos, vectorToTarget);
    }

    private static boolean isWithinCoverage(Entity user, Entity hitEntity, double maxDistance, double arcAngle) {
        Vec3 eyePos = user.getEyePosition(0);
        Vec3 lookVec = user.getLookAngle().normalize();
        Vec3 targetPos = hitEntity.position().add(0, hitEntity.getBbHeight() * 0.5D, 0);
        Vec3 vectorToTarget = eyePos.vectorTo(targetPos);
        if (!isWithinDistance(user, eyePos, maxDistance, targetPos, vectorToTarget)) return false;

        Vec3 targetAngle = vectorToTarget.normalize();
        double dot = lookVec.dot(targetAngle);
        return isWithinCoverageAngle(arcAngle, dot);
    }

    private static boolean isWithinDistance(Entity user, Vec3 eyePos, double maxDistance, Vec3 targetPos, Vec3 vectorToTarget) {
        double maxDistanceSqr = Mth.square(maxDistance);
        ClipContext context = new ClipContext(eyePos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user);
        HitResult blockPickResult = user.level().clip(context);
        double distanceToBPRSqr = blockPickResult.getLocation().distanceToSqr(eyePos);
        if (blockPickResult.getType() != HitResult.Type.MISS) {
            maxDistanceSqr = distanceToBPRSqr;
        }

        double distanceToTargetSqr = vectorToTarget.lengthSqr();
        return distanceToTargetSqr <= maxDistanceSqr;
    }

    private static boolean isWithinCoverageAngle(double coverageDeg, double dot) {
        if (coverageDeg <= 180) {
            // Forward arc
            double halfAngle = coverageDeg * 0.5D;
            double minDot = Math.cos(Math.toRadians(halfAngle));
            return dot >= minDot;
        } else {
            // Blind spot behind player
            double blindArc = 360 - coverageDeg;
            double blindHalf = blindArc * 0.5D;
            double blindDot = Math.cos(Math.toRadians(blindHalf));
            return dot > -blindDot;
        }
    }

    private static void applySunbeamDamage(Level level, Entity entity, Entity hitEntity) {
        if(hitEntity.getType().is(TGMTags.VULNERABLE_TO_SUNBEAM)){
            DamageSource sunbeamDamageSource = level.damageSources().magic();
            if(entity.invulnerableTime <= 0 || sunbeamDamageSource.is(DamageTypeTags.BYPASSES_COOLDOWN)){
                hitEntity.hurt(sunbeamDamageSource, Config.SUNBEAM_DAMAGE.get());
            }
        }
    }

    private static HitResult pick(Entity entity, float partialTick) {
        double interactionRange = Config.SUNBEAM_RANGE.get();
        double interactionRangeSqr = Mth.square(interactionRange);
        Vec3 eyePosition = entity.getEyePosition(partialTick);
        HitResult blockPickResult = entity.pick(interactionRange, partialTick, false);
        double distanceToBPRSqr = blockPickResult.getLocation().distanceToSqr(eyePosition);
        if (blockPickResult.getType() != HitResult.Type.MISS) {
            interactionRangeSqr = distanceToBPRSqr;
            interactionRange = Math.sqrt(distanceToBPRSqr);
        }

        Vec3 viewVector = entity.getViewVector(partialTick);
        Vec3 targetVector = eyePosition.add(viewVector.x * interactionRange, viewVector.y * interactionRange, viewVector.z * interactionRange);
        AABB searchBox = entity.getBoundingBox().expandTowards(viewVector.scale(interactionRange)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(
                entity, eyePosition, targetVector, searchBox, e -> !e.isSpectator() && e.isPickable(), interactionRangeSqr
        );
        return entityhitresult != null && entityhitresult.getLocation().distanceToSqr(eyePosition) < distanceToBPRSqr
                ? filterHitResult(entityhitresult, eyePosition, interactionRange)
                : filterHitResult(blockPickResult, eyePosition, interactionRange);
    }

    private static HitResult filterHitResult(HitResult hitResult, Vec3 pos, double interactionRange) {
        Vec3 vec3 = hitResult.getLocation();
        if (!vec3.closerThan(pos, interactionRange)) {
            Vec3 vec31 = hitResult.getLocation();
            Direction direction = Direction.getNearest(vec31.x - pos.x, vec31.y - pos.y, vec31.z - pos.z);
            return BlockHitResult.miss(vec31, direction, BlockPos.containing(vec31));
        } else {
            return hitResult;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getTimer(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)getTimer(stack) * 13.0F / getMaxSunbeamUseTimeTicks());
    }

    private static Integer getMaxSunbeamUseTimeTicks() {
        return Config.MAX_SUNBEAM_USE_TIME.get() * 20;
    }

    private static Integer getSunbeamUseCooldownTimeTicks() {
        return Config.SUNBEAM_USE_COOLDOWN_TIME.get() * 20;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (getTimer(stack)) / getMaxSunbeamUseTimeTicks());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
