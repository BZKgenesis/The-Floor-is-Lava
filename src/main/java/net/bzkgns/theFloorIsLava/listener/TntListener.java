package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.items.items.TntItem;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.bzkgns.theFloorIsLava.utils.BlockUtils;
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
    private final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();

    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    @EventHandler
    public void onSwing(PlayerAnimationEvent event) {

        Player player = event.getPlayer();

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                itemsConfig.getTntRaycastDistance(),
                entity -> entity instanceof TNTPrimed
        );

        if (result == null) {
            return;
        }

        TNTPrimed tnt = (TNTPrimed) result.getHitEntity();
        if (tnt == null) {
            return;
        }
        if (tnt.getTicksLived() < itemsConfig.getTntImmuneDelayTick()) {
            return;
        }

        Vector knockback = player.getLocation().getDirection()
                .normalize()
                .multiply(itemsConfig.getTntKnockbackMultiplier());

        int knockbackLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
        knockback.multiply(1f + knockbackLevel * itemsConfig.getTntKnockbackEnchantmentMultiplier());
        knockback.setY(Math.abs(knockback.getY()));
        knockback = knockback.add(event.getPlayer().getVelocity());
        tnt.setVelocity(tnt.getVelocity().add(knockback));
    }

    @EventHandler
    public void onTntPlaced(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();
        if (new TntItem().isItem(item)){
            item.setAmount(item.getAmount()-1);
            TNTPrimed tnt = event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
            tnt.setSource(player);
            BlockState blockState = tnt.getBlockData().createBlockState();
            blockState.setType(BlockUtils.getConcreteBlockByPlayer(player));
            tnt.setBlockData(blockState.getBlockData());
            tnt.setYield((float) itemsConfig.getTntPower());
            tnt.setSource(player);
            tnt.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "tnt_source"),
                    PersistentDataType.STRING,
                    player.getUniqueId().toString());

            tnt.setCustomNameVisible(true);
            tnt.setVelocity( new Vector(0,itemsConfig.getTntSpawnYVelocity(),0) );

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


        event.setDamage(event.getDamage() * itemsConfig.getTntDamageReduction());
    }
}
