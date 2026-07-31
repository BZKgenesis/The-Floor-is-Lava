package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TeamManagerItem extends CustomItem {
    public TeamManagerItem() {
        super("team_manager",
                Rarity.EPIC,
                Material.PAPER,
                true
        );
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(50,50,0);
    }

}
