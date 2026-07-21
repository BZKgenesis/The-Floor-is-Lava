package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class GiveAllItem extends CustomItem {
    public GiveAllItem() {
        super("give_all",
                "items.give_all.display_name",
                "items.give_all.lore",
                Rarity.COMMON,
                Material.BOOK,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
