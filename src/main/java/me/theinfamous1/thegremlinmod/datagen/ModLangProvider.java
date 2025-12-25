package me.theinfamous1.thegremlinmod.datagen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import me.theinfamous1.thegremlinmod.Config;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        for(UnmodifiableConfig.Entry entry : Config.SPEC.getValues().entrySet()){
            String key = entry.getKey();
            String translationKey = TheGremlinMod.MODID + ".configuration." + key;
            this.add(translationKey, capitalizeAndSpace(key));
        }
    }

    public static String capitalizeAndSpace(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return Arrays.stream(input.split("_"))
                .map(word -> word.isEmpty() ? word
                        : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
