package net.bzkgns.theFloorIsLava.listener;

import com.destroystokyo.paper.entity.ai.Goal;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.goals.ThrowableIronGolemGoal;
import net.bzkgns.theFloorIsLava.items.items.ThrowableIronGolemItem;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.bzkgns.theFloorIsLava.utils.TextUtils.plainText;

public class ThrowableIronGolemListener implements Listener {

    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    @EventHandler
    public void onThrowableIronGolemLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!(egg.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!new ThrowableIronGolemItem().isItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(
                new NamespacedKey(plugin,"throwableIronGolemEntity"),
                PersistentDataType.STRING,
                "throwableIronGolemEntity");
    }


    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event){
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!(Objects.equals(egg.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "throwableIronGolemEntity"),
                        PersistentDataType.STRING),
                "throwableIronGolemEntity"))
        ) return;
        if (!(egg.getShooter() instanceof Player p)) return;

        spawnIronGolem(egg.getLocation(), p);

    }

    private void spawnIronGolem(Location loc, Player p) {
        loc.getWorld().spawn(loc, org.bukkit.entity.IronGolem.class, golem -> {
            TeamData teamData = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
            if (teamData != null) {
                golem.customName(teamData.getName());
                teamData.getVanillaTeam().addEntities(golem);
            } else {
                golem.customName(Component.text(p.getName() + "'s Iron Golem"));
            }
            golem.setCustomNameVisible(true);
            golem.setPlayerCreated(true);
            golem.setTarget(null);
            golem.setInvulnerable(false);
            golem.setPersistent(true);
            golem.setRemoveWhenFarAway(false);

            golem.getPersistentDataContainer().set(
                    new NamespacedKey(plugin,"ironGolemEntity"),
                    PersistentDataType.STRING,
                    plainText(golem.customName()));

            golem.getPersistentDataContainer().set(
                    new NamespacedKey(plugin,"ironGolemEntityColor"),
                    PersistentDataType.INTEGER,
                    teamData==null? NamedTextColor.WHITE.value():teamData.getColor().value());
            Sound golemRepairSound = Sound.sound(Key.key("entity.iron_golem.repair"), Sound.Source.HOSTILE, 1f, 0.5f);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> golem.playSound(golemRepairSound));

            Goal<@NotNull IronGolem> goal = new ThrowableIronGolemGoal(p, golem);
            plugin.getServer().getMobGoals().addGoal(golem, 0, goal);
        });

    }
}
