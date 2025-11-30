package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static io.github.pikayorld.theFloorIsLavaManager.BlockColorUtils.getWoolBlockByPlayer;


public class TheFloorIslavaListener implements Listener {

    private final Plugin plugin;

    public TheFloorIslavaListener(Plugin plugin) {
        this.plugin = plugin;
    }

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

        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"batte"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"eggBridge"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"patate"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"blocs_en_plus"));
    }

    @EventHandler
    public void onPlaced(BlockPlaceEvent event){
        Player p = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (block.getType().toString().endsWith("WOOL")){
            block.setType(getWoolBlockByPlayer(p));
        }
    }

    @EventHandler
    public void noPickup(PlayerAttemptPickupItemEvent e){
        Item item = e.getItem();
        ItemStack stack = item.getItemStack();
        if (stack.getType().toString().endsWith("WOOL")){
            item.setItemStack(new ItemStack(Material.LIGHT_GRAY_WOOL, stack.getAmount()));
        }
    }
}
