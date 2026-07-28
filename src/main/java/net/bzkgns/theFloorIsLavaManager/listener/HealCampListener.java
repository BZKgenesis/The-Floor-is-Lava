package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.items.abilities.HealCampInstance;
import net.bzkgns.theFloorIsLavaManager.items.abilities.HealCampManager;
import net.bzkgns.theFloorIsLavaManager.items.items.HealCampItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class HealCampListener implements Listener {

    @EventHandler
    public void onCampfirePlaced(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemInHand = event.getItemInHand();
        if (!new HealCampItem().isItem(itemInHand)) return;
        Location location = event.getBlockPlaced().getLocation();
        float SIZE = 5f;

        HealCampManager.getInstance().removeHealCamp(location);

        HealCampManager.getInstance().addHealCamp(location, new HealCampInstance(location, (int) SIZE, event.getPlayer()));
    }

    @EventHandler
    public void onCampfireBroken(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        HealCampManager.getInstance().removeHealCamp(location);
    }

    @EventHandler
    public void onAreaCloudApplyEffect(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntityTeam(cloud);
        if (team == null) return;
        event.getAffectedEntities().removeIf(e -> {
            Team entityTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntityTeam(e);
            return entityTeam == null || !entityTeam.equals(team);
        });
    }
}
