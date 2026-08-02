package net.bzkgns.theFloorIsLava.items;


import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.shop.ShopSellItem;
import net.bzkgns.theFloorIsLava.currency.Price;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
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

    public static List<CustomItem> getAllBuyableItemStacks(Audience audience){
        List<CustomItem> itemStacks = new ArrayList<>();
        for (CustomItem item : ITEMS.values()) {
            if (item.getPrice() != null) {
                itemStacks.add(item);
            }
        }
        return itemStacks;
    }

    public static Map<Material, Price> getAllSellableMap(){
        Map<Material, Price> sellableItems = new HashMap<>();
        for(ShopSellItem item : TheFloorIsLava.getInstance().getGameManager().getShopConfigManager().getConfig().getShopSellableItems()){
            Material material = Material.getMaterial(item.id().toUpperCase());
            if (material != null) {
                sellableItems.put(material, new Price(item.resource(), item.material(), 0));
            }
        }

        return sellableItems;
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
