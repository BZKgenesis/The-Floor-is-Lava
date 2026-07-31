package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.abilities.TeamRespawnManager;
import org.jetbrains.annotations.Nullable;

public class TeamRespawnItem extends CustomItem {
    public TeamRespawnItem() {
        super("team_respawn",
                "items.team_respawn_anchor.display_name",
                "items.team_respawn_anchor.lore",
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
