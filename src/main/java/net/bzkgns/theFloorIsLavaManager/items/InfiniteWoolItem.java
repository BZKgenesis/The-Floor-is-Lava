package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

public class InfiniteWoolItem extends CustomItem {
    public InfiniteWoolItem() {
        super("infinite_wool",
                "items.infinite_wool.display_name",
                "items.infinite_wool.lore",
                Rarity.EPIC,
                Material.LIGHT_GRAY_WOOL,
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
