package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.GHAST_TEAR), RecipeCategory.MISC, TheGremlinMod.SUN_DROP.get(), 1.0F, 200).unlockedBy("has_ghast_tear", has(Items.GHAST_TEAR)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TheGremlinMod.SUNBEAM.get()).pattern("IIS").define('I', Items.IRON_INGOT).define('S', TheGremlinMod.SUN_DROP.get()).unlockedBy("has_sun_drop", has(TheGremlinMod.SUN_DROP.get())).save(recipeOutput);
    }
}
