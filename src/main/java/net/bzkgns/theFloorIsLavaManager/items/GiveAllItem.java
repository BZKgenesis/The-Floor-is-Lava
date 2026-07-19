package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

public class GiveAllItem extends CustomItem{
    public GiveAllItem() {
        super("give_all",
                "items.give_all.display_name",
                "items.give_all.lore",
                Rarity.COMMON,
                Material.BOOK,
                true);
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
