package com.megatrex4.mixin;

import com.megatrex4.MTSnoreSound;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    
    /**
     * Injects snoring sound handling into the player's tick method.
     * 
     * This method is called every tick (20 times per second) for each player.
     * It checks if the player is sleeping and delegates to the snoring sound
     * handler to manage audio playback timing and positioning.
     * 
     * @param ci Mixin callback info (unused but required by Mixin framework)
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void mtsnore$playSnore(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        MTSnoreSound.handleEntity((LivingEntity) (Object) this);
    }
}
