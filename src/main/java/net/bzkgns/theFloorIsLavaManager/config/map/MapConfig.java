package net.bzkgns.theFloorIsLavaManager.config.map;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;
import net.bzkgns.theFloorIsLavaManager.config.ConfigSection;

import java.util.List;

public class MapConfig  implements ConfigSection<MapConfig> {

    private boolean spawnSpawnStructure = true;
    private int centerX = 0;
    private int centerZ = 0;

    private static final List<ConfigKey<MapConfig, ?>> KEYS = List.of(
            MapConfigKeys.SPAWN_SPAWN_STRUCTURE,
            MapConfigKeys.CENTER_X,
            MapConfigKeys.CENTER_Z
    );

    // --- Getters / setters ---

    public boolean isSpawnSpawnStructure() { return spawnSpawnStructure; }
    public void setSpawnSpawnStructure(boolean v) { this.spawnSpawnStructure = v; }
    public int getCenterX() { return centerX; }
    public void setCenterX(int centerX) { this.centerX = centerX; }
    public int getCenterZ() { return centerZ; }
    public void setCenterZ(int centerZ) { this.centerZ = centerZ; }

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