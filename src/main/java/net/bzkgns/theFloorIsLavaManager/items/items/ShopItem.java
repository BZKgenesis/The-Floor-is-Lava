package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class ShopItem extends CustomItem {
    public ShopItem() {
        super("shop_item",
                "items.shop_item.display_name",
                "items.shop_item.lore",
                Rarity.COMMON,
                Material.BOOK,
                true
        );
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
