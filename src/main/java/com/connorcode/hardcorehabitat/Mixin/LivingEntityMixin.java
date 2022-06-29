package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    public final void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof PlayerEntity)) return;

        int lives = 7;
        if (HardcoreHabitat.lives.containsKey(uuid)) lives = HardcoreHabitat.lives.get(self.getUuid());
        cir.setReturnValue((float) (2 * (10 - lives)));
    }
}
