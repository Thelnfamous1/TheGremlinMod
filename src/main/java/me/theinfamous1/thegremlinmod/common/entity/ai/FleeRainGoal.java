package me.theinfamous1.thegremlinmod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;

public class FleeRainGoal extends FleeSunGoal {
    private int interval = reducedTickDelay(100);

    public FleeRainGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        if (this.interval > 0) {
            --this.interval;
            return false;
        } else {
            this.interval = 100;
            BlockPos blockpos = this.mob.blockPosition();
            return this.mob.level().isRainingAt(blockpos) && this.setWantedPos();
        }
    }
}