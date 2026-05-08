package com.example.damageudp;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(PlayerDamageUdpMod.MOD_ID)
public class PlayerDamageUdpMod {
    public static final String MOD_ID = "player_damage_udp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PlayerDamageUdpMod(IEventBus modEventBus, ModContainer modContainer) {
        DamageUdpConfig.register(modContainer);
        DamageEventHandler.register();
        LOGGER.info("{} loaded", MOD_ID);
    }
}
