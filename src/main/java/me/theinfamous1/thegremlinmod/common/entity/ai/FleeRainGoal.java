package me.theinfamous1.thegremlinmod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;

public class FleeRainGoal extends FleeSunGoal {

    public FleeRainGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        BlockPos blockPos = this.mob.blockPosition();
        return this.mob.level().isRainingAt(blockPos) && this.setWantedPos();
    }
}