package me.theinfamous1.thegremlinmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Shadow @Final protected static int SLOT_FUEL;

    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;ILnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)Z", ordinal = 0))
    private static boolean wrap_canBurn_serverTick(RegistryAccess registryAccess, RecipeHolder<?> recipeHolder, NonNullList<ItemStack> inventory, int maxStackSize, AbstractFurnaceBlockEntity furnaceBlockEntity, Operation<Boolean> original){
        if(recipeHolder != null && recipeHolder.id().equals(TheGremlinMod.SUN_DROP_RECIPE_LOCATION)){
            return inventory.get(SLOT_FUEL).is(Items.LAVA_BUCKET);
        }
        return original.call(registryAccess, recipeHolder, inventory, maxStackSize, furnaceBlockEntity);
    }
}
