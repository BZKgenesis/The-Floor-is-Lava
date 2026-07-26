package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.InfiniteWoolItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class InfiniteWoolListener implements Listener {

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
