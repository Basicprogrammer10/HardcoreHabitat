package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerMetadata.class)
public class ServerMetadataMixin {
    @Inject(method = "getDescription", at = @At("TAIL"), cancellable = true)
    public void getServerMotd(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(
                Text.of(String.format("JSC-Hardcore: %s", HardcoreHabitat.seasonRunning ? "RUNNING" : "ENDED")));
    }
}
