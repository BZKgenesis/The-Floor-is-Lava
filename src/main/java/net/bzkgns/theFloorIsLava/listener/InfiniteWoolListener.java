package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.items.InfiniteWoolItem;
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

        Bukkit.getScheduler().runTask(TheFloorIsLava.getInstance(), () -> player.getInventory().setItem(event.getHand(), infinite.giveItem(event.getPlayer())));
    }
}
