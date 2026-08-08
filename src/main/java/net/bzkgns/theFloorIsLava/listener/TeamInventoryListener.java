package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.abilities.TeamInventoryManager;
import net.bzkgns.theFloorIsLava.items.items.TeamInventoryItem;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class TeamInventoryListener implements Listener {

    @EventHandler
    public void onTeamInvInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // Ignore la main secondaire
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new TeamInventoryItem().isItem(item)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String team = getTeamOf(player);

        if (team == null) {
            Messages.actionBar(player, "team_inv.action_bar.not_in_team");
            return;
        }

        TeamInventoryManager.TeamInventory inv = TeamInventoryManager.getInstance().getTeamInventory(team);
        player.openInventory(inv.getInventory());
    }

    private String getTeamOf(Player p){
        if (p!= null){
            Team team = TheFloorIsLava.getInstance().getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
            if (team != null){
                return team.getName();
            }
        }
        return null;
    }
}
