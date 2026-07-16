package net.bzkgns.theFloorIsLavaManager.config.map;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;
import net.bzkgns.theFloorIsLavaManager.config.ConfigSection;

import java.util.List;

public class MapConfig  implements ConfigSection<MapConfig> {

    private boolean spawnSpawnStructure = true;

    private static final List<ConfigKey<MapConfig, ?>> KEYS = List.of(
            MapConfigKeys.SPAWN_SPAWN_STRUCTURE
    );

    // --- Getters / setters ---

    public boolean isSpawnSpawnStructure() { return spawnSpawnStructure; }
    public void setSpawnSpawnStructure(boolean v) { this.spawnSpawnStructure = v; }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public List<ConfigKey<MapConfig,?>> getKeys() {
        return List.copyOf(KEYS);
    }

    @SuppressWarnings("unused")
    public Object getValueForKey(ConfigKey<MapConfig, ?> key) {
        return key.get(this);
    }
}