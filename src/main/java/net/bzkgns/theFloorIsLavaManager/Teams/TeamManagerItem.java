package net.bzkgns.theFloorIsLavaManager.Teams;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TeamManagerItem {
    public static boolean isTeamManagerItem(ItemStack stack){
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
        if (stack.getType() == Material.PAPER){
            if(stack.getPersistentDataContainer().has(new NamespacedKey(plugin, "teamManager"))){
                return Objects.equals(stack.getPersistentDataContainer().get(new NamespacedKey(plugin, "teamManager"), PersistentDataType.STRING), "teamManager");
            }
        }
        return false;

    }

    public static ItemStack giveTeamManagerItem(){
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
        ItemStack teamManagerStack = new ItemStack(Material.PAPER);
        teamManagerStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        teamManagerStack.setData(DataComponentTypes.ITEM_NAME, Component.text("Gestion des équipes"));
        ItemMeta popupTowerMeta = teamManagerStack.getItemMeta();
        popupTowerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "teamManager"), PersistentDataType.STRING, "teamManager");
        teamManagerStack.setItemMeta(popupTowerMeta);

        return teamManagerStack;

    }
}
