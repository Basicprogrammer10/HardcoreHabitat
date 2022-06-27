package com.connorcode.hardcorehabitat.Commands;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("lives").then(CommandManager.argument("player", word())
                .suggests((c, b) -> suggestMatching(HardcoreHabitat.playerManager.getPlayerNames(), b))
                .executes(ctx -> {
                    String rawPlayer = getString(ctx, "player");
                    if (Arrays.stream(HardcoreHabitat.playerManager.getPlayerNames())
                            .noneMatch(x -> Objects.equals(x, rawPlayer))) {
                        ctx.getSource().sendError(Text.of("Invalid Player"));
                        return 0;
                    }

                    ServerPlayerEntity player = Objects.requireNonNull(
                            HardcoreHabitat.playerManager.getPlayer(rawPlayer));
                    int lives = HardcoreHabitat.lives.get(player.getUuid());

                    Objects.requireNonNull(ctx.getSource().getPlayer()).sendMessage(
                            Text.of(String.format("%s has %d remaining lives", player.getName().getString(), lives)));
                    return 0;
                })));
    }
}
