package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.abilities.TeamRespawnManager;
import net.bzkgns.theFloorIsLava.items.items.TeamRespawnAnchorItem;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
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

        if (new TeamRespawnAnchorItem().isItem(blockPlaced)) {
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team == null) {
                player.sendActionBar(Messages.component(player, "error.no_team_cannot_place_anchor"));
                event.setCancelled(true);
                return;
            }
            TheFloorIsLava.getInstance().getLogger().info(event.getPlayer().getName()+ " a placé une ancre de réapparition en" + block.getLocation());
            if (TeamRespawnManager.getInstance().hasRespawnPoint(team.getId())){
                TeamRespawnManager.getInstance().removeRespawnPoint(team.getId());
            }
            TeamRespawnManager.getInstance().setRespawnPoint(team.getId(),location);
            Messages.send(player, "validation.anchor_placed");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        TheFloorIsLava plugin = TheFloorIsLava.getInstance();
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockPos = block.getLocation();
        ItemStack blockBroken = ItemStack.of(block.getType());
        if (blockBroken.getType().equals(TeamRespawnManager.respawnAnchorMaterial)){
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team != null){
                Location respawnPos = TeamRespawnManager.getInstance().getRespawnPoint(team.getId());
                if (respawnPos != null && respawnPos.getX() == block.getX() && respawnPos.getY() == block.getY() && respawnPos.getZ() == block.getZ()) {
                    player.sendActionBar(Messages.component(player, "error.cannot_break_own_anchor"));
                    plugin.getLogger().info(player.getName() + " a essayé de casser son propre portail d'inventaire d'équipe.");
                    event.setCancelled(true);
                    return;
                }
            }
            String teamName = TeamRespawnManager.getInstance().getTeamNameByRespawnPoint(blockPos);
            TeamData teamData = TeamManager.getInstance().getTeam(teamName);
            if (teamData != null) {
                TeamRespawnManager.getInstance().removeRespawnPoint(teamName);
                plugin.getLogger().info(player.getName() + " a cassé le portail d'inventaire d'équipe de l'équipe " + teamName);
                player.sendActionBar(Messages.component(player, "validation.anchor_removed"));

                Messages.broadcastTeamAlert(teamData, "team.anchor_removed_broadcast");
                return;
            }
            plugin.getLogger().warning(player.getName() + " a cassé un portail d'inventaire d'équipe qui n'était pas lié à une équipe.");
        }
    }
}