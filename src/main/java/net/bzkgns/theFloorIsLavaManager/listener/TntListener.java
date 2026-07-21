package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.TntItem;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class TntListener implements Listener {
    private static final float TNT_KNOCKBACK_ENCHANT_MULTIPLIER = 0.2f;
    private static final float TNT_KNOCKBACK_MULTIPLIER = 1.2f;
    private static final float TNT_DAMAGE_REDUCTION = 0.25f;
    private static final float TNT_RAYCAST_DISTANCE = 4.5f;
    private static final int TNT_IMMUNE_DELAY_TICK = 5;
    private static final float TNT_SPAWN_Y_VELOCITY = 0.5f;
    private static final float TNT_POWER = 4.0f;

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    @EventHandler
    public void onSwing(PlayerAnimationEvent event) {

        Player player = event.getPlayer();

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                TNT_RAYCAST_DISTANCE,
                entity -> entity instanceof TNTPrimed
        );

        if (result == null) {
            return;
        }

        TNTPrimed tnt = (TNTPrimed) result.getHitEntity();
        if (tnt == null) {
            return;
        }
        if (tnt.getTicksLived() < TNT_IMMUNE_DELAY_TICK) {
            return;
        }

        Vector knockback = player.getLocation().getDirection()
                .normalize()
                .multiply(TNT_KNOCKBACK_MULTIPLIER);

        int knockbackLevel = player.getActiveItem().getEnchantmentLevel(Enchantment.KNOCKBACK);

        knockback = knockback.multiply(1f + knockbackLevel * TNT_KNOCKBACK_ENCHANT_MULTIPLIER);

        knockback.setY(Math.abs(knockback.getY()));

        knockback = knockback.add(event.getPlayer().getVelocity());

        tnt.setVelocity(tnt.getVelocity().add(knockback));
    }

    @EventHandler
    public void onTntPlaced(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();
        if (new TntItem().isItem(item)){
            TNTPrimed tnt = event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);

            tnt.setSource(player);

            BlockState blockState = tnt.getBlockData().createBlockState();

            blockState.setType(BlockUtils.getConcreteBlockByPlayer(player));

            tnt.setBlockData(blockState.getBlockData());

            tnt.setYield(TNT_POWER);

            tnt.setSource(player);

            tnt.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "tnt_source"),
                    PersistentDataType.STRING,
                    player.getUniqueId().toString());


            tnt.setVelocity( new Vector(0,TNT_SPAWN_Y_VELOCITY,0) );

            TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (teamData != null) {
                teamData.getVanillaTeam().addEntities(tnt);
            }


            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onTntDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim))
            return;

        if (!(event.getDamager() instanceof TNTPrimed tnt))
            return;

        if (!tnt.getPersistentDataContainer().has(new NamespacedKey(plugin, "tnt_source")))
            return;

        if (!(tnt.getSource() instanceof Player source))
            return;

        Team tntTeam = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(source);
        Team victimTeam = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(victim);
        if (victimTeam != null && victimTeam.equals(tntTeam))
            return;


        event.setDamage(event.getDamage() * TNT_DAMAGE_REDUCTION);
    }
}
