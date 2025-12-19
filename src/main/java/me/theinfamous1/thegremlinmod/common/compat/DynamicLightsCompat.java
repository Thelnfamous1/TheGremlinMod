package me.theinfamous1.thegremlinmod.common.compat;

import com.google.common.base.Suppliers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSource;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import dev.lambdaurora.lambdynlights.api.item.ItemLuminance;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.criterion.ItemSwitchPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;

import java.util.function.Supplier;

public class DynamicLightsCompat implements DynamicLightsInitializer {

    public static final Supplier<ItemLightSource> SUNBEAM = Suppliers.memoize(() -> new ItemLightSource(
            ItemPredicate.Builder.item().of(TheGremlinMod.SUNBEAM.get()).withSubPredicate(TheGremlinMod.SWITCH_PREDICATE.get(), new ItemSwitchPredicate(true)).build(), new ItemLuminance.Value(15), true));

    @Override
    public void onInitializeDynamicLights(
            DynamicLightsContext context
    ) {
        // Code related to dynamic lighting here.
        context.itemLightSourceManager().onRegisterEvent().register(c -> c.register(SUNBEAM.get()));
    }

    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {

    }
}
