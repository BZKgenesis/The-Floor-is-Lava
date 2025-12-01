package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EggBridge {
    public static ItemStack giveEggBridgeItem(){
        ItemStack stack = new ItemStack(Material.EGG);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class),"eggBridge"), PersistentDataType.STRING, "eggBridge");
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.text("Egg Bridge"));
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static boolean isEggBridgeItem(ItemStack stack){
        if (stack.getType() == Material.EGG){
            ItemMeta meta = stack.getItemMeta();
            return Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "eggBridge"), PersistentDataType.STRING), "eggBridge");
        }
        return false;
    }
}
