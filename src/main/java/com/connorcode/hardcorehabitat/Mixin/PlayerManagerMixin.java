package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.Misc.PlayerManagerExtension;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin implements PlayerManagerExtension {
    @Shadow
    protected void savePlayerData(ServerPlayerEntity player) {

    }

    @Override
    public void savePlayer(ServerPlayerEntity player) {
        savePlayerData(player);
    }
}
