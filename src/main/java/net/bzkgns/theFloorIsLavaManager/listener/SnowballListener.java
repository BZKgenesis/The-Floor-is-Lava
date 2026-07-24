package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.SnowballPlateItem;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SnowballListener implements Listener {
    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(Objects.equals(snowball.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "snowballPlateEntity"),
                        PersistentDataType.STRING),
                "snowballPlateEntity"))
        ) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        Location loc = snowball.getLocation().getBlock().getLocation();
        fillAround(loc, 4, BlockUtils.getWoolBlockByPlayer(p));
    }

    @EventHandler
    public void onSnowballPlateLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!new SnowballPlateItem().isItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(
                new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "snowballPlateEntity"),
                PersistentDataType.STRING,
                "snowballPlateEntity");
    }

    private void fillAround(Location center, @SuppressWarnings("SameParameterValue") int radius, Material material) {
        World world = center.getWorld();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                Block b = world.getBlockAt(x, cy, z);

                // Si tu veux éviter de remplacer n’importe quoi :
                if (!b.getType().isSolid()) {
                    b.setType(material, false);
                }
            }
        }
    }
}
