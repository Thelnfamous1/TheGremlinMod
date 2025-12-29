package me.theinfamous1.thegremlinmod.client.sound;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.item.SunbeamItem;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.LivingEntity;

public class SunbeamSoundInstance extends AbstractTickableSoundInstance {
    protected static final float VOLUME = 1.0F;
    protected static final float PITCH = 1.0F;
    protected final LivingEntity user;

    public SunbeamSoundInstance(LivingEntity user) {
        super(TheGremlinMod.SUNBEAM_IDLE.get(), user.getSoundSource(), SoundInstance.createUnseededRandom());
        this.user = user;
        this.looping = true;
        this.delay = 0;
    }

    @Override
    public boolean canPlaySound() {
        return !this.user.isSilent();
    }

    @Override
    public void tick() {
        if (!this.user.isRemoved() && this.user.isHolding(item -> item.is(TheGremlinMod.SUNBEAM.get()) && SunbeamItem.getSwitchValue(item))) {
            this.x = (float)this.user.getX();
            this.y = (float)this.user.getY();
            this.z = (float)this.user.getZ();
            this.volume = VOLUME;
            this.pitch = PITCH;
        } else {
            this.stop();
        }

    }
}
