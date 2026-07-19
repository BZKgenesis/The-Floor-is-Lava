package net.bzkgns.theFloorIsLavaManager.items.team_inventory;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
public class TeamInventoryItem extends CustomItem {
    public TeamInventoryItem() {
        super("team_inv",
                "items.team_inv.display_name",
                "items.team_inv.lore",
                Rarity.EPIC,
                Material.ENDER_CHEST,
                true
        );
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {

        ShapedRecipe teamInvRecipe = new ShapedRecipe(key, giveItem(audience));
        teamInvRecipe.shape("ABA","BCB","ABA");
        teamInvRecipe.setIngredient('A', Material.DIAMOND);
        teamInvRecipe.setIngredient('B', Material.IRON_INGOT);
        teamInvRecipe.setIngredient('C', Material.CHEST);
        return teamInvRecipe;
    }
}
