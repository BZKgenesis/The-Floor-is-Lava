package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomItem {
    protected final NamespacedKey key;
    protected final TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

    protected CustomItem(String key){
        this.key = new NamespacedKey(plugin,key);
    }
    public abstract ItemStack giveItem();
    public abstract  boolean isItem(ItemStack stack);
    public abstract CraftingRecipe getRecipe();


    public String getKey(){
        return key.getKey();
    }
}
