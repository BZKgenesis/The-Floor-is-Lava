package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class TeamInventoryItem extends CustomItem {
    public TeamInventoryItem() {
        super("team_inv",
                Rarity.EPIC,
                Material.ENDER_CHEST,
                true
        );
    }
}
