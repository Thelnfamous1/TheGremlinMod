package me.theinfamous1.thegremlinmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.theinfamous1.thegremlinmod.common.entity.AbstractGremlin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1))
    private boolean wrap_aiStep_applyWaterDamage(LivingEntity instance, DamageSource ev, float amount, Operation<Boolean> original){
        if(instance instanceof AbstractGremlin gremlin && !gremlin.takesVanillaWaterDamage()){
            return false;
        }
        return original.call(instance, ev, amount);
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "CONSTANT", args = "floatValue=0.9", ordinal = 0))
    private float modify_sprintingSwimSlowdown(float original){
        if((LivingEntity)(Object)this instanceof AbstractGremlin abstractGremlin){
            return abstractGremlin.getSwimmingSlowdown();
        }
        return original;
    }
}
