package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.PopupTowerItem;
import net.bzkgns.theFloorIsLavaManager.items.abilities.PopupTower;
import net.bzkgns.theFloorIsLavaManager.statistics.StatisticType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PopupTowerListener implements Listener {

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

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
