package me.theinfamous1.thegremlinmod.common.entity.ai;

import java.util.EnumSet;

import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.goal.Goal;

public class MogwaiRestGoal extends Goal {
    public static final UniformInt MAX_SLEEP_TIME = TimeUtil.rangeOfSeconds(15, 45);
    private final Mogwai mob;
    private static final UniformInt WAIT_TIME_BEFORE_SLEEP = TimeUtil.rangeOfSeconds(30, 90);
    private int sleepCooldown;
    private int sleepTime;

    public MogwaiRestGoal(Mogwai mob) {
        super();
        this.mob = mob;
        this.sleepCooldown = this.adjustedTickDelay(WAIT_TIME_BEFORE_SLEEP.sample(this.mob.getRandom()));
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.mob.xxa == 0.0F && this.mob.yya == 0.0F && this.mob.zza == 0.0F && this.canSleep();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canSleep() && this.canContinueSleeping();
    }

    private boolean canSleep() {
        if (this.sleepCooldown > 0) {
            --this.sleepCooldown;
            return false;
        } else {
            return this.mob.getLastHurtByMob() == null && !this.mob.isInPowderSnow && this.mob.getNavigation().isDone() && this.mob.canPerformSleep();
        }
    }

    private boolean canContinueSleeping(){
        return this.sleepTime > 0;
    }

    @Override
    public void tick() {
        this.sleepTime--;
    }

    @Override
    public void stop() {
        this.sleepCooldown = this.adjustedTickDelay(WAIT_TIME_BEFORE_SLEEP.sample(this.mob.getRandom()));
        this.mob.setLying(false);
    }

    @Override
    public void start() {
        this.mob.setJumping(false);
        this.mob.setLying(true);
        this.mob.getNavigation().stop();
        this.mob.getMoveControl().setWantedPosition(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 0.0);
        this.sleepTime = this.adjustedTickDelay(MAX_SLEEP_TIME.sample(this.mob.getRandom()));
    }
}