# The Floor is Lava WIKI
Welcome to the wiki for The Floor is Lava plugin. Here you'll find all the available information about the plugin, its features, and how to use it.

The Floor is Lava is a competitive minigame where players must gather resources before the lava gradually starts to rise.
Teams must quickly build, buy items from the shop, and eliminate their opponents while surviving the rising lava.

## Installation

The plugin is compatible with Minecraft servers (26.1+) running Paper. To install it, follow these steps:
1. Download the plugin's JAR file from the official page or the GitHub repository.
2. Place the JAR file in your Minecraft server's `plugins` folder.
3. Restart your server so the plugin loads.

> You can add the [TreeFeller](https://modrinth.com/plugin/thizzyz-tree-feller) plugin to chop trees faster and gather more resources.

## Features

- Multiplayer game management with teams. (See the [Team Management](#team-management) section)
- Progressive lava rising system. (See the [Game Flow](#game-flow) section)
- Resource and economy management for buying items. (See the [Economy](#economy) section)
- Betting system for players. (See the [Gambling](#gambling) section)
- Custom and random map management. (See the [Game Map Management](#game-map-management) section)
- Statistics system for each player. (See the [Statistics](#statistics) section)
- Easy configuration via YAML files and in-game commands. (See the [Configuration](#configuration) section)

## Team Management

Team management is done entirely in-game using the team management item.
Players can create teams, join existing teams, and manage their team members.

## Commands

### Direct Commands
- `/shop`: Opens the in-game shop.
- `/kit choose <kit name>`: Lets you choose a starter kit.

### Player Commands
- `/tfl team`: Opens the team management menu.

### Operator Commands

#### Game Management
- `/tfl start`: Starts a game.
- `/tfl stop`: Completely stops the current game; all players are teleported to the lobby.
- `/tfl earlyRise`: Triggers the lava rise before the preparation time ends.
- `/tfl pause`: Pauses the game; the lava rise is halted. (DEPRECATED)
- `/tfl resume`: Resumes the game after a pause. (DEPRECATED)
- `/tfl setLevel <layer>`: Sets the lava level. (DEPRECATED)
- `/tfl getLevel`: Shows the current lava level. (DEPRECATED)
- `/tfl setSpeed <numTicks>`: Sets the speed of the lava rise. (DEPRECATED)
- `/tfl getSpeed`: Shows the current speed of the lava rise. (DEPRECATED)

#### Map Management
- `/tfl map reset map <map name>`: Loads a custom map.
- `/tfl map reset random [seed]`: Generates a random map.
- `/tfl map preview`: Lets you preview the generated map in spectator mode.
- `/tfl map setCenter [<x> <z>]`: Sets the center of the generated map.

#### Configuration Commands
- `/tfl config <section> set <key> <value>`: Changes a configuration setting.
- `/tfl config <section> get <key>`: Shows the current value of a configuration setting.
- `/tfl config <section> gui`: Opens the configuration graphical interface.
- `/tfl config <section> save`: Saves the configuration settings to the corresponding file.
- `/tfl config <section> list`: Lists all available configuration settings for a section.

## Configuration

### Game Settings

No configuration is required to use the plugin.
However, you can customize certain aspects of the game by editing the configuration files located in the `plugins/TheFloorIsLava/` folder.
Available configuration files:
- `game.yml`: Contains settings related to the flow of a game.
- `danger.yml`: Contains settings related to the lava rise.
- `items.yml`: Contains settings related to the items available in the game.
- `shop.yml`: Contains settings related to item sale and purchase prices.
- `gambling.yml`: Contains settings related to the odds and payouts of the gambling item (see the [Gambling](#gambling) section) (A dedicated tool is available to simulate and configure these settings; see [Tools](#gambling-simulation-tool)).
- `defaultMapConfig.yml`: Contains settings related to the default map configuration.

Each numeric setting can be changed in-game via the `/tfl config <section> set <value>` command.
An interface is also available for configuring settings more intuitively via the `/tfl config <section> gui` command.

> [!CAUTION]
> Setting changes only take effect until the next server restart. To make changes permanent, you must save the configuration via the `/tfl config <section> save` command, which will overwrite the corresponding configuration file.

### Shop Settings
You can add purchasable items by adding lines to the `shop.yml` file using the following format:
```yaml
shop:
  buyable_items:
    vanilla:
    - id: "<item name in uppercase snake case>"
      material: <price in materials>
      resource: <price in resources>
      quantity: <quantity of item purchased>
```

You can add sellable items by adding lines to the `shop.yml` file using the following format:
```yaml
shop:
  sellable_items:
  - id: "<item name in uppercase snake case>"
    material: <price in materials>
    resource: <price in resources>
```

The in-game settings interface does not allow adding or removing items, so you'll need to edit the `shop.yml` file to do that.
However, you can change the prices of existing items via the in-game configuration interface (don't forget to save your changes with `/tfl config shop save` to make them permanent).

### Language
The plugin is available in multiple languages; the language is chosen automatically based on the player's game language.
If a message isn't available in the player's language, the plugin will use the server's default language (changeable in `game.yml`).
If a message is global and can't be translated independently of the player's language, the plugin will use the server's default language (changeable in `game.yml`).

### Game Map Management

#### Custom Map Management

Custom maps must be placed in the `plugins/TheFloorIsLava/maps/` folder.

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

## Game Flow

### Lobby
Before a game starts, players spawn in a dedicated lobby dimension.
During this time, players can try out the items available in the game, choose their kit, and form teams.
Players are in survival mode, but can fly.

### Starting a Game
Before starting a game, operators can load a map via the `/tfl map reset map <map name>` command (see the [custom map management](#custom-map-management) section), or generate a random map via the `/tfl map reset random [seed]` command (see the [random map generation](#random-map-management) section).

Once the map is loaded, operators can start the game via the `/tfl start` command. A countdown then begins, players are teleported onto the map, and the game starts.

### Preparation

During the preparation phase, players have a limited amount of time to get ready before the lava starts rising.
This is when players need to gather resources to buy items, build structures to protect themselves from the lava, and prepare to survive as long as possible.

### Lava Rise

Once the preparation phase ends, the lava starts rising gradually. Players take damage as soon as they find themselves below the lava level, even if they aren't in contact with it.

### End of Game

If only one team remains alive, the game ends and that team is declared the winner. If all players are dead, the game ends without a winner.
For a team to be eliminated, it must no longer have a respawn anchor (see available items) and there must be no players left alive.

After a certain amount of time, all players are teleported back to the lobby. Operators can start a new game by resetting the map (see the [custom map management](#custom-map-management) section) or by generating a new random map (see the [game map management](#game-map-management) section).

## Shop

The shop is accessible via the `/shop` command or via the shop item available in the lobby. The shop allows you to buy items and sell resources and materials (see the [Economy](#economy) section).

### Purchasable Items Available

Several items are available; they can be found in the lobby and purchased with resources gathered during the preparation phase.
Operators can give themselves items via the `/tfl give <item>` command.

Table of available items:

| Item Name | ID (used to obtain them via command, or to edit their translation) | Description | Material Price ([economy](#economy)) | Resource Price ([economy](#economy)) |
|--------------------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------:|----------------------------------------:|
| Baseball Bat | `batte` | An enchanted baseball bat with knockback to push other players away | 0 | 45 |
| Shears | `shears` | A pair of shears to cut wool blocks faster | 10 | 20 |
| Egg Bridge | `egg_bridge` | An egg that creates a wool bridge along its path (somewhat inspired by Hypixel's Bed Wars) | 30 | 45 |
| Pop-up Tower | `popup_tower` | A wool tower that appears when the block is placed (also somewhat inspired by Hypixel's Bed Wars) | 45 | 25 |
| Snowball Platform | `snowball_plate` | A wool platform that appears where the snowball lands | 35 | 15 |
| Team Respawn Anchor | `team_respawn_anchor` | A team respawn point. As long as a team has a respawn anchor, the team cannot be eliminated. A team can only have one respawn anchor at a time. If another respawn anchor is placed, the old one will be destroyed. If the respawn anchor ends up below the lava level, it will be destroyed. Opposing players can destroy the respawn point. | 100 | 150 |
| Infinite Wool | `infinite_wool` | Wool that can be placed infinitely. Wool isn't a complex resource; its usefulness mainly comes from the inventory space it saves. | 60 | 20 |
| Feather Falling Boots | `feather_falling_boots` | A pair of boots that reduces gravity and fall damage. Very useful for getting around. It's a cheap item since it's meant to be bought by everyone to give them the same movement capabilities. | 10 | 35 |
| Fireball | `fireball` | A fireball that can be thrown. It doesn't damage allies but causes a large knockback, handy for fast vertical movement. | 20 | 45 |
| TNT | `tnt` | A block of TNT that activates immediately and can be redirected by hitting it. The TNT block has an initial vertical velocity so it can quickly be hit in the desired direction. | 25 | 35 |
| Parachute | `parachute` | A single-use parachute that resets the player's speed and slows their fall for a certain period. | 12 | 18 |
| Healing Camp | `heal_camp` | A temporary healing camp that regenerates the health of allied players within a small area, and which can be destroyed by opposing players. A team can have multiple healing camps. It's the only way to effectively regenerate health outside of golden apples and suspicious stew. | 40 | 90 |
| Gambling | `gambling` | Opens the gambling menu; unlimited uses. Be careful not to overuse it, as you can quickly go broke. For more information on gambling, see the [Gambling](#gambling) section | 50 | 50 |
| Throwable Iron Golem | `throwable_iron_golem` | A temporary iron golem that can be thrown; the golem attacks all enemy players within a certain radius. The golem takes damage every tick until it disappears. | 50 | 50 |
| Team Inventory | `team_inventory` | A portable team inventory. Very handy for sharing resources. Each player must buy it individually to gain access to it. | 80 | 70 |

### Sellable Resources and Materials

The price shown is the unit price; players can sell several resources or materials at once.

| Item Name | Material Price ([economy](#economy)) | Resource Price ([economy](#economy)) |
|-------------------|---------------------------------------:|----------------------------------------:|
| `oak_planks` | 2 | |
| `birch_planks` | 2 | |
| `dark_oak_planks` | 2 | |
| `spruce_planks` | 2 | |
| `jungle_planks` | 2 | |
| `acacia_planks` | 2 | |
| `mangrove_planks` | 5 | |
| `bamboo_planks` | 5 | |
| `cherry_planks` | 7 | |
| `pale_oak_planks` | 10 | |
| `cobblestone` | 1 | |
| `dirt` | | 1 |
| `sand` | | 1 |
| `red_sand` | | 1 |
| `granite` | 3 | |
| `diorite` | 3 | |
| `andesite` | 3 | |
| `coal` | | 2 |
| `copper_ingot` | | 3 |
| `iron_ingot` | | 5 |
| `redstone` | | 2 |
| `lapis_lazuli` | | 3 |
| `gold_ingot` | | 8 |
| `diamond` | | 15 |
| `emerald` | | 50 |

## Economy

The game's economy consists of 2 currencies: Resources and Materials.

Resources are tied to ores; they are the most valuable currency.
Materials are tied to building blocks.
Each item has a price consisting of a certain amount of resources and materials.
The more an item is tied to building, the more expensive it is in materials.
The more an item is tied to combat, the more expensive it is in resources.

Currencies are individual; each player has their own amount of resources and materials.

## Gambling

The gambling odds and payouts are configurable in the `gambling.yml` file and are available in-game to all players through the gambling menu.

The base RTP (Return to Player) is set to 116%, meaning that for every 100 resources wagered, a player can expect to get back an average of 116 resources.
Over the course of multiple bets, the odds are set so that a player comes out ahead 40% of the time after 100 consecutive bets, wagering 10% of their resources each time.

## Statistics

Certain statistics are recorded for each player.

WIP
