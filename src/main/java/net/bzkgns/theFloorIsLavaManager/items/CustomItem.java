package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomItem {
    private static final Map<String, CustomItem> items = new HashMap<>();
    protected final NamespacedKey key;
    protected final TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

    protected CustomItem(String key){
        this.key = new NamespacedKey(plugin,key);
        items.put(key,this);
    }
    public abstract ItemStack giveItem();
    public abstract  boolean isItem(ItemStack stack);
    public abstract CraftingRecipe getRecipe();


    @SuppressWarnings("unused")
    public static List<CustomItem> getAllItems(){
        return new ArrayList<>(items.values());
    }

    public static List<String> getAllItemKeys(){
        return new ArrayList<>(items.keySet());
    }

    public static CustomItem getItemByKey(String key){
        return items.get(key);
    }
}
