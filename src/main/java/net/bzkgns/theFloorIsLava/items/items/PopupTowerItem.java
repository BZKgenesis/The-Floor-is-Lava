package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class PopupTowerItem extends CustomItem {

    public PopupTowerItem() {
        super("popup_tower",
                Rarity.EPIC,
                Material.CHEST,
                true
        );
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(45,25,0);
    }
}
