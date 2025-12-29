package me.theinfamous1.thegremlinmod.client.sound;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class MogwaiCryingSoundInstance extends AbstractGremlinSoundInstance<Mogwai> {

    public MogwaiCryingSoundInstance(Mogwai mogwai) {
        super(TheGremlinMod.MOGWAI_CRY.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom(), mogwai);
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
    }

    @Override
    protected boolean shouldPlaySound() {
        return this.gremlin.isCrying();
    }
}