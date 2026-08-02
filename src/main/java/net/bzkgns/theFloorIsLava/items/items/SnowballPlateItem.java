package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class SnowballPlateItem extends CustomItem {
    public SnowballPlateItem() {
        super("snowball_plate",
                Rarity.RARE,
                Material.SNOWBALL,
                true
        );
    }

}
