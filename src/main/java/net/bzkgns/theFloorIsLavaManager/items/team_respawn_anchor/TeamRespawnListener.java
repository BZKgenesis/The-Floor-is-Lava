package net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TeamRespawnListener implements Listener {

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack blockPlaced = event.getItemInHand();
        Location location = block.getLocation();

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
            TeamRespawnManager.getInstance().setRespawnPoint(team.getName(),location);
            player.sendMessage(TextUtils.validationMessage("Le portail d'inventaire d'équipe a été placé.", false));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockPos = block.getLocation();
        ItemStack blockBroken = ItemStack.of(block.getType());
        if (blockBroken.getType().equals(TeamRespawnManager.respawnAnchorMaterial)){
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team != null){
                Location respawnPos = TeamRespawnManager.getInstance().getRespawnPoint(team.getName());
                if (respawnPos != null && respawnPos.getX() == block.getX() && respawnPos.getY() == block.getY() && respawnPos.getZ() == block.getZ()) {
                    player.sendActionBar(TextUtils.errorMessage("Vous ne pouvez pas casser votre propre portail d'inventaire d'équipe.", false));
                    plugin.getLogger().info(player.getName() + " a essayé de casser son propre portail d'inventaire d'équipe.");
                    event.setCancelled(true);
                    return;
                }
            }
            String teamName = TeamRespawnManager.getInstance().getTeamNameByRespawnPoint(blockPos);
            if (teamName != null) {
                TeamRespawnManager.getInstance().removeRespawnPoint(teamName);
                plugin.getLogger().info(player.getName() + " a cassé le portail d'inventaire d'équipe de l'équipe " + teamName);
                player.sendActionBar(TextUtils.validationMessage("Le portail d'inventaire d'équipe a été supprimé.", false));
                TeamManager.broadcastTeamMessage(TextUtils.errorMessage("Le portail d'inventaire d'équipe a été supprimé."), TeamManager.getInstance().getTeam(teamName));
                return;
            }
            plugin.getLogger().warning(player.getName() + " a cassé un portail d'inventaire d'équipe qui n'était pas lié à une équipe.");
        }
    }
}
