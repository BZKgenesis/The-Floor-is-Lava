package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.items.PopupTowerItem;
import net.bzkgns.theFloorIsLava.items.abilities.PopupTower;
import net.bzkgns.theFloorIsLava.statistics.StatisticType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PopupTowerListener implements Listener {

    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    @EventHandler
    public void onPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack blockPlaced = event.getItemInHand();

        if (new PopupTowerItem().isItem(blockPlaced)){
            plugin.getStatisticsManager().increment(player, StatisticType.POPUP_TOWERS);
            PopupTower.onPopupTowerPlaced(player,block);
        }
    }
}
