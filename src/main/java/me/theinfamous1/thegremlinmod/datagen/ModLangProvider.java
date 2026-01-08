package me.theinfamous1.thegremlinmod.datagen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import me.theinfamous1.thegremlinmod.TheGremlinModConfig;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.Util;
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
        for(UnmodifiableConfig.Entry entry : TheGremlinModConfig.SPEC.getValues().entrySet()){
            translateConfigKey(entry);
        }
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.COCOON_GROWING.getId()), "Cocoon grows");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.COCOON_HATCHING.getId()), "Cocoon hatches");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.COCOON_IDLE.getId()), "Cocoon pulsates");


        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_ATTACK.getId()), "Gremlin attacks");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_DIE.getId()), "Gremlin dies");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_DUPLICATE.getId()), "Gremlin duplicates");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_HURT.getId()), "Gremlin hurts");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_IDLE.getId()), "Gremlin growls");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.GREMLIN_LAUGH.getId()), "Gremlin laughs");

        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_CRY.getId()), "Mogwai cries");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_DIE.getId()), "Mogwai dies");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_DUPLICATE.getId()), "Mogwai duplicates");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_HURT.getId()), "Mogwai hurts");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_IDLE.getId()), "Mogwai squeaks");
        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.MOGWAI_SLEEP.getId()), "Mogwai sleeps");


        this.add(Util.makeDescriptionId("subtitles", TheGremlinMod.SUNBEAM_IDLE.getId()), "Sunbeam hums");
    }

    private void translateConfigKey(UnmodifiableConfig.Entry entry) {
        String key = entry.getKey();
        String translationKey = TheGremlinMod.MODID + ".configuration." + key;
        this.add(translationKey, capitalizeAndSpace(key));
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
