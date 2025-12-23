package me.theinfamous1.thegremlinmod.common.entity;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.entity.ai.GoToLandGoal;
import me.theinfamous1.thegremlinmod.common.entity.ai.MogwaiRestGoal;
import me.theinfamous1.thegremlinmod.common.entity.ai.FleeRainGoal;
import me.theinfamous1.thegremlinmod.common.entity.ai.RestrictRainGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class Mogwai extends AbstractGremlin implements GeoEntity{
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("mogwai.idle");
    private static final RawAnimation IDLE_2 = RawAnimation.begin().thenPlay("mogwai.idle2");
    private static final RawAnimation DUPLICATE = RawAnimation.begin().thenPlay("mogwai.duplicate");
    private static final RawAnimation SWIM = RawAnimation.begin().thenPlay("mogwai.swim");
    private static final RawAnimation CRY = RawAnimation.begin().thenPlay("mogwai.cry");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("mogwai.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenPlay("mogwai.run");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("mogwai.eat");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("mogwai.sleep");
    private static final RawAnimation HURT = RawAnimation.begin().thenPlay("mogwai.hurt");
    private static final RawAnimation DIE = RawAnimation.begin().thenPlay("mogwai.die");
    private static final RawAnimation COCOON = RawAnimation.begin().thenPlay("mogwai.cocoon");
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public static final int DUPLICATION_ANIMATION_TIME = 30;
    public static final int EATING_ANIMATION_TIME = 40;
    public static final int COCOONING_ANIMATION_TIME = 20;
    public static final int DEATH_ANIMATION_TIME = 20;
    protected int eatAnimationTicks;
    protected int cocooningAnimationTicks;
    private boolean fedPastBedtime;

    public Mogwai(EntityType<? extends Mogwai> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }

    public static boolean checkCustomSpawnRules(EntityType<? extends Mogwai> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return PathfinderMob.checkMobSpawnRules(animal, level, spawnType, pos, random);
    }

    public boolean isCrying() {
        return this.getActionFlag(2);
    }

    public void setCrying(boolean crying) {
        this.setActionFlag(2, crying);
    }

    public boolean isEating(){
        return this.getActionFlag(3);
    }

    public void setEating(boolean eating){
        this.setActionFlag(3, eating);
    }

    public boolean isLying() {
        return this.getActionFlag(4);
    }

    public void setLying(boolean lying) {
        this.setActionFlag(4, lying);
    }

    public boolean isCocooning() {
        return this.getActionFlag(5);
    }

    public void setCocooning(boolean cocoon) {
        this.setActionFlag(5, cocoon);
    }

    @Override
    protected int getDuplicationTime() {
        return DUPLICATION_ANIMATION_TIME;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key.equals(AbstractGremlin.DATA_ACTION_FLAGS_ID)){
            if (this.isEating()) {
                if (this.eatAnimationTicks == 0) this.eatAnimationTicks = Mogwai.EATING_ANIMATION_TIME;
            } else if (this.eatAnimationTicks > 0) {
                this.eatAnimationTicks = 0;
            }
            if (this.isCocooning()) {
                if (this.cocooningAnimationTicks == 0) this.cocooningAnimationTicks = Mogwai.COCOONING_ANIMATION_TIME;
            } else if (this.cocooningAnimationTicks > 0) {
                this.cocooningAnimationTicks = 0;
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3F));
        this.goalSelector.addGoal(1, new RestrictRainGoal(this));
        this.goalSelector.addGoal(2, new GoToLandGoal(this, 1.3F, 15, 7));
        this.goalSelector.addGoal(2, new FleeRainGoal(this, 1.3F));
        this.goalSelector.addGoal(3, new MogwaiRestGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0F, 0.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isFood(itemstack) && !this.isEating()) {
            if(this.isDoNotFeedTime() && !this.level().isClientSide()){
                if(TheGremlinMod.isDevelopmentEnvironment()){
                    TheGremlinMod.LOGGER.info("Fed past bedtime for {}", this);
                }
                this.setFedPastBedtime(true);
            }
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Triggered eating for {}", this);
            }
            this.setEating(true);
            this.startEatingItem(itemstack);
            this.removeInteractionItem(player, itemstack);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    private void startEatingItem(ItemStack itemstack) {
        this.setItemInHand(InteractionHand.MAIN_HAND, itemstack.copyWithCount(1));
    }

    private void removeInteractionItem(Player player, ItemStack stack) {
        stack.consume(1, player);
    }

    public boolean isFood(ItemStack itemstack) {
        return itemstack.has(DataComponents.FOOD);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Move", this::animateMovement));
        controllers.add(new AnimationController<>(this, "Action", this::animateAction));
    }

    private PlayState animateMovement(AnimationState<Mogwai> state) {
        return GremlinAnimationHandlers.animateMovement(this, state, SWIM, WALK, RUN, IDLE, IDLE_2);
    }

    private PlayState animateAction(AnimationState<Mogwai> state) {
        state.setControllerSpeed(1.0F);
        if(this.hasPose(Pose.DYING)){
            return state.setAndContinue(DIE);
        } else if(state.isCurrentAnimation(DIE)){
            state.resetCurrentAnimation();
        }

        if(this.isAnimatingHurt()){
            state.setControllerSpeed(2.0F);
            return state.setAndContinue(HURT);
        } else if(state.isCurrentAnimation(HURT)){
            state.resetCurrentAnimation();
        }

        if(this.isEating()){
            return state.setAndContinue(EAT);
        } else if(state.isCurrentAnimation(EAT)){
            state.resetCurrentAnimation();
        }

        if(this.isCocooning()){
            return state.setAndContinue(COCOON);
        } else if(state.isCurrentAnimation(COCOON)){
            state.resetCurrentAnimation();
        }

        if(this.isDuplicating()){
            return state.setAndContinue(DUPLICATE);
        }

        if(this.isCrying()){
            return state.setAndContinue(CRY);
        }

        if(this.isLying()){
            return state.setAndContinue(SLEEP);
        }

        return PlayState.STOP;
    }

    public boolean canPerformSleep() {
        return !this.isDuplicating() && !this.isCrying() && !this.isEating() && !this.isCocooning();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // eating
        this.updateEating();

        // cocooning
        this.updateCocooning();
    }

    private void updateCocooning() {
        if(this.cocooningAnimationTicks > 0){
            --this.cocooningAnimationTicks;
        }

        if (this.cocooningAnimationTicks == 0 && this.isCocooning()) {
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished cocooning for {}", this);
            }
            this.setCocooning(false);
            if(!this.level().isClientSide()){
                MogwaiCocoon cocoon = this.convertTo(TheGremlinMod.MOGWAI_COCOON.get(), false);
                if(cocoon != null){
                    if(TheGremlinMod.isDevelopmentEnvironment()){
                        TheGremlinMod.LOGGER.info("Spawned cocoon for {}", this);
                    }
                    cocoon.setSpawning(true);
                    EventHooks.onLivingConvert(this, cocoon);
                }
            }
        }
    }

    private void updateEating() {
        if(this.eatAnimationTicks > 0){
            this.updateEatingItem();
            --this.eatAnimationTicks;
        }

        if (this.eatAnimationTicks == 0 && this.isEating()) {
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished eating for {}", this);
            }
            this.setEating(false);
            if(!this.level().isClientSide()){
                if(this.isDoNotFeedTime()){
                    if(TheGremlinMod.isDevelopmentEnvironment()){
                        TheGremlinMod.LOGGER.info("Triggered cocooning for {}", this);
                    }
                    this.setCocooning(true);
                }
            }
        }
    }

    private void updateEatingItem() {
        if(this.shouldChew() && this.eatAnimationTicks % 2 == 0){
            this.addEatingParticles(this.getMainHandItem(), 5);
        }
        if(this.shouldSwallow()){
            ItemStack eatResult = this.eatFood(this.getMainHandItem());
            this.setItemInHand(InteractionHand.MAIN_HAND, eatResult);
        }
    }

    private float getEatProgress() {
        return Mth.clamp((float) (EATING_ANIMATION_TIME - this.eatAnimationTicks) / EATING_ANIMATION_TIME, 0.0F, 1.0F);
    }

    private boolean shouldChew(){
        float eatProgress = this.getEatProgress();
        return eatProgress >= 0.375 && eatProgress < 0.69F;
    }

    private boolean shouldSwallow(){
        return this.getEatProgress() >= 0.69F;
    }

    @Override
    public ItemStack eat(Level level, ItemStack food, FoodProperties foodProperties) {
        ItemStack eatResult = super.eat(level, food, foodProperties);
        Optional<ItemStack> usingConvertsTo = foodProperties.usingConvertsTo();
        if (usingConvertsTo.isPresent()) {
            if (!this.level().isClientSide()) {
                ItemStack container = usingConvertsTo.get().copy();
                this.spawnAtLocation(container);
            }
        }
        return eatResult;
    }

    private ItemStack eatFood(ItemStack eatingStack) {
        if(this.isFood(eatingStack)){
            this.addEatingParticles(eatingStack, 16);
            return this.eat(this.level(), eatingStack);
        }
        return eatingStack;
    }

    private void addEatingParticles(ItemStack eatingStack, int amount) {
        if(this.isFood(eatingStack)){
            this.spawnItemParticles(eatingStack, amount);
            this.playSound(this.getEatingSound(eatingStack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    private void spawnItemParticles(ItemStack stack, int amount) {
        for(int i = 0; i < amount; ++i) {
            Vec3 posVec = this.getMouthPosition().add(this.getLookAngle().multiply(this.getBbWidth() * 0.5D, 0, this.getBbWidth() * 0.5D));
            double randomX = ((double) this.random.nextFloat() - 0.5) * 0.1;
            double randomY = Math.random() * 0.1 + 0.1;
            Vec3 speedVec = (new Vec3(randomX, randomY, 0.0))
                    // Assuming players are 0.6 blocks wide, we need to scale the speed vec by how big our mob is relative to players
                    .scale(this.getBbWidth() / 0.6F)
                    .xRot(-this.getXRot() * Mth.DEG_TO_RAD)
                    .yRot(-this.getYRot() * Mth.DEG_TO_RAD);
            this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), posVec.x, posVec.y, posVec.z, speedVec.x, speedVec.y, speedVec.z);
        }

    }

    private Vec3 getMouthPosition() {
        return this.position().add(0, this.getBbHeight() * 0.75D, 0);
    }

    private boolean isDoNotFeedTime() {
        long dayTime = this.level().getDayTime();
        return dayTime >= 18000L && dayTime < 23000L;
    }

    @Override
    protected EntityType<? extends AbstractGremlin> getDuplicationType() {
        return TheGremlinMod.MOGWAI.get();
    }

    @Override
    public boolean isPerformingAnimatedAction() {
        return this.isDuplicating() || this.isCrying() || this.isEating() || this.isLying() || this.isCocooning();
    }

    @Override
    public boolean canWalkWhilePerformingAnimatedAction() {
        return this.isDuplicating() || this.isCrying() || this.isEating();
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.updateCrying();
    }

    private void updateCrying() {
        if(this.fedPastBedtime() && !this.isDoNotFeedTime()){
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Reset fed past bedtime for {}", this);
            }
            this.setFedPastBedtime(false);
        }
        this.setCrying(this.isDoNotFeedTime() && !this.fedPastBedtime());
    }

    private boolean fedPastBedtime() {
        return this.fedPastBedtime;
    }

    private void setFedPastBedtime(boolean fed) {
        this.fedPastBedtime = fed;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("fed_past_bedtime")){
            this.setFedPastBedtime(compound.getBoolean("fed_past_bedtime"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("fed_past_bedtime", this.fedPastBedtime());
    }
}
