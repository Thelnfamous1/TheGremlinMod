package me.theinfamous1.thegremlinmod.common.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;

public class RestrictRainGoal extends RestrictSunGoal {
    private final PathfinderMob hostMob;
    public RestrictRainGoal(PathfinderMob hostMob) {
        super(hostMob);
        this.hostMob = hostMob;
    }

    @Override
    public boolean canUse() {
        return this.hostMob.level().isRainingAt(this.hostMob.blockPosition()) && GoalUtils.hasGroundPathNavigation(this.hostMob);
    }
}
