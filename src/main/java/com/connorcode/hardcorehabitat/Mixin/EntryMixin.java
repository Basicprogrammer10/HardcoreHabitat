package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.Util;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListS2CPacket.Entry.class)
public class EntryMixin {
    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!HardcoreHabitat.lives.containsKey(profile.getId())) HardcoreHabitat.lives.put(profile.getId(), 7);
        int lives = HardcoreHabitat.lives.get(profile.getId());
        cir.setReturnValue(Text.of(String.format("%s%s [%d] ",
                Util.colorForLives(lives), profile.getName(), lives)));
    }
}
