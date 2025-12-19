package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheGremlinMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(TheGremlinMod.SUN_DROP.get());
        ResourceLocation sunbeamId = TheGremlinMod.SUNBEAM.getId();
        ResourceLocation sunbeamOff = sunbeamId.withSuffix("_off");
        /*
        ResourceLocation sunbeamOn = sunbeamId.withSuffix("_on");
        ItemModelBuilder sunbeamModel = this.withExistingParent(sunbeamId.getPath(), sunbeamOff).texture("0", sunbeamOff.withPrefix("item/"));
        ItemModelBuilder sunbeamOnModel = this.withExistingParent(sunbeamOn.getPath(), sunbeamOff).texture("0", sunbeamOn.withPrefix("item/"));
        sunbeamModel.override().predicate(TheGremlinModClient.SWITCH_PROPERTY, 1.0F).model(sunbeamOnModel);
         */
        this.withExistingParent(sunbeamId.getPath(), sunbeamOff).texture("0", sunbeamOff.withPrefix("item/"));
    }
}
