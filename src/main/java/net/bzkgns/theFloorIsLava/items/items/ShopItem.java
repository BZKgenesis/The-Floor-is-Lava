package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class ShopItem extends CustomItem {

    public ShopItem() {
        super("shop",
                Rarity.COMMON,
                Material.BOOK,
                true);
    }
}
