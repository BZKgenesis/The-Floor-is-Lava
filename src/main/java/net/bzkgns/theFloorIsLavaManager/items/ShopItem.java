package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

public class ShopItem extends CustomItem{
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
    public ItemStack giveItem() {
        return itemStack.clone();
    }

    @Override
    public CraftingRecipe getRecipe() {
        return null;
    }
}
