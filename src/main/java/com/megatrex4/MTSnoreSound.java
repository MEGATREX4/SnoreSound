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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MT Snore Sound Mod - Adds realistic snoring sounds for sleeping players and villagers.
 * 
 * This client-side mod enhances the game experience by playing spatial snoring sounds
 * when entities are sleeping. Child villagers produce higher-pitched snores while adults
 * produce deeper, lower-pitched snores. The sound volume decreases based on distance
 * from the player, creating a realistic audio experience.
 * 
 * Features:
 * - Spatial audio positioning (sounds come from entity locations)
 * - Distance-based volume control (audible up to 10 blocks)
 * - Age-based pitch variation (children vs adults)
 * - Random timing intervals (2-10 seconds between snores)
 *
 */
@Environment(EnvType.CLIENT)
public class MTSnoreSound implements ClientModInitializer {
    
    // Constants
    public static final String MOD_ID = "snoresound";
    public static final Identifier SNORE_ID = Identifier.of(MOD_ID, "snore");
    public static SoundEvent SNORE_EVENT = SoundEvent.of(SNORE_ID);
    
    // Audio configuration constants
    private static final float CHILD_MIN_PITCH = 1.5F;
    private static final float CHILD_MAX_PITCH = 3.0F;
    private static final float ADULT_MIN_PITCH = 0.8F;
    private static final float ADULT_MAX_PITCH = 1.3F;
    private static final double MAX_HEARING_DISTANCE = 10.0;
    private static final double VOLUME_FADE_START_DISTANCE = 5.0;
    private static final float MIN_VOLUME = 0.1F;
    private static final int MIN_DELAY_TICKS = 40; // 2 seconds
    private static final int MAX_ADDITIONAL_DELAY_TICKS = 161; // Up to 8 additional seconds
    
    // Instance variables
    private static final Random RANDOM = new Random();
    private static final Map<LivingEntity, Integer> NEXT_SNORE_TICK = new WeakHashMap<>();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Initializes the client-side mod by registering the custom snore sound event.
     * This method is called when the client starts up.
     */
    @Override
    public void onInitializeClient() {
        Registry.register(Registries.SOUND_EVENT, SNORE_ID, SNORE_EVENT);
        LOGGER.info("Registered snore sound event: {}", SNORE_ID);
    }

    /**
     * Handles snoring logic for a sleeping entity.
     * 
     * This method checks if an entity is sleeping and plays snoring sounds at random intervals.
     * The sound characteristics (pitch, volume) depend on the entity type and player distance.
     * 
     * @param entity The living entity to check for sleeping state and play snoring sounds
     */
    public static void handleEntity(LivingEntity entity) {
        if (!entity.getWorld().isClient()) return;

        if (entity.isSleeping()) {
            processSleepingEntity(entity);
        } else {
            // Remove entity from tracking when not sleeping
            NEXT_SNORE_TICK.remove(entity);
        }
    }
    
    /**
     * Processes a sleeping entity and determines if it should play a snoring sound.
     * 
     * @param entity The sleeping entity to process
     */
    private static void processSleepingEntity(LivingEntity entity) {
        int currentAge = entity.age;
        Integer nextSnoreAge = NEXT_SNORE_TICK.get(entity);

        if (shouldPlaySnore(currentAge, nextSnoreAge)) {
            playSnoreSound(entity);
            scheduleNextSnore(entity, currentAge);
        }
    }
    
    /**
     * Determines if a snoring sound should be played based on timing.
     * 
     * @param currentAge The entity's current age in ticks
     * @param nextSnoreAge The scheduled age for the next snore, or null if not scheduled
     * @return true if a snore should be played now
     */
    private static boolean shouldPlaySnore(int currentAge, Integer nextSnoreAge) {
        return nextSnoreAge == null || currentAge >= nextSnoreAge;
    }
    
    /**
     * Plays a snoring sound for the given entity with appropriate pitch and volume.
     * 
     * The sound is played using spatial audio positioning, with volume based on distance
     * from the player. Child villagers get higher pitch, adults get lower pitch.
     * 
     * @param entity The entity that should produce the snoring sound
     */
    private static void playSnoreSound(LivingEntity entity) {
        float pitch = calculatePitch(entity);
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            double distanceSquared = client.player.squaredDistanceTo(entity);
            
            if (isWithinHearingRange(distanceSquared)) {
                float volume = calculateVolumeByDistance(distanceSquared);
                playPositionedSound(entity, pitch, volume);
            }
        }
    }
    
    /**
     * Calculates the appropriate pitch for the snoring sound based on entity type and age.
     * 
     * @param entity The entity to calculate pitch for
     * @return The pitch value (higher for children, lower for adults)
     */
    private static float calculatePitch(LivingEntity entity) {
        if (entity instanceof VillagerEntity && ((VillagerEntity) entity).isBaby()) {
            // Child villagers: higher pitch (1.5-3.0)
            return CHILD_MIN_PITCH + RANDOM.nextFloat() * (CHILD_MAX_PITCH - CHILD_MIN_PITCH);
        } else {
            // Adult entities: lower pitch (0.8-1.3)
            return ADULT_MIN_PITCH + RANDOM.nextFloat() * (ADULT_MAX_PITCH - ADULT_MIN_PITCH);
        }
    }
    
    /**
     * Checks if the entity is within hearing range of the player.
     * 
     * @param distanceSquared The squared distance between player and entity
     * @return true if the entity is within the maximum hearing distance
     */
    private static boolean isWithinHearingRange(double distanceSquared) {
        return distanceSquared <= MAX_HEARING_DISTANCE * MAX_HEARING_DISTANCE;
    }
    
    /**
     * Calculates the volume based on distance from the player.
     * 
     * Volume remains at maximum (1.0) until the fade start distance, then gradually
     * decreases to minimum volume at maximum hearing distance.
     * 
     * @param distanceSquared The squared distance between player and entity
     * @return The calculated volume (between MIN_VOLUME and 1.0)
     */
    private static float calculateVolumeByDistance(double distanceSquared) {
        if (distanceSquared <= VOLUME_FADE_START_DISTANCE * VOLUME_FADE_START_DISTANCE) {
            return 1.0F;
        }
        
        double distance = Math.sqrt(distanceSquared);
        double fadeDistance = distance - VOLUME_FADE_START_DISTANCE;
        double fadeRange = MAX_HEARING_DISTANCE - VOLUME_FADE_START_DISTANCE;
        
        return (float) Math.max(MIN_VOLUME, 1.0F - (fadeDistance / fadeRange * (1.0F - MIN_VOLUME)));
    }
    
    /**
     * Plays a positioned sound instance at the entity's location.
     * 
     * @param entity The entity at whose location to play the sound
     * @param pitch The pitch of the sound
     * @param volume The volume of the sound
     */
    private static void playPositionedSound(LivingEntity entity, float pitch, float volume) {
        MinecraftClient client = MinecraftClient.getInstance();
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
    }
    
    /**
     * Schedules the next snoring sound for the entity.
     * 
     * The delay is randomized between 2-10 seconds to create natural variation.
     * 
     * @param entity The entity to schedule the next snore for
     * @param currentAge The entity's current age in ticks
     */
    private static void scheduleNextSnore(LivingEntity entity, int currentAge) {
        int delay = MIN_DELAY_TICKS + RANDOM.nextInt(MAX_ADDITIONAL_DELAY_TICKS);
        NEXT_SNORE_TICK.put(entity, currentAge + delay);
    }
}
