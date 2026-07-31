package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class ShopItem extends CustomItem {

    public ShopItem() {
        super("shop",
                "items.new_shop.display_name",
                "items.new_shop.lore",
                Rarity.COMMON,
                Material.BOOK,
                true);
    }
}
