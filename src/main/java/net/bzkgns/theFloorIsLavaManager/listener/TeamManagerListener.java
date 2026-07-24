package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.TeamManagerItem;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.teams.TeamGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TeamManagerListener implements Listener {

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    @EventHandler
    public void onTeamManagerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // Ignore la main secondaire
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new TeamManagerItem().isItem(item)) return;
        if (plugin.getGameManager().getState()== GameState.RUNNING) return;

        event.setCancelled(true);
        TeamGUI.openMainMenu(event.getPlayer());
    }
}
