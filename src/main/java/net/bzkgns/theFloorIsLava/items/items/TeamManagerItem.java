package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TeamManagerItem extends CustomItem {
    public TeamManagerItem() {
        super("team_manager",
                "items.team_manager.display_name",
                "items.team_manager.lore",
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
