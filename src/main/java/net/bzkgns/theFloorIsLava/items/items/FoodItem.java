package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FoodItem extends CustomItem {
    public FoodItem() {
        super("food",
                new ItemStack(Material.BAKED_POTATO, 1));
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(0,5,0);
    }
}
