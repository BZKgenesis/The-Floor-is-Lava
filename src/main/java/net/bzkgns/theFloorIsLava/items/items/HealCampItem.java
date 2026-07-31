package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class HealCampItem extends CustomItem {
    public HealCampItem() {
        super("heal_camp",
                Rarity.LEGENDARY,
                Material.CAMPFIRE,
                true);
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(40,90,0);
    }
}
