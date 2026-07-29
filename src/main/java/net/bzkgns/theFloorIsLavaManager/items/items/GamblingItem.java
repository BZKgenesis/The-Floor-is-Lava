package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class GamblingItem extends CustomItem {
    public GamblingItem() {
        super("gambling",
                "items.gambling.display_name",
                "items.gambling.lore",
                Rarity.LEGENDARY,
                Material.EMERALD,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
