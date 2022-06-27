package com.connorcode.hardcorehabitat.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").executes(ctx -> {
            ServerPlayerEntity player = Objects.requireNonNull(ctx.getSource().getPlayer());
            player.sendMessage(Text.of("You have %d remaining lives"));
            return 1;
        }));
    }
}
