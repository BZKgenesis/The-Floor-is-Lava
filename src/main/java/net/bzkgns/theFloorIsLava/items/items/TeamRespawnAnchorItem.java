package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.abilities.TeamRespawnManager;

public class TeamRespawnAnchorItem extends CustomItem {
    public TeamRespawnAnchorItem() {
        super("team_respawn_anchor",
                Rarity.RARE,
                TeamRespawnManager.respawnAnchorMaterial,
                true
        );
    }
}
