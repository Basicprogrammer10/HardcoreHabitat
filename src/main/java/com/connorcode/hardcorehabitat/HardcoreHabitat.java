package com.connorcode.hardcorehabitat;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;

import java.util.HashMap;
import java.util.UUID;

public class HardcoreHabitat implements ModInitializer {
    public static HashMap<UUID, Integer> lives = new HashMap<>();
    public static boolean seasonRunning = true;

    public static ServerPropertiesHandler properties;

    @Override
    public void onInitialize() {
    }
}
