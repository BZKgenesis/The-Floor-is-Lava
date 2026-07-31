package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.abilities.TeamRespawnManager;
import org.jetbrains.annotations.Nullable;

public class TeamRespawnAnchorItem extends CustomItem {
    public TeamRespawnAnchorItem() {
        super("team_respawn_anchor",
                Rarity.RARE,
                TeamRespawnManager.respawnAnchorMaterial,
                true
        );
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(100,150,0);
    }
}
