package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class FireBallCustomItem extends CustomItem {
    public FireBallCustomItem() {
        super("fireball_custom",
                "items.fireball.display_name",
                "items.fireball.lore",
                Rarity.RARE,
                Material.FIRE_CHARGE,
                true);
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(20,45,0);
    }
}
