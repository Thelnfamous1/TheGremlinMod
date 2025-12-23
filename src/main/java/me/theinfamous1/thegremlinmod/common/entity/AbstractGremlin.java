package me.theinfamous1.thegremlinmod.common.entity;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class AbstractGremlin extends PathfinderMob{
    public static final long DUPLICATION_COOLDOWN_TIME = 600L;
    protected static final EntityDataAccessor<Byte> DATA_ACTION_FLAGS_ID = SynchedEntityData.defineId(AbstractGremlin.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE = SynchedEntityData.defineId(AbstractGremlin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_WALKING = SynchedEntityData.defineId(AbstractGremlin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_USE_ALTERNATE_IDLE = SynchedEntityData.defineId(AbstractGremlin.class, EntityDataSerializers.BOOLEAN);
    protected long duplicationCooldown;
    protected int duplicateAnimationTicks;
    private int deathAnimationTicks;
    private static final int SWITCH_IDLE_COOLDOWN_TIME = 40;
    private int switchIdleCooldown;

    public AbstractGremlin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTION_FLAGS_ID, (byte) 0);
        builder.define(DATA_CAN_DUPLICATE, true);
        builder.define(DATA_WALKING, false);
        builder.define(DATA_USE_ALTERNATE_IDLE, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DATA_ACTION_FLAGS_ID)) {
            if (this.isDuplicating()) {
                if (this.duplicateAnimationTicks == 0) this.duplicateAnimationTicks = this.getDuplicationTime();
            } else if (this.duplicateAnimationTicks > 0) {
                this.duplicateAnimationTicks = 0;
            }
        }
    }

    protected abstract int getDuplicationTime();

    protected boolean getActionFlag(int flag) {
        return (this.entityData.get(DATA_ACTION_FLAGS_ID) & 1 << flag) != 0;
    }

    protected void setActionFlag(int flag, boolean set) {
        byte b0 = this.entityData.get(DATA_ACTION_FLAGS_ID);
        if (set) {
            this.entityData.set(DATA_ACTION_FLAGS_ID, (byte) (b0 | 1 << flag));
        } else {
            this.entityData.set(DATA_ACTION_FLAGS_ID, (byte) (b0 & ~(1 << flag)));
        }
    }

    public boolean isDuplicating() {
        return this.getActionFlag(1);
    }

    public void setDuplicating(boolean duplicating) {
        this.setActionFlag(1, duplicating);
    }

    public boolean canDuplicate() {
        return this.entityData.get(DATA_CAN_DUPLICATE);
    }

    public void setCanDuplicate(boolean canDuplicate) {
        this.entityData.set(DATA_CAN_DUPLICATE, canDuplicate);
    }

    public boolean isWalking() {
        return this.entityData.get(DATA_WALKING);
    }

    public void setWalking(boolean walking) {
        this.entityData.set(DATA_WALKING, walking);
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimationTicks;
        if (this.deathAnimationTicks >= Mogwai.DEATH_ANIMATION_TIME && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, EntityEvent.POOF);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("death_animation_ticks")) {
            this.deathAnimationTicks = compound.getInt("death_animation_ticks");
        }
        if(compound.contains("duplication_cooldown")){
            this.duplicationCooldown = compound.getInt("duplication_cooldown");
        }
        if(compound.contains("can_duplicate")){
            this.setCanDuplicate(compound.getBoolean("can_duplicate"));
        }
        if(compound.contains("duplicating")){
            this.setDuplicating(compound.getBoolean("duplicating"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("death_animation_ticks", this.deathAnimationTicks);
        compound.putLong("duplication_cooldown", this.duplicationCooldown);
        compound.putBoolean("can_duplicate", this.canDuplicate());
        compound.putBoolean("duplicating", this.isDuplicating());

    }

    protected boolean isAnimatingHurt() {
        return this.hurtTime > 0;
    }

    public abstract boolean isPerformingAnimatedAction();

    public abstract boolean canWalkWhilePerformingAnimatedAction();

    protected void resetDuplicationCooldown() {
        if(TheGremlinMod.isDevelopmentEnvironment()){
            TheGremlinMod.LOGGER.info("Reset duplication cooldown for {}", this);
        }
        this.duplicationCooldown = DUPLICATION_COOLDOWN_TIME;
        if(TheGremlinMod.isDevelopmentEnvironment()){
            TheGremlinMod.LOGGER.info("Disabled duplication for {}", this);
        }
        this.setCanDuplicate(false);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        if (this.getMoveControl().hasWanted()) {
            double speedModifier = this.getMoveControl().getSpeedModifier();
            this.setSprinting(speedModifier > 1.0D && this.zza > 0.0F);
        } else{
            this.setSprinting(false);
        }
        this.setWalking(this.zza != 0.0F);
        if(this.switchIdleCooldown > 0){
            this.switchIdleCooldown--;
            if(this.switchIdleCooldown == 0){
                this.setUsingAlternateIdle(this.random.nextBoolean());
                this.switchIdleCooldown = SWITCH_IDLE_COOLDOWN_TIME;
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // duplication
        this.updateDuplicationCooldown();
        this.updateDuplication();
    }

    private void updateDuplication() {
        if(!this.level().isClientSide() && this.shouldTriggerDuplication() && this.canDuplicate() && !this.isDuplicating()){
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Triggered environmental duplication for {}", this);
            }
            this.setDuplicating(true);
        }
        if(this.duplicateAnimationTicks > 0){
            --this.duplicateAnimationTicks;
        }
        if(this.duplicateAnimationTicks == 0 && this.isDuplicating()){
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished duplication for {}", this);
            }
            this.setDuplicating(false);
            if(!this.level().isClientSide()){
                this.duplicate();
            }
        }
    }

    protected void duplicate() {
        AbstractGremlin gremlin = this.getDuplicationType().create(this.level());
        if (gremlin != null) {
            gremlin.moveTo(this.position());
            gremlin.setPersistenceRequired();
            gremlin.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level().addFreshEntity(gremlin);
        }
    }

    protected abstract EntityType<? extends AbstractGremlin> getDuplicationType();

    private boolean shouldTriggerDuplication() {
        return this.isInWaterRainOrBubble();
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            --this.duplicationCooldown;
        }

        if (!this.level().isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Enabled duplication for {}", this);
            }
            this.setCanDuplicate(true);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.getDirectEntity() instanceof ThrownPotion thrownPotion && AbstractGremlin.isWaterPotion(thrownPotion)){
            if(!this.level().isClientSide() && this.canDuplicate()){
                if(TheGremlinMod.isDevelopmentEnvironment()){
                    TheGremlinMod.LOGGER.info("Triggered impact duplication for {}", this);
                }
                this.setDuplicating(true);
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    public static boolean isWaterPotion(ThrownPotion potion){
        ItemStack itemstack = potion.getItem();
        PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return potioncontents.is(Potions.WATER);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.setSharedFlag(3, sprinting);
    }

    public void setVanillaSprinting(boolean sprinting){
        super.setSprinting(sprinting);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    public boolean takesVanillaWaterDamage() {
        return false;
    }

    public boolean isUsingAlternateIdle() {
        return this.entityData.get(DATA_USE_ALTERNATE_IDLE);
    }

    public void setUsingAlternateIdle(boolean usingAlternateIdle){
        this.entityData.set(DATA_USE_ALTERNATE_IDLE, usingAlternateIdle);
    }

    protected boolean wantsToSwim() {
        return this.isSprinting();
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                //this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                //this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public float getSwimmingSlowdown(){
        return 0.98F;
    }
}
