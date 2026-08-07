# The Floor is Lava WIKI - Dev WIKI

## Commands

#### Debug Commands
- `/tfl debug dangerState`: Shows the current state of the lava rise.
- `/tfl debug gamble computeRtp`: Calculates the current RTP of the gambling system.
- `/tfl debug gameState`: Shows the current state of the game.
- `/tfl debug kit`: Lists all available kits.
- `/tfl debug kit <kit name>`: Shows the details of a specific kit.
- `/tfl debug oreCount <radius>`: Counts the number of ores within a radius around the player. Useful for tuning economy-related settings.
- `/tfl debug playerKits`: Shows the kits equipped by players.
- `/tfl debug playerStats`: Displays all player statistics (this is mainly to check that the database is working correctly, rather than to actually retrieve stats).
- `/tfl debug respawnTeam`: Lists team respawn anchors and their positions.
- `/tfl debug team`: Lists all teams and their members.

## Configuration

### Language
The plugin is available in multiple languages; the language is chosen automatically based on the player's game language.
If a message isn't available in the player's language, the plugin will use the server's default language (changeable in `game.yml`).
If a message is global and can't be translated independently of the player's language, the plugin will use the server's default language (changeable in `game.yml`).

### Game Map Management

#### Custom Map Management

Custom maps must be placed in the `plugins/TheFloorIsLava/maps/` folder.
They must be either a folder containing the map files, or a zip file containing the map files.
Maps must follow the Minecraft 26.1+ file structure.
Structure of a custom map:
```
maps/
└── <map name>/
    ├── data/
    ├── dimensions/
    │   ├── tfl/
    │   │   └── game/ (If this dimension is not found, the plugin will use the overworld by default)
    │   └── minecraft/
    │       └── overworld/ (If tfl:game is not found)
    └── mapConfig.yml (map configuration file; if not present, the plugin will use the default configuration)
```

Or alternatively, just the dimension:
```
maps/
└── <map name>/
    ├── data/
    ├── entities/
    ├── poi/
    ├── region/
    └── mapConfig.yml (map configuration file; if not present, the plugin will use the default configuration)
```

To load a custom map, operators can use the `/tfl map reset map <map name>` command.
The map's settings are then loaded from the map's `mapConfig.yml` file.
If this file isn't present, default settings are used.
Operators can still change the map's settings via the `/tfl config map set <value>` command or via the `/tfl config map gui` graphical interface.
!! WARNING !! When loading a custom map, the currently configured settings are overwritten by those defined in the new map's `mapConfig.yml`.
It is therefore recommended to save the map settings before loading a custom map.

#### Random Map Management

To generate a random map, operators can use the `/tfl map reset random [seed]` command. If no seed is provided, a random seed will be generated.
The map's settings are then loaded from the `defaultMapConfig.yml` file. Operators can still change the map's settings via the `/tfl config map set <value>` command or via the `/tfl config map gui` graphical interface.

A randomly generated map isn't necessarily playable (for example, if the map is generated in an ocean, there is no spawn point). It is therefore recommended to check the map before starting a game, using the `/tfl map preview` command. This command teleports the operator to the generated map in spectator mode and lets them check whether the map is playable. Operators can also use the `/tfl map setCenter [<x> <z>]` command to change the map's center if it isn't optimal.
By default, the game's center is generated at position (0, 0) on the map.

## Tools

Tools are available for developers and operators to make configuring and developing the plugin easier.
These tools are Python scripts available on the plugin's GitHub repository.

### Installation

To use the tools, you first need to clone the GitHub repository.

Navigate to the folder where you want to clone the repository and run the following command:
```bash
git clone https://github.com/BZKgenesis/The-Floor-is-Lava.git
```

Create a Python virtual environment and activate it:
```bash
python -m venv .venv
source .venv/bin/activate  # On Linux/Mac
.venv\Scripts\activate  # On Windows
```

Install the required dependencies:
```bash
pip install -r requirements.txt
```

### Usage

#### Gambling Simulation Tool

The gambling simulation script is a Python module.
To open the graphical interface, run the following command:
```bash
python -m scripts.gambling_main gui
```

#### Missing Translation Detection Tool

The missing translation detection script scans the entire project using regex rules to look for translation keys that are missing from the language configuration files.
This tool isn't foolproof, but it can catch most missing translations.

```bash
python -m scripts.lang_scan
```

The script prints the number of missing translation keys and writes them to a `translation_candidates.txt` file in the project's `scripts/` folder.
