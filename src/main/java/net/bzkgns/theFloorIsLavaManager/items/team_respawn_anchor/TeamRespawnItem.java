package net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class TeamRespawnItem extends CustomItem {
    public TeamRespawnItem() {
        super("team_respawn",
                "Ancre de réapparition d'équipe",
                List.of("Permet de réapparaître à l'endroit où l'ancre est posée", "au lieu du spawn du monde de jeu."),
                Rarity.RARE,
                Material.RESPAWN_ANCHOR,
                true
        );
    }

    @Override
    public ItemStack giveItem() {
        return itemStack.clone();
    }

    @Override
    public CraftingRecipe getRecipe() {

        ShapedRecipe teamRespawnAnchorRecipe = new ShapedRecipe(key, giveItem());
        teamRespawnAnchorRecipe.shape("ABA","BCB","ABA");
        teamRespawnAnchorRecipe.setIngredient('A', Material.DIAMOND);
        teamRespawnAnchorRecipe.setIngredient('B', Material.OBSIDIAN);
        teamRespawnAnchorRecipe.setIngredient('C', Material.ENDER_PEARL);
        return teamRespawnAnchorRecipe;
    }
}
