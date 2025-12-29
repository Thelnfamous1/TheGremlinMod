package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSoundsProvider extends SoundDefinitionsProvider {
    protected ModSoundsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, TheGremlinMod.MODID, helper);
    }

    @Override
    public void registerSounds() {
        registerSound(TheGremlinMod.COCOON_GROWING, "cocoon_growing");
        registerSound(TheGremlinMod.COCOON_HATCHING, "cocoon_hatching");
        registerSound(TheGremlinMod.COCOON_IDLE, "cocoon_idle");


        registerSound(TheGremlinMod.GREMLIN_ATTACK, "gremlin_attack", 2);
        registerSound(TheGremlinMod.GREMLIN_DIE, "gremlin_die");
        registerSound(TheGremlinMod.GREMLIN_DUPLICATE, "gremlin_duplicate");
        registerSound(TheGremlinMod.GREMLIN_HURT, "gremlin_hurt");
        registerSound(TheGremlinMod.GREMLIN_IDLE, "gremlin_idle", 3);
        registerSound(TheGremlinMod.GREMLIN_LAUGH, "gremlin_laugh", 2);


        registerSound(TheGremlinMod.MOGWAI_CRY, "mogwai_cry");
        registerSound(TheGremlinMod.MOGWAI_DIE, "mogwai_die");
        registerSound(TheGremlinMod.MOGWAI_DUPLICATE, "mogwai_duplicate");
        registerSound(TheGremlinMod.MOGWAI_HURT, "mogwai_hurt");
        registerSound(TheGremlinMod.MOGWAI_IDLE, "mogwai_idle", 2);
        registerSound(TheGremlinMod.MOGWAI_SLEEP, "mogwai_sleep");

        registerSound(TheGremlinMod.SUNBEAM_IDLE, "sunbeam_idle");
    }

    private void registerSound(DeferredHolder<SoundEvent, SoundEvent> soundEvent, String path, int variations) {
        this.add(soundEvent, withVariations(soundEvent, TheGremlinMod.location(path), variations));
    }

    private void registerSound(DeferredHolder<SoundEvent, SoundEvent> soundEvent, String path) {
        this.add(soundEvent, SoundDefinition.definition().subtitle(Util.makeDescriptionId("subtitles", soundEvent.getId())).with(sound(TheGremlinMod.location(path))));
    }

    private static SoundDefinition withVariations(DeferredHolder<SoundEvent, SoundEvent> soundEvent, ResourceLocation soundLocation, int variations){
        SoundDefinition definition = SoundDefinition.definition().subtitle(Util.makeDescriptionId("subtitles", soundEvent.getId()));
        for(int i = 1; i <= variations; i++){
            definition.with(sound(soundLocation.withSuffix("_" + i)));
        }
        return definition;
    }
}
