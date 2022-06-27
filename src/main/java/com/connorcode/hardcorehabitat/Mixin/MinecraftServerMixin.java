package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.HardcoreStateLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "setPlayerManager", at = @At("RETURN"))
    public void setPlayerManager(PlayerManager playerManager, CallbackInfo ci) {
        HardcoreHabitat.playerManager = playerManager;
    }

    @Inject(method = "save", at = @At("TAIL"))
    public void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) throws IOException {
        HardcoreStateLoader.save(HardcoreHabitat.seasonRunning);
    }
}
