package me.theinfamous1.thegremlinmod.common.entity;

import me.theinfamous1.thegremlinmod.TheGremlinModConfig;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.entity.ai.*;
import me.theinfamous1.thegremlinmod.common.util.TGMTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Gremlin extends AbstractGremlin implements GeoEntity, Enemy, GremlinConvert {
    private static final RawAnimation HATCHING = RawAnimation.begin().thenPlay("gremlin.cocoon");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("gremlin.idle");
    private static final RawAnimation IDLE_2 = RawAnimation.begin().thenPlay("gremlin.idle2");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("gremlin.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenPlay("gremlin.run");
    private static final RawAnimation SWIM = RawAnimation.begin().thenPlay("gremlin.swim");
    private static final RawAnimation DUPLICATE = RawAnimation.begin().thenPlay("gremlin.duplicate");
    private static final RawAnimation HIDE_DUPLICATE = RawAnimation.begin().thenLoop("gremlin.hide_duplicate");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("gremlin.attack");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("gremlin.attack2");
    private static final RawAnimation CLIMB = RawAnimation.begin().thenPlay("gremlin.climb");
    private static final RawAnimation HURT = RawAnimation.begin().thenPlay("gremlin.hurt");
    private static final RawAnimation DIE = RawAnimation.begin().thenPlay("gremlin.death");
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public static final int DUPLICATION_ANIMATION_TIME = Mth.ceil(6 * 20);
    private static final int HATCH_ANIMATION_TIME = 20;
    private int hatchAnimationTicks;
    private static final int ATTACK_1_ANIMATION_TIME = Mth.ceil(0.75F * 20);
    private static final int ATTACK_2_ANIMATION_TIME = Mth.ceil(1.25F * 20);
    private static final int DEATH_ANIMATION_TIME = Mth.ceil(1.5F * 20);
    public static final int IDLE1_ANIMATION_TIME = Mth.ceil(1.5 * 20);
    public static final int IDLE2_ANIMATION_TIME = Mth.ceil(3.5 * 20);
    private int attackAnimationTicks;

    private static final EntityDataAccessor<Boolean> DATA_USE_ALTERNATE_ATTACK = SynchedEntityData.defineId(Gremlin.class, EntityDataSerializers.BOOLEAN);
    private int soundTick;

    public Gremlin(EntityType<? extends Gremlin> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }



    public static boolean checkCustomSpawnRules(EntityType<? extends Gremlin> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return PathfinderMob.checkMobSpawnRules(animal, level, spawnType, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GremlinMeleeAttackGoal(this, 1.3F, true));
        this.goalSelector.addGoal(1, new RestrictRainGoal(this));
        this.goalSelector.addGoal(2, new GoToLandGoal(this, 1.3F, 15, 7));
        this.goalSelector.addGoal(2, new FleeRainGoal(this, 1.3F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));


        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,0, false, false, this::canPursueTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, false, le -> le.attackable() && this.canPursueTarget(le)){
            @Override
            public boolean canContinueToUse() {
                return this.target != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
            }
        });
        if(TheGremlinModConfig.GREMLIN_MINE_BLOCKS.get()){
            this.goalSelector.addGoal(TheGremlinModConfig.GREMLIN_MINE_PRIORITY.get(), new GremlinMineGoal<>(this));
        }
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        } else {
            return entity.getType().is(TGMTags.GREMLIN_FRIENDS) && this.getTeam() == null && entity.getTeam() == null;
        }
    }

    public boolean isHatching(){
        return super.getActionFlag(2);
    }

    public void setHatching(boolean hatching){
        super.setActionFlag(2, hatching);
    }

    public boolean isClimbing(){
        return super.getActionFlag(3);
    }

    public void setClimbing(boolean climbing){
        super.setActionFlag(3, climbing);
    }

    @Override
    protected int getDefaultDuplicationTime() {
        return DUPLICATION_ANIMATION_TIME;
    }

    @Override
    protected long getDefaultDuplicationCooldownTime() {
        return TheGremlinModConfig.GREMLIN_DUPLICATION_COOLDOWN.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_USE_ALTERNATE_ATTACK, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key.equals(AbstractGremlin.DATA_ACTION_FLAGS_ID)){
            /*
            if (this.isHatching()) {
                if (this.hatchAnimationTicks == 0) this.hatchAnimationTicks = HATCH_ANIMATION_TIME;
            } else if (this.hatchAnimationTicks > 0) {
                this.hatchAnimationTicks = 0;
            }
             */
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity, this.isHatching() ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (packet.getData() == 1) {
            this.setHatching(true);
        }
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
        //this.updateHatching();

        if (this.attackAnimationTicks > 0) {
            --this.attackAnimationTicks;
        }
    }

    private void updateHatching() {
        if(this.hatchAnimationTicks > 0){
            --this.hatchAnimationTicks;
        }

        if (this.hatchAnimationTicks == 0 && this.isHatching()) {
            if(TheGremlinMod.isDevelopmentEnvironment()){
                TheGremlinMod.LOGGER.info("Finished hatching for {}", this);
            }
            this.setHatching(false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Duplicate", this::animateDuplicate));
        controllers.add(new AnimationController<>(this, "Move", this::animateMovement));
        controllers.add(new AnimationController<>(this, "Action", this::animateAction));
    }

    private PlayState animateDuplicate(AnimationState<Gremlin> state) {
        return GremlinAnimationHandlers.animateDuplicate(this, state, HIDE_DUPLICATE);
    }

    private PlayState animateMovement(AnimationState<Gremlin> state) {
        return GremlinAnimationHandlers.animateMovement(this, state, SWIM, WALK, RUN, IDLE, IDLE_2);
    }

    private PlayState animateAction(AnimationState<Gremlin> state) {
        //state.setControllerSpeed(1.0F);
        if(this.hasPose(Pose.DYING)){
            return state.setAndContinue(DIE);
        } else if(state.isCurrentAnimation(DIE)){
            state.resetCurrentAnimation();
        }

        if(this.isDuplicating()){
            return state.setAndContinue(DUPLICATE);
        }

        /*
        if(this.isHatching()){
            return state.setAndContinue(HATCHING);
        } else if(state.isCurrentAnimation(HATCHING)){
            state.resetCurrentAnimation();
        }
         */

        if(this.isAttacking()){
            if(this.isUsingAlternateAttack()){
                return state.setAndContinue(ATTACK_2);
            } else{
                return state.setAndContinue(ATTACK);
            }
        } else if(state.isCurrentAnimation(ATTACK)){
            state.resetCurrentAnimation();
        } else if(state.isCurrentAnimation(ATTACK_2)){
            state.resetCurrentAnimation();
        }

        if(this.onClimbable()){
            return state.setAndContinue(CLIMB);
        }

        if(this.isAnimatingHurt()){
            //state.setControllerSpeed(2.0F);
            return state.setAndContinue(HURT);
        } else if(state.isCurrentAnimation(HURT)){
            state.resetCurrentAnimation();
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    @Override
    public boolean isPerformingSpecialAction() {
        return this.isDuplicating() || this.isHatching() || this.isAttacking();
    }

    @Override
    public boolean canWalkWhilePerformingSpecialAction() {
        if(this.isDuplicating()){
            return false;
        }
        return this.isAttacking() && !this.isUsingAlternateAttack();
    }

    @Override
    protected EntityType<? extends AbstractGremlin> getDuplicationType() {
        return TheGremlinMod.GREMLIN.get();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        //return super.createNavigation(level);
        return new WallClimberNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.setClimbing(this.onClimbableLadder() || this.horizontalCollision);
        }

        /*
        this.soundTick = this.soundTick == 0 ? this.random.nextIntBetweenInclusive(1, 80) : this.soundTick - 1;
        if (this.soundTick == 0 && this.isAggressive()) {
            this.playLaughSound();
        }
         */
    }

    protected void playLaughSound() {
        float pitch = 0.7F + 0.4F * this.random.nextFloat();
        float volume = 0.8F + 0.2F * this.random.nextFloat();
        this.level().playLocalSound(this, TheGremlinMod.GREMLIN_LAUGH.get(), this.getSoundSource(), volume, pitch);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing() || this.onClimbableLadder();
    }

    protected boolean onClimbableLadder() {
        return super.onClimbable();
    }

    @Override
    public void finishConversion(Mob convertedFrom) {
        //this.setHatching(true);
    }

    @Override
    public boolean receivesConversion(Mob convertingMob) {
        return convertingMob.getType() == TheGremlinMod.MOGWAI_COCOON.get();
    }

    public boolean isAttacking(){
        return this.attackAnimationTicks > 0;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.setUsingAlternateAttack(this.random.nextBoolean());
        this.attackAnimationTicks = this.getMaxAttackAnimationTicks();
        this.level().broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        return super.doHurtTarget(entity);
    }

    public int getMaxAttackAnimationTicks() {
        return this.isUsingAlternateAttack() ? ATTACK_2_ANIMATION_TIME : ATTACK_1_ANIMATION_TIME;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityEvent.START_ATTACKING) {
            this.attackAnimationTicks = this.getMaxAttackAnimationTicks();
        }
        super.handleEntityEvent(id);
    }

    public boolean isUsingAlternateAttack() {
        return this.entityData.get(DATA_USE_ALTERNATE_ATTACK);
    }

    public void setUsingAlternateAttack(boolean usingAlternateAttack){
        this.entityData.set(DATA_USE_ALTERNATE_ATTACK, usingAlternateAttack);
    }

    protected void updateNoActionTime() {
        float f = this.getLightLevelDependentMagicValue();
        if (f > 0.5F) {
            this.noActionTime += 2;
        }

    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.updateWaterPathfinding();
        /*
        if(this.isPerformingSpecialAction()){
            if(!this.canWalkWhilePerformingSpecialAction()){
                this.stopAllMovement();
            }
            if(!this.canAttackWhilePerformingSpecialAction()){
                this.setTarget(null);
            }
        }
         */
    }

    protected boolean canAttackWhilePerformingSpecialAction(){
        return !this.isDuplicating();
    }

    private void updateWaterPathfinding() {
        boolean chasingPlayer = this.getTarget() instanceof Player;
        float waterPenalty = chasingPlayer ? 0.0F : -1.0F;
        this.setPathfindingMalus(PathType.WATER, waterPenalty);
        this.setPathfindingMalus(PathType.WATER_BORDER, waterPenalty);
    }

    private boolean canPursueTarget(LivingEntity target) {
        return !target.isInWaterRainOrBubble() || target instanceof Player;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return -level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    protected int getMaxDeathAnimationTime() {
        return DEATH_ANIMATION_TIME;
    }

    @Override
    protected int getSwitchIdleCooldownTime() {
        return (this.isUsingAlternateIdle() ? IDLE2_ANIMATION_TIME : IDLE1_ANIMATION_TIME) * this.random.nextInt(1, 5);
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TheGremlinMod.GREMLIN_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return TheGremlinMod.GREMLIN_DIE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isPerformingSpecialAction() ? null : this.isAggressive() ? TheGremlinMod.GREMLIN_LAUGH.get() : TheGremlinMod.GREMLIN_IDLE.get();
    }

    @Override
    protected void playDuplicateSound() {
        this.playSound(TheGremlinMod.GREMLIN_DUPLICATE.get());
    }

    @Override
    protected void playAttackSound() {
        this.playSound(TheGremlinMod.GREMLIN_ATTACK.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData spawnGroupData1 = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        this.handleAttributes();

        return spawnGroupData1;
    }

    private void handleAttributes() {
    }
}
