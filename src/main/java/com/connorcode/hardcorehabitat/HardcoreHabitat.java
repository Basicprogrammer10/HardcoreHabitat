package com.connorcode.hardcorehabitat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HardcoreHabitat implements ModInitializer {
    public static HashMap<UUID, Integer> lives = new HashMap<>();
    public static boolean seasonRunning = true;
    public static HashMap<UUID, ArrayList<Runner>> playerRespawnMessageQueue = new HashMap<>();
    public static ArrayList<UUID> joinedPlayersCache = new ArrayList<>();

    public static ServerPropertiesHandler properties;

    @Override
    public void onInitialize() {
    }
}
