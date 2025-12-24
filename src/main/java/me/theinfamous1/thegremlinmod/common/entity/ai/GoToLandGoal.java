package me.theinfamous1.thegremlinmod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class GoToLandGoal extends MoveToBlockGoal {
    public GoToLandGoal(PathfinderMob drowned, double speedModifier, int searchRange, int verticalSearchRange) {
        super(drowned, speedModifier, searchRange, verticalSearchRange);
    }

    @Override
    public boolean canUse() {
        return this.mob.isInWater() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.isEmptyBlock(above) && level.isEmptyBlock(above.above()) && level.getBlockState(pos).entityCanStandOn(level, pos, this.mob);
    }
}
