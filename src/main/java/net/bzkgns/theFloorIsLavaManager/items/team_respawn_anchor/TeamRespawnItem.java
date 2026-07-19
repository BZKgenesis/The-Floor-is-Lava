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
                "items.team_respawn.display_name",
                List.of("items.team_respawn_anchor.lore1", "items.team_respawn_anchor.lore2"),
                Rarity.RARE,
                TeamRespawnManager.respawnAnchorMaterial,
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
