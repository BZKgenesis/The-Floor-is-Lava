package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

public class InfiniteWoolItem extends CustomItem {
    public InfiniteWoolItem() {
        super("infinite_wool",
                "Infinite Wool",
                "Permet de placer des blocs de laine infiniment",
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
