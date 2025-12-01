package io.github.pikayorld.theFloorIsLavaManager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
        if (event.getPlayer().getStatistic(Statistic.TOTAL_WORLD_TIME) < 100 && world != null){
            Location spawnPos = new Location (world,0.5,281,0.5);
            event.getPlayer().teleport(spawnPos);
        }

        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"batte"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"eggBridge"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"patate"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"blocs_en_plus"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"fireball"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"ciseaux"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"enderPearl"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"popupTower"));
    }

    @EventHandler
    public void onPlaced(BlockPlaceEvent event){
        Player p = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (block.getType().toString().endsWith("WOOL")){
            block.setType(getWoolBlockByPlayer(p));
        }
        if (PopupTower.isPopupTower(event.getItemInHand())){
            Rotation rotation = Rotation.NONE;
            float angle =p.getYaw()+180;
            if (angle<=45 || angle>=315){
                rotation = Rotation.NONE;
            } else if (angle>=45 && angle<=135) {
                rotation = Rotation.CLOCKWISE;
            } else if (angle>=135 && angle<=225) {
                rotation = Rotation.FLIPPED;
            }else if (angle>=225 && angle<=315) {
                rotation = Rotation.COUNTER_CLOCKWISE;
            }
            PopupTower.placePopupTower(p,block.getLocation(),rotation);
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player victime && e.getDamageSource().getCausingEntity()!=null){
            if (e.getDamageSource().getCausingEntity() instanceof Player aggresseur){
                if (!TheFloorIsLavaManager.pvp){
                    e.setCancelled(true);
                }
            }
        }
    }
}
