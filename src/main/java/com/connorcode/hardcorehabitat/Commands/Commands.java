package com.connorcode.hardcorehabitat.Commands;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        // Lives command
        dispatcher.register(CommandManager.literal("lives")
                .then(CommandManager.argument("player", word()).suggests((c, b) -> suggestMatching(players(), b))
                        .executes(LivesCommand::run)));

        // Transfer life command
        dispatcher.register(CommandManager.literal("transfer-life").then(CommandManager.argument("player", word())
                .suggests((c, b) -> suggestMatching(Arrays.stream(players()).filter(x -> !Objects.equals(x,
                        Objects.requireNonNull(c.getSource().getPlayer()).getName().getString())), b))
                .executes(TransferLifeCommand::run)));
    }

    static String[] players() {
        return HardcoreHabitat.playerManager.getPlayerNames();
    }
}
