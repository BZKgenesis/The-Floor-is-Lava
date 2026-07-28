package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class NewShopItem extends CustomItem {

    public NewShopItem() {
        super("new_shop",
                "items.new_shop.display_name",
                "items.new_shop.lore",
                Rarity.COMMON,
                Material.BOOK,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
