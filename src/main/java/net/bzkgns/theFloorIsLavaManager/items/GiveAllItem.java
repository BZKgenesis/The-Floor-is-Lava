package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

public class GiveAllItem extends CustomItem{
    public GiveAllItem() {
        super("give_all", "GiveAll", "Ouvre le menu de give", Rarity.COMMON, Material.BOOK, true);
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
