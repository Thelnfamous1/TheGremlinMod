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
        this.add(TheGremlinMod.MOD_TAB.get().getDisplayName().getString(), "The Gremlin Mod");

    }
}
