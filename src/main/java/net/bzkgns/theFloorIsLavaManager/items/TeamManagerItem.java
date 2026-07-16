package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
public class TeamManagerItem extends CustomItem {
    public TeamManagerItem() {
        super("team_manager",
                "Gestion des équipes",
                "Permet de gérer les équipes",
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
