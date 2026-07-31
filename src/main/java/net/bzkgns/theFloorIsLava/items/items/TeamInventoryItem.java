package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TeamInventoryItem extends CustomItem {
    public TeamInventoryItem() {
        super("team_inv",
                "items.team_inv.display_name",
                "items.team_inv.lore",
                Rarity.EPIC,
                Material.ENDER_CHEST,
                true
        );
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(80,70,0);
    }
}
