package net.bzkgns.theFloorIsLavaManager.items.team_inventory;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class TeamInventoryListener implements Listener {

    @EventHandler
    public void onTeamInvInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new TeamInventoryItem().isItem(item)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String team = getTeamOf(player);

        if (team == null) {
            player.sendActionBar(TextUtils.errorMessage("Vous n'êtes pas dans une équipe, donc pas de coffre partagé.", false));
            return;
        }

        TeamInventory inv = TeamInventoryManager.getInstance().getTeamInventory(team);
        player.openInventory(inv.getInventory());
    }

    private String getTeamOf(Player p){
        if (p!= null){
            Team team = TheFloorIsLavaManager.getInstance().getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
            if (team != null){
                return team.getName();
            }
        }
        return null;
    }
}
