package me.theinfamous1.thegremlinmod;

import me.theinfamous1.thegremlinmod.common.util.SunbeamDetectionMode;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
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

    public static final ModConfigSpec SPEC = BUILDER.build();
}
