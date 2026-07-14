package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TeamInventoryManager {

    private static TeamInventoryManager instance = null;
    private final Plugin plugin;
    private final Map<String, TeamInventory> teamInventories = new HashMap<>();

    public static TeamInventoryManager getInstance() {
        if ( instance == null ) {
            instance = new TeamInventoryManager(JavaPlugin.getPlugin(TheFloorIsLavaManager.class));
        }
        return instance;
    }

    public TeamInventoryManager(Plugin plugin){
        this.plugin = plugin;
    }
    public TeamInventory getTeamInventory(String teamName) {
        return teamInventories.computeIfAbsent(teamName,
                _ -> new TeamInventory(plugin,27));
    }

}
