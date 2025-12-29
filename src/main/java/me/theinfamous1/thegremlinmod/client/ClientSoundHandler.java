package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.client.sound.MogwaiCryingSoundInstance;
import me.theinfamous1.thegremlinmod.client.sound.MogwaiSleepingSoundInstance;
import me.theinfamous1.thegremlinmod.client.sound.SunbeamSoundInstance;
import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class ClientSoundHandler {
    public static void playSunbeamSound(LivingEntity entity){
        Minecraft.getInstance().getSoundManager().play(new SunbeamSoundInstance(entity));
    }

    public static void playMogwaiCryingSound(Mogwai mogwai){
        Minecraft.getInstance().getSoundManager().play(new MogwaiCryingSoundInstance(mogwai));
    }

    public static void playMogwaiSleepingSound(Mogwai mogwai){
        Minecraft.getInstance().getSoundManager().play(new MogwaiSleepingSoundInstance(mogwai));
    }
}
