package net.bzkgns.theFloorIsLava.statistics.visual;

import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RankingListener implements Listener {
    @EventHandler
    public void onRankingClick(PlayerInteractEntityEvent event) {

        if (event.getRightClicked() instanceof Interaction interaction) {
            RankingInstance rankingInstance = RankingInstance.getRankingInstanceFromEntity(interaction);
            if (rankingInstance != null) {
                if (interaction.getScoreboardTags().contains("rankingRight")) {
                    rankingInstance.nextType();
                } else if (interaction.getScoreboardTags().contains("rankingLeft")) {
                    rankingInstance.previousType();
                }
            }
        }
    }
}
