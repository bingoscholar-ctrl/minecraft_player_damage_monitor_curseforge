package com.example.damageudp;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public final class DamageUdpConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable UDP JSON output")
            .define("enabled", true);

    public static final ModConfigSpec.ConfigValue<String> UDP_HOST = BUILDER
            .comment("UDP target host")
            .define("udpHost", "127.0.0.1");

    public static final ModConfigSpec.IntValue UDP_PORT = BUILDER
            .comment("UDP target port")
            .defineInRange("udpPort", 25575, 1, 65535);

    public static final ModConfigSpec.BooleanValue SURVIVAL_ONLY = BUILDER
            .comment("Only listen in survival mode")
            .define("survivalOnly", false);

    public static final ModConfigSpec.DoubleValue INTENSITY_CAP = BUILDER
            .comment("Final intensity cap")
            .defineInRange("intensityCap", 1.0D, 0.0D, 1.0D);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private DamageUdpConfig() {}

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC);
    }
}
