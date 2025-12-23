package me.theinfamous1.thegremlinmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.theinfamous1.thegremlinmod.common.entity.GremlinConvert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "convertTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean inject_convertTo_pre_addFreshEntity(Level instance, Entity entity, Operation<Boolean> original){
        if(entity instanceof GremlinConvert convertedTo && convertedTo.receivesConversion((Mob)(Object)this)){
            convertedTo.finishConversion((Mob)(Object)this);
        }
        return original.call(instance, entity);
    }
}
