package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;

public class HealCampItem extends CustomItem {
    public HealCampItem() {
        super("heal_camp",
                "items.heal_camp.display_name",
                "items.heal_camp.lore",
                Rarity.LEGENDARY,
                Material.CAMPFIRE,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
