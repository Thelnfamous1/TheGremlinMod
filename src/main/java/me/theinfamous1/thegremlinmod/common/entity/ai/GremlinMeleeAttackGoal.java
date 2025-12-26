package me.theinfamous1.thegremlinmod.common.entity.ai;

import me.theinfamous1.thegremlinmod.common.entity.Gremlin;
import me.theinfamous1.thegremlinmod.mixin.MeleeAttackGoalAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GremlinMeleeAttackGoal extends MeleeAttackGoal {
    private final Gremlin gremlin;

    public GremlinMeleeAttackGoal(Gremlin mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.gremlin = mob;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(target);
            this.resetAttackCooldown();
        }
    }

    @Override
    protected void resetAttackCooldown() {
        ((MeleeAttackGoalAccessor)this).setTicksUntilNextAttack(this.gremlin.getMaxAttackAnimationTicks());
    }
}
