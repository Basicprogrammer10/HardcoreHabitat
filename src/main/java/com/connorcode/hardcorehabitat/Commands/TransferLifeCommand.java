package com.connorcode.hardcorehabitat.Commands;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.Util;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.particle.ParticleTypes;
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

        // Make sure season is still running
        if (!HardcoreHabitat.seasonRunning) {
            ctx.getSource().sendError(Text.of("The season is over, you cannot transfer lives"));
            return 0;
        }

        // Get players
        ServerPlayerEntity thisPlayer = Objects.requireNonNull(ctx.getSource().getPlayer());
        int thisPlayerLives = HardcoreHabitat.lives.get(thisPlayer.getUuid());

        ServerPlayerEntity transferPlayer = Objects.requireNonNull(HardcoreHabitat.playerManager.getPlayer(rawPlayer));
        int transferPlayerLives = HardcoreHabitat.lives.get(transferPlayer.getUuid());

        // Make sure this player has enough lives
        if (thisPlayerLives <= 0) {
            ctx.getSource().sendError(Text.of("You have no lives to give!"));
            return 0;
        }

        if (transferPlayerLives >= 7) {
            ctx.getSource()
                    .sendError(Text.of(String.format("%s has all 7 lives!", transferPlayer.getName().getString())));
            return 0;
        }

        // Transfer lives
        thisPlayerLives--;
        transferPlayerLives++;
        HardcoreHabitat.lives.put(thisPlayer.getUuid(), thisPlayerLives);
        HardcoreHabitat.lives.put(transferPlayer.getUuid(), transferPlayerLives);

        // Send messages and update player list
        thisPlayer.sendMessage(
                Text.of(String.format("Transferred one life to %s", transferPlayer.getName().getString())));
        transferPlayer.sendMessage(Text.of(String.format("%s gave you a life", thisPlayer.getName().getString())));

        thisPlayer.sendMessage(Text.of(Util.genLiveCountText(thisPlayerLives)), true);
        transferPlayer.sendMessage(Text.of(Util.genLiveCountText(transferPlayerLives)), true);

        for (ServerPlayerEntity i : HardcoreHabitat.playerManager.getPlayerList())
            for (ServerPlayerEntity j : new ServerPlayerEntity[]{thisPlayer, transferPlayer})
                i.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, j));

        // Fancy effects
        thisPlayer.getWorld()
                .spawnParticles(ParticleTypes.SCRAPE, thisPlayer.getX(), thisPlayer.getY() + 1, thisPlayer.getZ(), 50,
                        0.25, 0.25, 0.375, 1);
        transferPlayer.getWorld().spawnParticles(ParticleTypes.WAX_ON, transferPlayer.getX(), transferPlayer.getY() + 1,
                transferPlayer.getZ(), 50, 0.25, 0.25, 0.375, 1);

        return 0;
    }
}
