package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
public class TeamManagerItem extends CustomItem {
    public TeamManagerItem() {
        super("team_manager",
                "items.team_manager.display_name",
                "items.team_manager.lore",
                Rarity.EPIC,
                Material.PAPER,
                true
        );
    }
    @Override
    public ItemStack giveItem(){
        return itemStack.clone();
    }


    @Override
    public CraftingRecipe getRecipe() {
        return null;
    }

}
