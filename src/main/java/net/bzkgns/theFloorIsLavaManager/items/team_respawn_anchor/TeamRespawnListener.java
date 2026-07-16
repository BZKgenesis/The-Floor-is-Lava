package net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TeamRespawnListener implements Listener {

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack blockPlaced = event.getItemInHand();

        if (new TeamRespawnItem().isItem(blockPlaced)) {
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team == null) {
                player.sendActionBar(TextUtils.errorMessage("Vous ne pouvez pas placer le portail d'inventaire d'équipe car vous n'êtes pas dans une équipe.", false));
                event.setCancelled(true);
                return;
            }
            TheFloorIsLavaManager.getInstance().getLogger().info(event.getPlayer().getName()+ " a placé une ancre de réapparition en" + block.getLocation());
            if (TeamRespawnManager.getInstance().hasRespawnPoint(team.getName())){
                TeamRespawnManager.getInstance().removeRespawnPoint(team.getName());
            }
            TeamRespawnManager.getInstance().setRespawnPoint(team.getName(), new BlockPos(block.getX(),block.getY(),block.getZ()));
            player.sendMessage(TextUtils.validationMessage("Le portail d'inventaire d'équipe a été placé.", false));
        }
    }
}
