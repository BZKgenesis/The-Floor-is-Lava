package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class SnowballPlateItem extends CustomItem {
    public SnowballPlateItem() {
        super("snowball_plate",
                "items.snowball_plate.display_name",
                "items.snowball_plate.lore",
                Rarity.RARE,
                Material.SNOWBALL,
                true
        );
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(35,15,0);
    }
}
