package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class InfiniteWool implements Listener {

    @EventHandler
    public void onWoolPlaced(BlockPlaceEvent event) {
        InfiniteWoolItem infinite = new InfiniteWoolItem();

        if (!infinite.isItem(event.getItemInHand())) {
            return;
        }

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTask(TheFloorIsLavaManager.getInstance(), () -> player.getInventory().setItem(event.getHand(), infinite.giveItem(event.getPlayer())));
    }
}
