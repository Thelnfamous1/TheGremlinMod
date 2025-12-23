package me.theinfamous1.thegremlinmod.common.entity;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.event.EventHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MogwaiCocoon extends Mob implements GeoEntity, GremlinConvert {
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("cocoon.idle");
    private static final RawAnimation CONVERT_FROM_MOGWAI = RawAnimation.begin().thenPlay("cocoon.mogwai");
    private static final RawAnimation CONVERT_TO_GREMLIN = RawAnimation.begin().thenPlay("cocoon.gremlin");
    public static final int TIME_TO_HATCH = TheGremlinMod.isDevelopmentEnvironment() ? 100 : Level.TICKS_PER_DAY;
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> DATA_SPAWNING = SynchedEntityData.defineId(MogwaiCocoon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HATCHING = SynchedEntityData.defineId(MogwaiCocoon.class, EntityDataSerializers.BOOLEAN);
    public static final int SPAWN_ANIMATION_TIME = 20;
    private int spawnAnimationTicks;
    public static final int HATCH_ANIMATION_TIME = 20;
    private int hatchAnimationTicks;
    private boolean hatched;

    public MogwaiCocoon(EntityType<? extends MogwaiCocoon> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.STEP_HEIGHT, 0.0);
    }

    public static boolean checkCustomSpawnRules(EntityType<? extends MogwaiCocoon> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return PathfinderMob.checkMobSpawnRules(animal, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SPAWNING, false);
        builder.define(DATA_HATCHING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key.equals(DATA_SPAWNING)){
            if(this.isSpawning()){
                if (this.spawnAnimationTicks == 0) this.spawnAnimationTicks = SPAWN_ANIMATION_TIME;
            } else{
                this.spawnAnimationTicks = 0;
            }
        }
        if(key.equals(DATA_HATCHING)){
            if(this.isHatching()){
                if (this.hatchAnimationTicks == 0) this.hatchAnimationTicks = HATCH_ANIMATION_TIME;
            } else{
                this.hatchAnimationTicks = 0;
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity, this.isSpawning() ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (packet.getData() == 1) {
            this.setSpawning(true);
        }
    }

    public boolean isSpawning(){
        return this.entityData.get(DATA_SPAWNING);
    }

    public void setSpawning(boolean spawning){
        this.entityData.set(DATA_SPAWNING, spawning);
    }

    public boolean isHatching(){
        return this.entityData.get(DATA_HATCHING);
    }

    public void setHatching(boolean hatching){
        this.entityData.set(DATA_HATCHING, hatching);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        compound.putInt("age", this.tickCount);
        compound.putBoolean("hatched", this.hatched);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if(compound.contains("age")){
            this.tickCount = compound.getInt("age");
        }
        if(compound.contains("hatched")){
            this.hatched = compound.getBoolean("hatched");
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSpawning();
        this.updateHatching();

        if(!this.level().isClientSide() && this.tickCount > TIME_TO_HATCH && !this.isHatching() && !this.hatched){
            this.setHatching(true);
        }
    }

    private void updateHatching() {
        if(this.hatchAnimationTicks > 0){
            --this.hatchAnimationTicks;
        }
        if(this.hatchAnimationTicks == 0 && this.isHatching()){
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished hatching for {}", this);
            }
            this.hatched = true;
            this.setHatching(false);
            if(!this.level().isClientSide()){
                Gremlin gremlin = this.convertTo(TheGremlinMod.GREMLIN.get(), false);
                if(gremlin != null){
                    if(TheGremlinMod.isDevelopmentEnvironment()){
                        TheGremlinMod.LOGGER.info("Spawned gremlin for {}", this);
                    }
                    EventHooks.onLivingConvert(this, gremlin);
                }
            }
        }
    }

    private void updateSpawning() {
        if(this.spawnAnimationTicks > 0){
            --this.spawnAnimationTicks;
        }
        if(this.spawnAnimationTicks == 0 && this.isSpawning()){
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished spawning for {}", this);
            }
            this.setSpawning(false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", this::animate));
    }

    private PlayState animate(AnimationState<MogwaiCocoon> state) {
        if(this.isSpawning()){
            return state.setAndContinue(CONVERT_FROM_MOGWAI);
        } else if(state.isCurrentAnimation(CONVERT_FROM_MOGWAI)){
            state.resetCurrentAnimation();
        }

        if(this.isHatching()){
            return state.setAndContinue(CONVERT_TO_GREMLIN);
        } else if(state.isCurrentAnimation(CONVERT_TO_GREMLIN)){
            state.resetCurrentAnimation();
        }

        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof Player && !this.level().mayInteract((Player)entity, this.blockPosition());
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.isCreativePlayer()){
            return super.hurt(source, amount);
        }
        return false;
    }

    @Override
    public void finishConversion(Mob convertedFrom) {
        this.setSpawning(true);
    }

    @Override
    public boolean receivesConversion(Mob convertingMob) {
        return convertingMob.getType() == TheGremlinMod.MOGWAI.get();
    }
}
