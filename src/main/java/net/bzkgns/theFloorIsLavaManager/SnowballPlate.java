package net.bzkgns.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SnowballPlate {
    public static ItemStack giveSnowballPlate(){
        ItemStack stack = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class),"snowballPlate"), PersistentDataType.STRING, "snowballPlate");
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.text("Snowball Plate"));
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static boolean isSnowballPlateItem(ItemStack stack){
        if (stack.getType() == Material.SNOWBALL){
            ItemMeta meta = stack.getItemMeta();
            return Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "snowballPlate"), PersistentDataType.STRING), "snowballPlate");
        }
        return false;
    }
}
