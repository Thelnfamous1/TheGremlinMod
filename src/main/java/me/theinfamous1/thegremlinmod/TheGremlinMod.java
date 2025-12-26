package me.theinfamous1.thegremlinmod;

import com.mojang.serialization.Codec;
import me.theinfamous1.thegremlinmod.common.criterion.ItemSwitchPredicate;
import me.theinfamous1.thegremlinmod.common.entity.Gremlin;
import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import me.theinfamous1.thegremlinmod.common.entity.MogwaiCocoon;
import me.theinfamous1.thegremlinmod.common.item.SunbeamItem;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TheGremlinMod.MODID)
public class TheGremlinMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "thegremlinmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SWITCH = DATA_COMPONENTS.registerComponentType("switch", booleanBuilder -> booleanBuilder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TIMER = DATA_COMPONENTS.registerComponentType("timer", booleanBuilder -> booleanBuilder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    // Create a Deferred Register to hold Items which will all be registered under the "thegremlinmod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "thegremlinmod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> SUN_DROP = ITEMS.registerSimpleItem("sun_drop", new Item.Properties());
    public static final DeferredItem<Item> SUNBEAM = ITEMS.registerItem("sunbeam", p -> new SunbeamItem(p.stacksTo(1).component(SWITCH, false).component(TIMER, 0)));

    public static final DeferredRegister<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATES = DeferredRegister.create(Registries.ITEM_SUB_PREDICATE_TYPE, MODID);
    public static final DeferredHolder<ItemSubPredicate.Type<?>, ItemSubPredicate.Type<ItemSwitchPredicate>> SWITCH_PREDICATE = ITEM_SUB_PREDICATES.register("switch", () -> new ItemSubPredicate.Type<>(ItemSwitchPredicate.CODEC));

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Mogwai>> MOGWAI = ENTITY_TYPES.register("mogwai", () ->
            EntityType.Builder.of(Mogwai::new, MobCategory.CREATURE)
                    .sized(pixelsToBlocks(8.0F), pixelsToBlocks(17.5F))
                    .build(location("mogwai").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<MogwaiCocoon>> MOGWAI_COCOON = ENTITY_TYPES.register("mogwai_cocoon", () ->
            EntityType.Builder.of(MogwaiCocoon::new, MobCategory.CREATURE)
                    .sized(pixelsToBlocks(14.0F), pixelsToBlocks(21.5F))
                    .fireImmune()
                    .build(location("mogwai_cocoon").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Gremlin>> GREMLIN = ENTITY_TYPES.register("gremlin", () ->
            EntityType.Builder.of(Gremlin::new, MobCategory.MONSTER)
                    .sized(pixelsToBlocks(7.0F), pixelsToBlocks(22.5F))
                    .build(location("gremlin").toString()));

    private static float pixelsToBlocks(float pixels){
        return pixels / 16.0F;
    }


    public static final DeferredItem<Item> MOGWAI_SPAWN_EGG = ITEMS.registerItem("mogwai_spawn_egg", p -> new DeferredSpawnEggItem(MOGWAI, 0xcacaca, 0x7a602a, p));
    public static final DeferredItem<Item> MOGWAI_COCOON_SPAWN_EGG = ITEMS.registerItem("mogwai_cocoon_spawn_egg", p -> new DeferredSpawnEggItem(MOGWAI_COCOON, 0x263e00, 0x406802, p));
    public static final DeferredItem<Item> GREMLIN_SPAWN_EGG = ITEMS.registerItem("gremlin_spawn_egg", p -> new DeferredSpawnEggItem(GREMLIN, 0x1f3200, 0xe52e07, p));


    // Creates a creative tab with the id "thegremlinmod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thegremlinmod")) //The language key for the title of your CreativeModeTab
            .icon(() -> SUN_DROP.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SUN_DROP.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(SUNBEAM.get());
                output.accept(MOGWAI_SPAWN_EGG.get());
                output.accept(MOGWAI_COCOON_SPAWN_EGG.get());
                output.accept(GREMLIN_SPAWN_EGG.get());
            }).build());
    public static final ResourceLocation SUN_DROP_RECIPE_LOCATION = location("sun_drop");

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TheGremlinMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityAttributeCreation);
        modEventBus.addListener(this::onEntitySpawnPlacements);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ITEM_SUB_PREDICATES.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TheGremlinMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation location(String path){
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
    }

    private void entityAttributeCreation(EntityAttributeCreationEvent event){
        event.put(MOGWAI.get(), Mogwai.createAttributes().build());
        event.put(MOGWAI_COCOON.get(), MogwaiCocoon.createAttributes().build());
        event.put(GREMLIN.get(), Gremlin.createAttributes().build());
    }

    private void onEntitySpawnPlacements(RegisterSpawnPlacementsEvent event){
        event.register(MOGWAI.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mogwai::checkCustomSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MOGWAI_COCOON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MogwaiCocoon::checkCustomSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GREMLIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Gremlin::checkCustomSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }
}
