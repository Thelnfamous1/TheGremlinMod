package me.theinfamous1.thegremlinmod.common.entity;

import net.minecraft.world.entity.Mob;

public interface GremlinConvert {
    void finishConversion(Mob convertedFrom);

    boolean receivesConversion(Mob convertingMob);
}
