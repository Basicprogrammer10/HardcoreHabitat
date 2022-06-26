package com.connorcode.hardcorehabitat.Mixin;

import com.connorcode.hardcorehabitat.HardcoreHabitat;
import com.connorcode.hardcorehabitat.ServerPlayerEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityExtension {
    @Override
    public int getLives() {
        return getLives(this);
    }

    private int getLives(ServerPlayerEntityMixin e) {
        UUID uuid = ((ServerPlayerEntity) (Object) e).getUuid();
        if (!HardcoreHabitat.lives.containsKey(uuid)) HardcoreHabitat.lives.put(uuid, 7);
        return HardcoreHabitat.lives.get(uuid);
    }

    private void setLives(ServerPlayerEntityMixin e, int lives) {
        HardcoreHabitat.lives.put(((ServerPlayerEntity) (Object) e).getUuid(), lives);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Lives", getLives(this));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Lives")) HardcoreHabitat.lives.put(((Entity) (Object) this).getUuid(), nbt.getInt("Lives"));
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        int lives = getLives(this);
        if (lives > 0) {
            setLives(this, lives - 1);
            ((PlayerEntity) (Object) this).sendMessage(Text.of(String.format("You now have %d lives!", lives - 1)),
                    true);
            return;
        }

        HardcoreHabitat.seasonRunning = false;
        ((ServerPlayerEntity) (Object) this).changeGameMode(GameMode.SPECTATOR);
        ((PlayerEntity) (Object) this).sendMessage(Text.of("Welp,, thats all for this season!"), true);
    }
}
