package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WoolItem extends CustomItem {

    public WoolItem() {
        super("wool",
                new ItemStack(Material.LIGHT_GRAY_WOOL, 2)
        );
    }
}
