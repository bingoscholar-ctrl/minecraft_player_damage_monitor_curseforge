package com.example.damageudp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@EventBusSubscriber(modid = PlayerDamageUdpMod.MOD_ID)
public final class DamageEventHandler {
    private DamageEventHandler() {}

    public static void register() {
        // 保持空实现：类加载后 @EventBusSubscriber 自动注册。
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!DamageUdpConfig.ENABLED.get()) {
            return;
        }

        GameType gameType = player.gameMode.getGameModeForPlayer();
        if (!isSupportedMode(gameType, DamageUdpConfig.SURVIVAL_ONLY.get())) {
            return;
        }

        double damagePoints = Math.max(0.0D, event.getNewDamage());
        double damageHearts = damagePoints / 2.0D;
        double baseIntensity = clamp(damagePoints / 20.0D, 0.0D, 1.0D);
        double intensity = Math.min(baseIntensity, DamageUdpConfig.INTENSITY_CAP.get());

        String source = event.getSource().type().msgId();
        String worldId = ((ServerLevel) player.level()).dimension().location().toString();

        String json = "{"
                + "\"event\":\"player_damage\"," 
                + "\"player\":\"" + escape(player.getGameProfile().getName()) + "\"," 
                + "\"uuid\":\"" + player.getUUID() + "\"," 
                + "\"damage_points\":" + fmt(damagePoints) + ","
                + "\"damage_hearts\":" + fmt(damageHearts) + ","
                + "\"intensity\":" + fmt(intensity) + ","
                + "\"damage_source\":\"" + escape(source) + "\"," 
                + "\"game_mode\":\"" + gameType.getName() + "\"," 
                + "\"world\":\"" + escape(worldId) + "\"," 
                + "\"timestamp_ms\":" + Instant.now().toEpochMilli()
                + "}";

        sendUdp(json);
    }

    private static boolean isSupportedMode(GameType gameType, boolean survivalOnly) {
        if (survivalOnly) {
            return gameType == GameType.SURVIVAL;
        }
        return gameType == GameType.SURVIVAL || gameType == GameType.ADVENTURE;
    }

    private static void sendUdp(String payload) {
        try {
            InetAddress host = InetAddress.getByName(DamageUdpConfig.UDP_HOST.get());
            byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, DamageUdpConfig.UDP_PORT.get());
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(packet);
            }
        } catch (Exception e) {
            PlayerDamageUdpMod.LOGGER.debug("UDP send failed", e);
        }
    }

    private static String fmt(double value) {
        return String.format(java.util.Locale.ROOT, "%.4f", value);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
