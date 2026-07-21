package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class InfiniteWoolItem extends CustomItem {
    public InfiniteWoolItem() {
        super("infinite_wool",
                "items.infinite_wool.display_name",
                "items.infinite_wool.lore",
                Rarity.EPIC,
                Material.LIGHT_GRAY_WOOL,
                true
        );
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
