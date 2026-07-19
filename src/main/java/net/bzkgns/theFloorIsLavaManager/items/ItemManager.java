package net.bzkgns.theFloorIsLavaManager.items;


import net.kyori.adventure.audience.Audience;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemManager {

    private static final Map<String, CustomItem> ITEMS = new HashMap<>();

    public static void register(CustomItem item) {
        ITEMS.put(item.getKey(), item);
    }

    public static void registerAll(CustomItem... items) {
        for (CustomItem item : items) {
            register(item);
        }
    }


    @SuppressWarnings("unused")
    public static List<CustomItem> getAllItems(){
        return new ArrayList<>(ITEMS.values());
    }

    public static List<String> getAllItemKeys(){
        return new ArrayList<>(ITEMS.keySet());
    }

    public static List<String> getAllCraftableItemKeys(Audience audience){
        List<String> craftableItemKeys = new ArrayList<>();
        for (CustomItem item : ITEMS.values()) {
            if (item.getRecipe(audience) != null) {
                craftableItemKeys.add(item.getKey());
            }
        }
        return craftableItemKeys;
    }

    public static List<CraftingRecipe> getAllCraftingRecipes(Audience audience){
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (CustomItem item : ITEMS.values()) {
            if (item.getRecipe(audience) != null) {
                recipes.add(item.getRecipe(audience));
            }
        }
        return recipes;
    }

    public static CustomItem getItemByKey(String key){
        return ITEMS.get(key);
    }

    public static CustomItem getAssociatedCustomItem(ItemStack itemStack) {
        for (CustomItem item : ITEMS.values()) {
            if (item.isItem(itemStack)) {
                return item;
            }
        }
        return null;
    }

}
