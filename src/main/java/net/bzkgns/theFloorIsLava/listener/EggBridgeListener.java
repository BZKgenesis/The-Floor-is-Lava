package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.items.EggBridgeItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class EggBridgeListener implements Listener {



    @EventHandler
    public void onEggBridgeLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!(egg.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!new EggBridgeItem().isItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(
                new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLava.class),"eggBridgeEntity"),
                PersistentDataType.STRING,
                "eggBridgeEntity");
    }
}
