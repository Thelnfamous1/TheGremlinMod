package me.theinfamous1.thegremlinmod.common.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public record ItemSwitchPredicate(boolean switchValue) implements SingleComponentItemPredicate<Boolean> {
    public static final Codec<ItemSwitchPredicate> CODEC = RecordCodecBuilder.create((p_337369_) -> {
        return p_337369_.group(Codec.BOOL.fieldOf("switch").forGetter(ItemSwitchPredicate::switchValue)).apply(p_337369_, ItemSwitchPredicate::new);
    });

    @Override
    public DataComponentType<Boolean> componentType() {
        return TheGremlinMod.SWITCH.get();
    }

    @Override
    public boolean matches(ItemStack itemStack, Boolean switchValue) {
        return switchValue == this.switchValue;
    }

    public static ItemSwitchPredicate itemSwitch(boolean switchValue) {
        return new ItemSwitchPredicate(switchValue);
    }
}