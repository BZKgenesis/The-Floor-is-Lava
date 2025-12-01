package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TeamInventoryManager {

    private static TeamInventoryManager instance = null;
    private final Plugin plugin;
    private final Map<String, TeamInventory> teamInventories = new HashMap<>();

    public static TeamInventoryManager getInstance() {
        if ( instance == null ) {
            instance = new TeamInventoryManager(JavaPlugin.getPlugin(TheFloorIsLavaManager.class));
        }
        return instance;
    }

    public TeamInventoryManager(Plugin plugin){
        this.plugin = plugin;
    }
    public TeamInventory getTeamInventory(String teamName) {
        return teamInventories.computeIfAbsent(teamName,
                k -> new TeamInventory(plugin,27));
    }

    public ItemStack getTeamInventoryItem(){
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"teamInv"), PersistentDataType.STRING, "teamInv");
        item.setItemMeta(meta);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Portail d'inventaire d'équipe"));
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return item;
    }

    public boolean isTeamInventoryItem(ItemStack stack){
        if (stack.getType() == Material.ENDER_CHEST){
            if (stack.getPersistentDataContainer().has(new NamespacedKey(plugin,"teamInv"))){
                return Objects.equals(stack.getPersistentDataContainer().get(new NamespacedKey(plugin, "teamInv"), PersistentDataType.STRING), "teamInv");
            }
        }
        return false;
    }

}
