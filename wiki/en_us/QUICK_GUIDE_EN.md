<p align="center">
<img src="https://cdn.modrinth.com/data/cached_images/9d3eb8d2fa3cc2c46fb6252af693e34df405f88b.png" alt="drawing" width="350"/>
</p>

The Floor is Lava is a competitive Paper minigame where teams must survive as the lava gradually rises.

Gather resources, sell them for two different currencies, buy special items from the shop, and eliminate the other teams before they are swallowed by the rising lava.

## Features

* Fully configurable rising lava system.
* Complete team management system.
* Shop with an economy based on Resources and Materials.
* Starter kits.
* Custom maps or randomly generated maps.
* Configurable gambling system.
* Player statistics.
* Automatic translation based on each player's game language.

## Installation

1. Download the plugin.
2. Place the `.jar` file in your server's `plugins` folder.
3. Start your Paper 1.21.6+ (26.1+) server.

> **Recommended:** The TreeFeller plugin lets players cut down trees much faster, making the early game more dynamic.

## Quick Start

### 1. Generate a map

```text
/tfl map reset random
```

or load a custom map

```text
/tfl map reset map <name>
```

### 2. Start the game

```text
/tfl start
```

### 3. Play

During the preparation phase:

* Gather resources.
* Sell them in the shop (`/shop`).
* Buy items.
* Build your base.
* Place your team respawn anchor.

Once the lava starts rising, survive as long as possible while eliminating the other teams.

The last team still alive wins the game.

## Main Commands

### Players

```text
/shop
/tfl team
/kit choose <kit>
```

### Administrators

```text
/tfl start
/tfl stop
/tfl map reset random
/tfl map reset map <name>
/tfl map preview
```

All configuration commands are available in the full documentation on the GitHub repository.

## Configuration

The plugin works out of the box with no configuration required.

All settings (preparation time, lava rising speed, shop, items, gambling probabilities, etc.) can be configured through YAML files or directly in-game using the `/tfl config` commands.

## Documentation

The wiki includes:

* Detailed configuration.
* Creating custom maps.
* The complete item list.
* Economy settings.
* Development tools.
* Complete command documentation.

## Use of AI

The project's architecture, design choices, features, algorithms, and game logic were conceived and developed by the project author. AI was used as an assistance tool, not as the designer of the plugin.

Artificial intelligence tools were used during the development of this project to assist with certain tasks, including:

- correcting spelling and grammar mistakes
- rephrasing texts
- translation
- assistance with writing documentation
- help with debugging and code correction
- implementation of some isolated classes (for example, `ConfigGUI`)
- code autocompletion
- explanations and answers to questions regarding the Paper API