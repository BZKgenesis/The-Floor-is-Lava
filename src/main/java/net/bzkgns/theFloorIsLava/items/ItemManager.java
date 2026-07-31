package net.bzkgns.theFloorIsLava.items;


import net.bzkgns.theFloorIsLava.currency.Price;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class ItemManager {

    private static final Map<String, CustomItem> ITEMS = new HashMap<>();

    private static final Map<Material, Price> SELLABLE_ITEMS;
    static {
        Map<Material, Price> sellable_items = new HashMap<>();

        sellable_items.put(Material.OAK_PLANKS,  new Price(2, 0, 0));
        sellable_items.put(Material.BIRCH_PLANKS,new Price(2, 0, 0));
        sellable_items.put(Material.DARK_OAK_PLANKS,new Price(2, 0, 0));
        sellable_items.put(Material.SPRUCE_PLANKS,new Price(2, 0, 0));
        sellable_items.put(Material.JUNGLE_PLANKS,new Price(2, 0, 0));
        sellable_items.put(Material.MANGROVE_PLANKS,new Price(5, 0, 0));
        sellable_items.put(Material.BAMBOO_PLANKS,new Price(5, 0, 0));
        sellable_items.put(Material.PALE_OAK_PLANKS,new Price(10, 0, 0));
        sellable_items.put(Material.ACACIA_PLANKS,new Price(2, 0, 0));
        sellable_items.put(Material.CHERRY_PLANKS,new Price(7, 0, 0));

        sellable_items.put(Material.COBBLESTONE, new Price(1, 0, 0));
        sellable_items.put(Material.DIRT,        new Price(0, 1, 0));
        sellable_items.put(Material.SAND,        new Price(0, 1, 0));
        sellable_items.put(Material.RED_SAND,    new Price(0, 1, 0));
        sellable_items.put(Material.GRANITE,     new Price(3, 0, 0));
        sellable_items.put(Material.DIORITE,     new Price(3, 0, 0));
        sellable_items.put(Material.ANDESITE,    new Price(3, 0, 0));

        sellable_items.put(Material.COAL,        new Price(0, 2, 0));
        sellable_items.put(Material.COPPER_INGOT,new Price(0, 3, 0));
        sellable_items.put(Material.IRON_INGOT,  new Price(0, 5, 0));
        sellable_items.put(Material.REDSTONE,    new Price(0, 2, 0));
        sellable_items.put(Material.LAPIS_LAZULI,new Price(0, 3, 0));
        sellable_items.put(Material.GOLD_INGOT,  new Price(0, 8, 0));
        sellable_items.put(Material.DIAMOND,     new Price(0, 15, 0));
        sellable_items.put(Material.EMERALD,     new Price(0, 50, 0));

        SELLABLE_ITEMS = Collections.unmodifiableMap(sellable_items);

    }

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
        return SELLABLE_ITEMS;
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
