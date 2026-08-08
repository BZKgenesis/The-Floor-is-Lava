package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import static net.bzkgns.theFloorIsLava.utils.BlockUtils.filterProtectedBlocks;
import static net.bzkgns.theFloorIsLava.utils.BlockUtils.getWoolBlockByPlayer;

public class SpawnProtectionListener implements Listener {

    @EventHandler
    public void onPlaced(BlockPlaceEvent event){
        if (!BlockUtils.canPlaceBlock(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (block.getType().toString().endsWith("WOOL")){
            block.setType(getWoolBlockByPlayer(player));
        }
    }

    @EventHandler
    public void onBroke(BlockBreakEvent event){
        if (!BlockUtils.canPlaceBlock(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        filterProtectedBlocks(event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        filterProtectedBlocks(event.blockList());
    }
}
