package com.connorcode.hardcorehabitat.Commands;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class TransferLifeCommand {
    public static int run(CommandContext<ServerCommandSource> ctx) {
        String rawPlayer = getString(ctx, "player");
        if (Arrays.stream(Commands.players()).noneMatch(x -> Objects.equals(x, rawPlayer))) {
            ctx.getSource().sendError(Text.of("Invalid Player"));
            return 0;
        }

        if (!HardcoreHabitat.seasonRunning) {
            ctx.getSource().sendError(Text.of("The season is over, you cannot transfer lives"));
            return 0;
        }

        ServerPlayerEntity thisPlayer = Objects.requireNonNull(ctx.getSource().getPlayer());
        int thisPlayerLives = HardcoreHabitat.lives.get(thisPlayer.getUuid());

        ServerPlayerEntity transferPlayer = Objects.requireNonNull(HardcoreHabitat.playerManager.getPlayer(rawPlayer));
        int transferPlayerLives = HardcoreHabitat.lives.get(transferPlayer.getUuid());

        return 0;
    }
}
