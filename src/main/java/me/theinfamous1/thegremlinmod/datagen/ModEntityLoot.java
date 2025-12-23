package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class ModEntityLoot extends EntityLootSubProvider {
    public ModEntityLoot(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(TheGremlinMod.MOGWAI.get(), LootTable.lootTable());
        this.add(TheGremlinMod.MOGWAI_COCOON.get(), LootTable.lootTable());
        this.add(TheGremlinMod.GREMLIN.get(), LootTable.lootTable());
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return TheGremlinMod.ENTITY_TYPES.getEntries().stream().map(DeferredHolder::get);
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> p_249029_) {
        return true;
    }
}
