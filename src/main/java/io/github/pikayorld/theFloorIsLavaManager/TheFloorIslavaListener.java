package io.github.pikayorld.theFloorIsLavaManager;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class TheFloorIslavaListener implements Listener {

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Material newType = event.getNewState().getType();

        // Empêche l'eau/lave de créer de l'obsidienne ou du cobble
        if (newType == Material.OBSIDIAN || newType == Material.COBBLESTONE || newType == Material.STONE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        World world = Bukkit.getWorld("world");
        if (event.getPlayer().getStatistic(Statistic.TOTAL_WORLD_TIME) < 100){
            event.getPlayer().teleport(new Location(world, 0.5, 281, 0.5));
        }
    }
}
