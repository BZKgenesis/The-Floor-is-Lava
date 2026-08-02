package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FoodItem extends CustomItem {
    public FoodItem() {
        super("food",
                new ItemStack(Material.BAKED_POTATO, 1));
    }

}
