package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    public void getDeathMessage(CallbackInfoReturnable<Text> cir) {
        if (!(this.entity instanceof PlayerEntity)) return;

        int lives = HardcoreHabitat.lives.get(entity.getUuid());
        if (lives < 1) return;

        cir.setReturnValue(Text.of(String.format("%s, %d remaining %s", cir.getReturnValue()
                        .getString(), lives,
                lives == 1 ? "life" : "lives")));
    }
}
