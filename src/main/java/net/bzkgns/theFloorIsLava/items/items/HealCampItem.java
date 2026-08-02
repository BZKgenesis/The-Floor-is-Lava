package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class HealCampItem extends CustomItem {
    public HealCampItem() {
        super("heal_camp",
                Rarity.LEGENDARY,
                Material.CAMPFIRE,
                true);
    }

}
