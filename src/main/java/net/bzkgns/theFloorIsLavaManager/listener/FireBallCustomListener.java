package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.items.items.FireBallCustomItem;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
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
    private static final int FIREBALL_COOLDOWN = 20;
    private static final float FIREBALL_POWER = 2.0f;
    private static final float FIREBALL_SPEED = 1.0f;
    private static final float FIREBALL_DAMAGE_REDUCTION = 0.25f;
    private static final boolean FIREBALL_PLACE_FIRE = false;
    @EventHandler
    public void onFireBallUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (! new FireBallCustomItem().isItem(event.getItem())) return;
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
            player.setCooldown(event.getItem().getType(), FIREBALL_COOLDOWN);
        }

        Fireball fireball =  player.getWorld().spawn(
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)),
                org.bukkit.entity.Fireball.class, f -> {
                    f.setShooter(player);
                    f.setDirection(player.getLocation().getDirection().multiply(FIREBALL_SPEED));
                    f.setYield(FIREBALL_POWER); // Explosion power
                    f.setGlowing(true);
                    f.setIsIncendiary(FIREBALL_PLACE_FIRE);
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
        event.setDamage(event.getDamage() * FIREBALL_DAMAGE_REDUCTION);
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
