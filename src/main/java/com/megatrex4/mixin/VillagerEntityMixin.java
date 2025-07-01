package com.megatrex4.mixin;

import com.megatrex4.MTSnoreSound;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for VillagerEntity to add snoring sound functionality.
 * 
 * This mixin injects into the villager's tick method to check if the villager
 * is sleeping and handle snoring sounds accordingly. The injection occurs at
 * the end of the tick method to avoid interfering with normal villager behavior.
 * 
 * The snoring system tracks each villager individually and plays sounds at
 * random intervals when they are sleeping, with different pitch ranges for
 * children versus adults.
 * 
 * @see MTSnoreSound#handleEntity(LivingEntity)
 */
@Environment(EnvType.CLIENT)
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    
    /**
     * Injects snoring sound handling into the villager's tick method.
     * 
     * This method is called every tick (20 times per second) for each villager.
     * It checks if the villager is sleeping and delegates to the snoring sound
     * handler to manage audio playback timing and positioning.
     * 
     * @param ci Mixin callback info (unused but required by Mixin framework)
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void mtsnore$playSnore(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (villager.isSleeping()) {
            MTSnoreSound.LOGGER.debug("Villager is sleeping, calling handleEntity");
        }
        MTSnoreSound.handleEntity((LivingEntity) (Object) this);
    }
}
