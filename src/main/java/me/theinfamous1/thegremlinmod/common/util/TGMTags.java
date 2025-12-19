package me.theinfamous1.thegremlinmod.common.util;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class TGMTags {
    public static final TagKey<EntityType<?>> VULNERABLE_TO_SUNBEAM = TagKey.create(Registries.ENTITY_TYPE, TheGremlinMod.location("vulnerable_to_sunbeam"));
}
