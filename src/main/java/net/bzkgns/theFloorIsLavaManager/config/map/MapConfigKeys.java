package net.bzkgns.theFloorIsLavaManager.config.map;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public class MapConfigKeys {
    private MapConfigKeys() {
    }

    public static final ConfigKey<MapConfig, Boolean> SPAWN_SPAWN_STRUCTURE =
            new ConfigKey<>(
                    "spawn-spawn-structure",
                    "config.map.spawn-spawn-structure",
                    MapConfig::isSpawnSpawnStructure,
                    MapConfig::setSpawnSpawnStructure,
                    Boolean::parseBoolean
            );
    public static final ConfigKey<MapConfig, Integer> CENTER_X =
            new ConfigKey<>(
                    "center-x",
                    "config.map.center-x",
                    MapConfig::getCenterX,
                    MapConfig::setCenterX,
                    Integer::parseInt
            );
    public static final ConfigKey<MapConfig, Integer> CENTER_Z =
            new ConfigKey<>(
                    "center-z",
                    "config.map.center-z",
                    MapConfig::getCenterZ,
                    MapConfig::setCenterZ,
                    Integer::parseInt
            );
}
