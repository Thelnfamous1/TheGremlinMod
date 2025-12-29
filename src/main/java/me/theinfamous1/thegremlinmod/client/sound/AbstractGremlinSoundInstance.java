package me.theinfamous1.thegremlinmod.client.sound;

import me.theinfamous1.thegremlinmod.common.entity.AbstractGremlin;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class AbstractGremlinSoundInstance<T extends AbstractGremlin> extends AbstractTickableSoundInstance {
    protected static final float VOLUME = 1.0F;
    protected static final float PITCH = 1.0F;
    protected final T gremlin;

    public AbstractGremlinSoundInstance(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource, T gremlin) {
        super(soundEvent, soundSource, randomSource);
        this.gremlin = gremlin;
    }

    @Override
    public boolean canPlaySound() {
        return !this.gremlin.isSilent();
    }

    @Override
    public void tick() {
        if (!this.gremlin.isRemoved() && this.shouldPlaySound()) {
            this.x = (float)this.gremlin.getX();
            this.y = (float)this.gremlin.getY();
            this.z = (float)this.gremlin.getZ();
            this.volume = VOLUME;
            this.pitch = PITCH;
        } else {
            this.stop();
        }

    }

    protected abstract boolean shouldPlaySound();
}
