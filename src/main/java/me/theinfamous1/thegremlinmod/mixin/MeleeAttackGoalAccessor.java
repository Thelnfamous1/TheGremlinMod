package me.theinfamous1.thegremlinmod.mixin;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MeleeAttackGoal.class)
public interface MeleeAttackGoalAccessor {

    @Accessor("ticksUntilNextAttack")
    void setTicksUntilNextAttack(int ticksUntilNextAttack);
}
