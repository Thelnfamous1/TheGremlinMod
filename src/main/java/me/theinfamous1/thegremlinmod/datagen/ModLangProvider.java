package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, TheGremlinMod.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(TheGremlinMod.SUN_DROP.get(), "Sun Drop");
        this.add(TheGremlinMod.SUNBEAM.get(), "Sunbeam");
        this.add(TheGremlinMod.MOD_TAB.get().getDisplayName().getString(), "The Gremlin Mod");
        this.add(TheGremlinMod.MOGWAI.get(), "Mogwai");
        this.add(TheGremlinMod.MOGWAI_COCOON.get(), "Mogwai Cocoon");
        this.add(TheGremlinMod.GREMLIN.get(), "Gremlin");
        this.add(TheGremlinMod.MOGWAI_SPAWN_EGG.get(), "Mogwai Spawn Egg");
        this.add(TheGremlinMod.MOGWAI_COCOON_SPAWN_EGG.get(), "Mogwai Cocoon Spawn Egg");
        this.add(TheGremlinMod.GREMLIN_SPAWN_EGG.get(), "Gremlin Spawn Egg");
    }
}
