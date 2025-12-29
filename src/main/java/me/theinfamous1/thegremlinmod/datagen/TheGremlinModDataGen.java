package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.util.TGMTags;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TheGremlinMod.MODID)
public class TheGremlinModDataGen {

    @SubscribeEvent
    static void onDataGen(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        event.addProvider(new ModLangProvider(packOutput));
        event.addProvider(new ModItemModelProvider(packOutput, existingFileHelper));
        event.addProvider(new ModEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        event.addProvider(new ModRecipeProvider(packOutput, lookupProvider));
        event.addProvider(new ModLootProvider(packOutput, lookupProvider));
        event.addProvider(new ModBiomeTagProvider(packOutput, lookupProvider, existingFileHelper));
        event.addProvider(new ModSoundsProvider(packOutput, existingFileHelper));
        RegistrySetBuilder builder = createRegistrySetBuilder();
        event.createDatapackRegistryObjects(builder);
    }

    private static RegistrySetBuilder createRegistrySetBuilder() {
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                    HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
                    context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TheGremlinMod.location("mogwai_spawns")),
                            BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                                    biomeLookup.getOrThrow(TGMTags.SPAWNS_MOGWAI),
                                    new MobSpawnSettings.SpawnerData(TheGremlinMod.MOGWAI.get(), 4, 1, 1)
                            ));
                });
        return builder;
    }

    private static CompletableFuture<HolderLookup.Provider> getRegistries(RegistrySetBuilder builder) {
        return CompletableFuture.supplyAsync(() -> createLookup(builder), Util.backgroundExecutor());
    }
    public static HolderLookup.Provider createLookup(RegistrySetBuilder builder) {
        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return builder.build(access);
    }
}
