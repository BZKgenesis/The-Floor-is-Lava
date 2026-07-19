package net.bzkgns.theFloorIsLavaManager.items;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
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
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }

}
