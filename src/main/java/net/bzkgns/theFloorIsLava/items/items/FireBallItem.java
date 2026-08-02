package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class FireBallItem extends CustomItem {
    public FireBallItem() {
        super("fireball",
                Rarity.RARE,
                Material.FIRE_CHARGE,
                true);
    }
}
