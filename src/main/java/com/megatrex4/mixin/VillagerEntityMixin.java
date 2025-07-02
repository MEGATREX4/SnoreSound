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
        MTSnoreSound.handleEntity((LivingEntity) (Object) this);
    }
}
