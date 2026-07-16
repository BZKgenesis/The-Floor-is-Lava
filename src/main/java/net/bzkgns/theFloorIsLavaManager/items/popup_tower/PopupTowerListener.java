package net.bzkgns.theFloorIsLavaManager.items.popup_tower;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PopupTowerListener implements Listener {


    @EventHandler
    public void onPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack blockPlaced = event.getItemInHand();

        if (new PopupTowerItem().isItem(blockPlaced)){
            PopupTower.onPopupTowerPlaced(player,block);
        }
    }
}
