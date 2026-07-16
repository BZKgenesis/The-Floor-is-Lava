package net.bzkgns.theFloorIsLavaManager.config.map;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public class MapConfigKeys {
    private MapConfigKeys() {
    }

    public static final ConfigKey<MapConfig, Boolean> SPAWN_SPAWN_STRUCTURE =
            new ConfigKey<>(
                    "spawn-spawn-structure",
                    "Si la structure de spawn doit être générée au spawn",
                    MapConfig::isSpawnSpawnStructure,
                    MapConfig::setSpawnSpawnStructure,
                    Boolean::parseBoolean
            );
}
