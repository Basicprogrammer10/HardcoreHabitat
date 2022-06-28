package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.Util;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Mixin(DragonEggBlock.class)
public class DragonEggBlockMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
               CallbackInfoReturnable<ActionResult> cir) throws IOException {
        BlockState lowerBlockState = world.getBlockState(pos.down());
        if (lowerBlockState.getBlock() != Blocks.END_ROD) return;

        // Explosion particle and sound
        ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 50, 0.5, 0.5,
                0.5, 1);
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
                25, 1, true);

        // Reset lives of online players
        HardcoreHabitat.lives.replaceAll((i, v) -> 7);

        // Save all player-data
        HardcoreHabitat.playerManager.saveAllPlayerData();

        // Send message and update player list
        for (ServerPlayerEntity i : HardcoreHabitat.playerManager.getPlayerList()) {
            i.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, i));
            i.sendMessage(Text.of(Util.genLiveCountText(7)), true);
        }

        // Reset lives of offline players
        Path worldFolder = FabricLoader.getInstance().getGameDir().resolve(HardcoreHabitat.properties.levelName);
        File playerDataDir = new File(worldFolder + File.separator + WorldSavePath.PLAYERDATA);
        for (File i : Objects.requireNonNull(playerDataDir.listFiles())) {
            // Continue if not dat file
            if (!i.getName().endsWith(".dat")) continue;

            // Continue if player is online
            UUID fileUuid = UUID.fromString(i.getName().split("\\.")[0]);
            if (HardcoreHabitat.lives.containsKey(fileUuid)) continue;
            System.out.println(fileUuid);

            // Load and edit player-data
            NbtCompound playerData = Objects.requireNonNull(NbtIo.readCompressed(i));
            System.out.println(playerData);
            playerData.putInt("Lives", 7);
            System.out.println(playerData);

            // Write new player data
            File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", playerDataDir);
            NbtIo.writeCompressed(playerData, file);
            net.minecraft.util.Util.backupAndReplace(new File(playerDataDir, player.getUuidAsString() + ".dat"), file,
                    new File(playerDataDir, player.getUuidAsString() + ".dat_old"));
        }

        // Cancel teleport
        cir.setReturnValue(ActionResult.success(world.isClient));
    }
}
