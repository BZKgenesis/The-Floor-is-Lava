package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.items.items.FireBallItem;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class FireBallCustomListener implements Listener {
    private final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();
    @EventHandler
    public void onFireBallUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (! new FireBallItem().isItem(event.getItem())) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();


        if (event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE){
            if (player.hasCooldown(event.getItem().getType()))
                return;
            if (event.getItem().getAmount() <= 1) {
                event.getPlayer().getInventory().removeItem(event.getItem());
            } else {
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
            // Active le cooldown
            player.setCooldown(event.getItem().getType(), itemsConfig.getFireballCooldown());
        }

        Fireball fireball =  player.getWorld().spawn(
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)),
                org.bukkit.entity.Fireball.class, f -> {
                    f.setShooter(player);
                    f.setDirection(player.getLocation().getDirection().multiply(itemsConfig.getFireballSpeed()));
                    f.setYield((float) itemsConfig.getFireballPower()); // Explosion power
                    f.setGlowing(true);
                    f.setIsIncendiary(itemsConfig.isFireballPlaceFire());
                }
        );

        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        if (teamData == null) return;
        Team t = sb.getTeam(teamData.getId());
        if (t != null) t.addEntities(fireball);
    }
    @EventHandler
    public void onFireballDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim))
            return;

        if (!(event.getDamager() instanceof Fireball fireball))
            return;

        if (!(fireball.getShooter() instanceof Player shooter))
            return;

        Team shooterTeam = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(shooter);
        Team victimTeam = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(victim);
        if (victimTeam != null && victimTeam.equals(shooterTeam))
            return;



        // Réduit les dégâts à 25 %
        event.setDamage(event.getDamage() * itemsConfig.getFireballDamageReduction());
    }

    @EventHandler
    public void onFireballExplode(EntityExplodeEvent event) {

        if (!(event.getEntity() instanceof Fireball fireball))
            return;

        Location explosion = fireball.getLocation();

        for (Entity entity : explosion.getWorld().getNearbyEntities(
                explosion,
                5,
                5,
                5
        )) {

            if (!(entity instanceof Player player))
                continue;


            Vector knockback = player.getLocation()
                    .toVector()
                    .subtract(explosion.toVector())
                    .normalize()
                    .multiply(2.5);

            knockback.setY(0.8);

            player.setVelocity(
                    player.getVelocity().add(knockback)
            );
        }
    }
}
