package me.theinfamous1.thegremlinmod;

import me.theinfamous1.thegremlinmod.common.util.SunbeamDetectionMode;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class TheGremlinModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue MAX_SUNBEAM_USE_TIME = BUILDER.comment("The maximum time, in seconds, that the sunbeam can be turned on for.").defineInRange("max_sunbeam_use_time", 5, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue SUNBEAM_USE_COOLDOWN_TIME = BUILDER.comment("The time, in seconds, that the sunbeam will be put on cooldown for after maximum use time.").defineInRange("sunbeam_use_cooldown_time", 60, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue SUNBEAM_DAMAGE = BUILDER.comment("The amount of damage, in half-hearts, that the sunbeam will apply to a target.").defineInRange("sunbeam_damage", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue SUNBEAM_RANGE = BUILDER.comment("The maximum distance, in blocks, that the sunbeam can apply damage to a target.").defineInRange("sunbeam_range", 3, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.LongValue COCOON_HATCH_TIME = BUILDER.comment("The amount of time, in ticks, that it takes for a cocoon to hatch into a gremlin.").defineInRange("cocoon_hatch_time", Level.TICKS_PER_DAY, 0, Long.MAX_VALUE);
    public static final ModConfigSpec.EnumValue<SunbeamDetectionMode> SUNBEAM_DETECTION_MODE = BUILDER.comment("The logic to use for sunbeam hit detection.").defineEnum("sunbeam_detection_mode", SunbeamDetectionMode.SURROUND);
    public static final ModConfigSpec.IntValue SUNBEAM_FOCUS_ANGLE = BUILDER.comment("When the sunbeam detection mode is configured to FOCUS, use this angle to determine the size of the area in which the sunbeam detects targets.").defineInRange("sunbeam_focus_angle", 90, 0, 360);
    public static final ModConfigSpec.LongValue MOGWAI_DUPLICATION_COOLDOWN = BUILDER.comment("The amount of time, in ticks, for a mogwai to cooldown duplication after duplicating.").defineInRange("mogwai_duplication_cooldown", 600L, 0, Long.MAX_VALUE);
    public static final ModConfigSpec.LongValue GREMLIN_DUPLICATION_COOLDOWN = BUILDER.comment("The amount of time, in ticks, for a gremlin to cooldown duplication after duplicating.").defineInRange("gremlin_duplication_cooldown", 600L, 0, Long.MAX_VALUE);

    // Gremlin mining configs

    public static final ModConfigSpec.BooleanValue GREMLIN_MINE_BLOCKS = BUILDER.comment("Allow Gremlins to attempt to break blocks in their way when pursuing a target.").define("gremlin_mine_blocks", true);

    public static final ModConfigSpec.IntValue GREMLIN_MINE_PRIORITY = BUILDER.comment("The priority for a Gremlin to use its mining behavior. The lower the number, the higher the priority.").defineInRange("gremlin_mine_priority", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue GREMLIN_MIN_DIST_FOR_MINING = BUILDER.comment("Minimum distance, in blocks, for Gremlins to run their mining behavior when pursuing a target.").defineInRange("gremlin_min_dist_for_mining", 0.2, 0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue GREMLIN_MAX_DIST_FOR_MINING = BUILDER.comment("Maximum distance, in blocks, for Gremlins to run their mining behavior when pursuing a target.").defineInRange("gremlin_max_dist_for_mining", 32, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue GREMLIN_MINING_INCREMENT = BUILDER.comment("The amount of mining progress towards the block's hardness value that Gremlins can make per tick (1/20th of a second).").defineInRange("gremlin_mining_increment", 0.1, 0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue GREMLIN_MAX_BLOCK_HARDNESS = BUILDER.comment("The maximum hardness value of a block that Gremlins can mine.").defineInRange("gremlin_max_block_hardness", 12, 0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue GREMLIN_BLOCK_HARDNESS_MULTIPLIER = BUILDER.comment("The value to multiply the hardness of the block by when a Gremlin is mining it, affecting the time to break. This does not affect block selection.").defineInRange("gremlin_block_hardness_multiplier", 5, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue GREMLIN_DROP_BROKEN_BLOCKS = BUILDER.comment("Allow blocks broken by Gremlins to drop their loot.").define("gremlin_drop_broken_blocks", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
