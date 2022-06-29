package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.Misc.PlayerManagerExtension;
import com.connorcode.hardcorehabitat.Misc.Runner;
import com.connorcode.hardcorehabitat.Misc.ServerPlayerEntityExtension;
import com.connorcode.hardcorehabitat.Util;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityExtension {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract void sendMessage(Text message);

    @Override
    public int getLives() {
        return getLives(this);
    }

    private int getLives(ServerPlayerEntityMixin e) {
        UUID uuid = ((ServerPlayerEntity) (Object) e).getUuid();
        return HardcoreHabitat.lives.get(uuid);
    }

    private void setLives(ServerPlayerEntityMixin e, int lives) {
        HardcoreHabitat.lives.put(((ServerPlayerEntity) (Object) e).getUuid(), lives);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Lives", getLives(this));

        ServerPlayerEntity self = ((ServerPlayerEntity) (Object) this);
        if (self.isDisconnected()) HardcoreHabitat.lives.remove(self.getUuid());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Lives")) setLives(this, nbt.getInt("Lives"));
        else setLives(this, 7);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity self = ((ServerPlayerEntity) (Object) this);
        int lives = getLives(this);

        if (!HardcoreHabitat.playerRespawnMessageQueue.containsKey(self.getUuid()))
            HardcoreHabitat.playerRespawnMessageQueue.put(self.getUuid(), new ArrayList<>());

        if (lives > 0) {
            lives--;
            setLives(this, lives);
            Objects.requireNonNull(self.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH))
                    .setBaseValue(2 * (10 - lives));
            ((PlayerManagerExtension) server.getPlayerManager()).savePlayer(self);

            System.out.printf("LIVES: %d\n", lives);
            System.out.println(HardcoreHabitat.lives);

            for (ServerPlayerEntity i : self.server.getPlayerManager().getPlayerList())
                i.networkHandler.sendPacket(
                        new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, self));

            int finalLives = lives;
            HardcoreHabitat.playerRespawnMessageQueue.get(self.getUuid())
                    .add(() -> self.sendMessage(Text.of(Util.genLiveCountText(finalLives)), true));
            HardcoreHabitat.playerRespawnMessageQueue.get(self.getUuid()).add(() -> self.networkHandler.sendPacket(
                    new HealthUpdateS2CPacket(2f * (10 - finalLives), 20, 5)));
            return;
        }

        HardcoreHabitat.seasonRunning = false;
        for (ServerPlayerEntity i : self.server.getPlayerManager().getPlayerList()) {
            i.changeGameMode(GameMode.SPECTATOR);
            if (i != self) i.networkHandler.sendPacket(new TitleS2CPacket(Text.of("§cSeason Over")));
            i.sendMessage(Text.of("§cSeason over"));
        }
        HardcoreHabitat.playerRespawnMessageQueue.get(self.getUuid())
                .add(() -> self.networkHandler.sendPacket(new TitleS2CPacket(Text.of("§cSeason Over"))));
    }


    @Inject(method = "onSpawn", at = @At("TAIL"))
    public void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity self = ((ServerPlayerEntity) (Object) this);

        // Make player list header
        int maxPlayerName = Arrays.stream(Objects.requireNonNull(self.getServer()).getPlayerNames()).map(String::length)
                .max(Integer::compareTo).orElse(0);
        String spaces = " ".repeat(Math.round((Math.max(0, (12 - maxPlayerName)) / 2f)));
        for (ServerPlayerEntity i : self.getServer().getPlayerManager().getPlayerList())
            i.networkHandler.sendPacket(
                    new PlayerListHeaderS2CPacket(Text.of(String.format("\n%s §nJSC-Hardcore§r %s\n", spaces, spaces)),
                            Text.empty()));

        // If player is new, Send a welcome message
        PlayerManager playerManager = Objects.requireNonNull(self.getServer()).getPlayerManager();
        if (HardcoreHabitat.seasonRunning && playerManager.loadPlayerData(
                self) == null && !HardcoreHabitat.joinedPlayersCache.contains(self.getUuid())) {
            self.networkHandler.sendPacket(new TitleS2CPacket(Text.of(null)));
            self.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("Welcome to JSC-Hardcore!")));
            HardcoreHabitat.joinedPlayersCache.add(self.getUuid());
        }

        // If player has respawn queue items run them
        if (HardcoreHabitat.playerRespawnMessageQueue.containsKey(self.getUuid())) {
            ArrayList<Runner> toRun = HardcoreHabitat.playerRespawnMessageQueue.get(self.getUuid());
            for (Runner i : toRun) i.run();
            toRun.clear();
        }

        System.out.println(HardcoreHabitat.lives);
        int lives = HardcoreHabitat.lives.get(self.getUuid());
        Objects.requireNonNull(self.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH))
                .setBaseValue(2 * (10 - lives));

        // If the season is over and the player is in survival mode, put them in spectator
        if (HardcoreHabitat.seasonRunning || !self.interactionManager.isSurvivalLike()) return;
        self.networkHandler.sendPacket(new TitleS2CPacket(Text.of(null)));
        self.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("§cThe season has ended")));
        self.changeGameMode(GameMode.SPECTATOR);
    }
}
