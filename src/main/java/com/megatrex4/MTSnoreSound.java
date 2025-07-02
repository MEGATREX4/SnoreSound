package com.megatrex4;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class MTSnoreSound implements ClientModInitializer {
    public static final String MOD_ID = "snoresound";
    public static final Identifier SNORE_ID = Identifier.of(MOD_ID, "snore");
    public static SoundEvent SNORE_EVENT = SoundEvent.of(SNORE_ID);
    private static final Random RANDOM = new Random();
    private static final Map<LivingEntity, Integer> NEXT_SNORE_TICK = new WeakHashMap<>();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        Registry.register(Registries.SOUND_EVENT, SNORE_ID, SNORE_EVENT);
        LOGGER.info("Registered snore sound event: {}", SNORE_ID);
    }

    /**
     * Handles snoring sound logic for a sleeping entity.
     * 
     * @param entity the living entity to check for snoring (player or villager)
     * @throws NullPointerException if entity is null
     * 
     */
    public static void handleEntity(LivingEntity entity) {
        if (!entity.getWorld().isClient()) return;

        if (entity.isSleeping()) {
            int age = entity.age;
            Integer next = NEXT_SNORE_TICK.get(entity);

            if (next == null || age >= next) {
                float pitch;
                if (entity instanceof VillagerEntity && ((VillagerEntity) entity).isBaby()) {
                    pitch = 2.0F + RANDOM.nextFloat() * 2.0F; // Small villagers: 2.0 - 4.0
                } else {
                    pitch = 0.8F + RANDOM.nextFloat() * 0.7F; // Large entities: 0.8 - 1.5
                }
                
                SoundCategory category = entity instanceof PlayerEntity
                    ? SoundCategory.PLAYERS
                    : SoundCategory.NEUTRAL;

                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    double distance = client.player.squaredDistanceTo(entity);
                    double maxDistance = 15.0;
                    double fadeStartDistance = 10.0;
                    
                    if (distance <= maxDistance * maxDistance) {
                        float volume = 1.0F;
                        
                        if (distance > fadeStartDistance * fadeStartDistance) {
                            double fadeDistance = Math.sqrt(distance) - fadeStartDistance;
                            double fadeRange = maxDistance - fadeStartDistance;
                            volume = (float) Math.max(0.1F, 1.0F - (fadeDistance / fadeRange * 0.9F));
                        }
                        
                        client.getSoundManager().play(
                            new net.minecraft.client.sound.PositionedSoundInstance(
                                SNORE_EVENT,
                                net.minecraft.sound.SoundCategory.NEUTRAL,
                                volume,
                                pitch,
                                entity.getRandom(),
                                entity.getX(),
                                entity.getY(),
                                entity.getZ()
                            )
                        );
                    } else {
                    }
                }
                
                int delay = 40 + RANDOM.nextInt(161);
                NEXT_SNORE_TICK.put(entity, age + delay);
            } else {
            }
        } else {
            NEXT_SNORE_TICK.remove(entity);
        }
    }
}