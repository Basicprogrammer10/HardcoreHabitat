package com.connorcode.hardcorehabitat.Commands;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class LivesCommand {
    public static int run(CommandContext<ServerCommandSource> ctx) {
        String rawPlayer = getString(ctx, "player");
        if (Arrays.stream(Commands.players())
                .noneMatch(x -> Objects.equals(x, rawPlayer))) {
            ctx.getSource()
                    .sendError(Text.of("Invalid Player"));
            return 0;
        }

        ServerPlayerEntity player = Objects.requireNonNull(HardcoreHabitat.playerManager.getPlayer(rawPlayer));
        int lives = HardcoreHabitat.lives.get(player.getUuid());

        Objects.requireNonNull(ctx.getSource()
                        .getPlayer())
                .sendMessage(Text.of(String.format("%s has %d remaining lives", player.getName()
                        .getString(), lives)));
        return 0;
    }
}
