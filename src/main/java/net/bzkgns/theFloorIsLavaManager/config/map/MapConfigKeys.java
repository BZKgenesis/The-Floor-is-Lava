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
    public static final ConfigKey<MapConfig, Integer> CENTER_X =
            new ConfigKey<>(
                    "center-x",
                    "Coordonnée X du centre de la map",
                    MapConfig::getCenterX,
                    MapConfig::setCenterX,
                    Integer::parseInt
            );
    public static final ConfigKey<MapConfig, Integer> CENTER_Z =
            new ConfigKey<>(
                    "center-z",
                    "Coordonnée Z du centre de la map",
                    MapConfig::getCenterZ,
                    MapConfig::setCenterZ,
                    Integer::parseInt
            );
}
