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

/**
 * Mixin for PlayerEntity to add snoring sound functionality.
 * 
 * This mixin injects into the player's tick method to check if the player
 * is sleeping and handle snoring sounds accordingly. The injection occurs at
 * the end of the tick method to avoid interfering with normal player behavior.
 * 
 * Players use the adult pitch range (0.8-1.3) regardless of their actual size,
 * as Minecraft players don't have a traditional "child" state like villagers do.
 * The snoring system treats all players as adults for audio purposes.
 * 
 * @see MTSnoreSound#handleEntity(LivingEntity)
 */
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
        if (player.isSleeping()) {
            MTSnoreSound.LOGGER.debug("Player {} is sleeping, calling handleEntity", player.getName().getString());
        }
        MTSnoreSound.handleEntity((LivingEntity) (Object) this);
    }
}
