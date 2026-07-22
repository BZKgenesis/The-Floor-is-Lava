package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class ParachuteItem extends CustomItem {
    public ParachuteItem() {
        super("parachute",
                "items.parachute.display_name",
                "items.parachute.lore",
                Rarity.RARE,
                Material.FEATHER,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
