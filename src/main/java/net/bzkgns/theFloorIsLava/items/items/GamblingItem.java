package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class GamblingItem extends CustomItem {
    public GamblingItem() {
        super("gambling",
                Rarity.LEGENDARY,
                Material.EMERALD,
                true);
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(50,50,0);
    }
}
