package net.bzkgns.theFloorIsLava.items.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ThrowableIronGolemGoal implements Goal<@NotNull IronGolem> {
    private static final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();


    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    // This is the key for the goal. It is used to identify the goal and is
    // used to determine if two goals are the same.
    public static final GoalKey<@NotNull IronGolem> KEY = GoalKey.of(
            // The entity class this goal is targeting.
            IronGolem.class,
            // The key used for identification. Should use your plugin's namespace.
            new NamespacedKey(plugin, "iron_golem_attack_player")
    );

    private int attackCooldown = 0; // The cooldown between attacks in ticks (20 ticks = 1 second).
    private final Player player; // The creator.
    private final @Nullable TeamData team; // The team of the creator.
    private final IronGolem ironGolem; // The team of the creator.

    public ThrowableIronGolemGoal(Player player, IronGolem ironGolem) {
        this.player = player;
        this.team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        this.ironGolem = ironGolem;
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void tick() {
        // Called every tick while the goal is running. Here, we make the camel
        // move towards the player using the Pathfinder API.
        // The 5.0 is the speed of the camel.
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        ironGolem.getNearbyEntities(
                    itemsConfig.getThrowableIronGolemMaxDistance(),
                    itemsConfig.getThrowableIronGolemMaxDistance(),
                    itemsConfig.getThrowableIronGolemMaxDistance()).stream()
                .filter(entity -> entity instanceof Player && entity.isValid() && !entity.isDead())
                .filter(entity -> {
                    Player nearbyPlayer = (Player) entity;
                    if (team == null) {
                        return !nearbyPlayer.equals(player);
                    }else{
                        TeamData nearbyPlayerTeam = TeamManager.getInstance().getPlayerTeam(nearbyPlayer.getUniqueId());
                        return nearbyPlayerTeam != null && !nearbyPlayerTeam.equals(team);
                    }
                })
                .findFirst()
                .ifPresent(nearbyPlayer -> {
                    ironGolem.getPathfinder().moveTo(nearbyPlayer);
                    ironGolem.lookAt(nearbyPlayer);
                    if (ironGolem.getLocation().distance(nearbyPlayer.getLocation()) < itemsConfig.getThrowableIronGolemAttackDistance()) {
                        ironGolem.attack(nearbyPlayer);
                        attackCooldown = itemsConfig.getThrowableIronGolemAttackCooldown();
                    }
                });
    }

    @Override
    public @NotNull GoalKey<@NotNull IronGolem> getKey() {
        return KEY;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE,GoalType.LOOK,GoalType.TARGET);
    }
}
