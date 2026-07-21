package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class TntItem extends CustomItem {


    public TntItem() {
        super("tnt",
                "items.tnt.display_name",
                "items.tnt.lore",
                Rarity.RARE,
                Material.TNT,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
