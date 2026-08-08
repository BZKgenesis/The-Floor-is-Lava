package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatisticListener implements Listener {
    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getStatisticsManager().load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getStatisticsManager().unload(event.getPlayer());
    }


}
