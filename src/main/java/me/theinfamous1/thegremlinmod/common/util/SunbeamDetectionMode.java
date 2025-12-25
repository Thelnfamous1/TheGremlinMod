package me.theinfamous1.thegremlinmod.common.util;

import net.minecraft.util.StringRepresentable;

public enum SunbeamDetectionMode implements StringRepresentable {
    PINPOINT("pinpoint"),
    FOCUSED("focused"),
    SURROUND("surround");

    private final String key;
    SunbeamDetectionMode(String key) {
        this.key = key;
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }
}
