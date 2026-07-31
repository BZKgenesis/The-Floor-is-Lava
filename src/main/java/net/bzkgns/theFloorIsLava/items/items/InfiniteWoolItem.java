package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class InfiniteWoolItem extends CustomItem {
    public InfiniteWoolItem() {
        super("infinite_wool",
                "items.infinite_wool.display_name",
                "items.infinite_wool.lore",
                Rarity.EPIC,
                Material.LIGHT_GRAY_WOOL,
                true
        );
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(60,20,0);
    }
}
