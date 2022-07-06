package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(ServerMetadata.class)
public class ServerMetadataMixin {
    private static final Random rand = new Random();
    private static final List<String> motds = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("motds.txt")))).lines()
            .toList();


    @Inject(method = "getDescription", at = @At("TAIL"), cancellable = true)
    public void getServerMotd(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(Text.of(String.format("§7JSC-Hardcore: %s\n§a> %s",
                HardcoreHabitat.seasonRunning ? "§aRUNNING" : "§cENDED", motds.get(rand.nextInt(motds.size())))));
    }
}
