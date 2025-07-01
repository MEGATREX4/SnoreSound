# MT Snore Sound Mod

A Minecraft Fabric mod that adds realistic snoring sounds for sleeping players and villagers.

## Features

- **Spatial Audio**: Snoring sounds are positioned at the sleeping entity's location, creating realistic 3D audio
- **Distance-Based Volume**: Sound volume fades naturally with distance (audible up to 10 blocks)
- **Age-Based Pitch Variation**: 
  - Child villagers produce higher-pitched snores (1.5-3.0 pitch)
  - Adult players and villagers produce deeper snores (0.8-1.3 pitch)
- **Random Timing**: Snores occur at random intervals between 2-10 seconds for natural variation
- **Client-Side Only**: No server installation required

## Installation

1. **Prerequisites**: 
   - Minecraft 1.21+
   - Fabric Loader
   - Fabric API

2. **Download**: Get the latest release from the releases page

3. **Install**: Place the `.jar` file in your `mods` folder

4. **Enjoy**: Sleep near villagers or other players to hear the snoring sounds!

## How It Works

The mod uses Fabric mixins to inject snoring behavior into player and villager entities. When an entity is sleeping, the mod:

- Tracks each sleeping entity individually
- Plays snoring sounds at random intervals
- Adjusts pitch based on the entity's age (child vs adult)
- Calculates volume based on distance from the player
- Uses Minecraft's spatial audio system for realistic positioning

## Technical Details

- **Mod ID**: `snoresound`
- **Environment**: Client-side only
- **Sound Category**: Neutral
- **Hearing Range**: 0-10 blocks (full volume at 0-5 blocks, fades to 10% at 10 blocks)

## Sound File

The mod includes a custom snoring sound file located at:
`assets/snoresound/sounds/snore.ogg`

## Development

Built with:
- Fabric Loader
- Fabric API
- Minecraft 1.21.1
- Java 21

## Contributing

Feel free to submit issues and enhancement requests!
