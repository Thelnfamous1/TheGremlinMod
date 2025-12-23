package me.theinfamous1.thegremlinmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.theinfamous1.thegremlinmod.common.entity.AbstractGremlin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1))
    private boolean wrap_aiStep_applyWaterDamage(LivingEntity instance, DamageSource ev, float amount, Operation<Boolean> original){
        if(instance instanceof AbstractGremlin gremlin && !gremlin.takesVanillaWaterDamage()){
            return false;
        }
        return original.call(instance, ev, amount);
    }
}
