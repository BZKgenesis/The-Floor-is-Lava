package net.bzkgns.theFloorIsLavaManager.items;


import org.bukkit.inventory.CraftingRecipe;

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

    public static List<String> getAllCraftableItemKeys(){
        List<String> craftableItemKeys = new ArrayList<>();
        for (CustomItem item : ITEMS.values()) {
            if (item.getRecipe() != null) {
                craftableItemKeys.add(item.getKey());
            }
        }
        return craftableItemKeys;
    }

    public static List<CraftingRecipe> getAllCraftingRecipes(){
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (CustomItem item : ITEMS.values()) {
            if (item.getRecipe() != null) {
                recipes.add(item.getRecipe());
            }
        }
        return recipes;
    }

    public static CustomItem getItemByKey(String key){
        return ITEMS.get(key);
    }

}
