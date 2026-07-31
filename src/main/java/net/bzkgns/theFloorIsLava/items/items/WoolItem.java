package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WoolItem extends CustomItem {

    public WoolItem() {
        super("wool",
                new ItemStack(Material.LIGHT_GRAY_WOOL, 2)
        );
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(1,0,0);
    }
}
