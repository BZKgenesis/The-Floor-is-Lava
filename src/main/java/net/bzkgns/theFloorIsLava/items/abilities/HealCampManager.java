package net.bzkgns.theFloorIsLava.items.abilities;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import org.bukkit.Location;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HealCampManager {
    private static HealCampManager instance;
    private final ConcurrentMap<Location, HealCampInstance> activeHealCamps = new ConcurrentHashMap<>();
    private int healCampTaskId = -1;

    private HealCampManager() {}

    public void registerHealCampTask(){
        if (healCampTaskId == -1) {
            healCampTaskId = org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    TheFloorIsLava.getInstance(),
                    this::tickHealCamps,
                    1, // Initial delay (1 tick)
                    1  // Repeat interval (1 tick)
            );
        }

    }

    public static HealCampManager getInstance() {
        if (instance == null) {
            instance = new HealCampManager();
        }
        return instance;
    }

    public void addHealCamp(Location location, HealCampInstance healCampData) {
        activeHealCamps.put(location, healCampData);
    }

    public void removeHealCamp(Location location) {
        HealCampInstance healCampData = activeHealCamps.remove(location);
        if (healCampData != null) {
            healCampData.remove();
        }
    }

    private void tickHealCamps() {
        activeHealCamps.forEach((location, healCampData) -> {
            healCampData.tick();
            if (!healCampData.isAlive())
                removeHealCamp(location);
        });
    }

}
