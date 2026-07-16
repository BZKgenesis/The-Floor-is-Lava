package net.bzkgns.theFloorIsLavaManager.items;


import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.getWoolBlockByPlayer;

public class EggBridgeTask implements Runnable {

    private final Plugin plugin;

    public EggBridgeTask() {
        this.plugin = TheFloorIsLavaManager.getInstance();
    }

    @Override
    public void run() {
        Server server = Bukkit.getServer();
        for (World world : server.getWorlds()){
            for (Entity entity : world.getEntities()){
                if (entity instanceof Egg egg){
                    if (Objects.equals(egg.getPersistentDataContainer().get(new NamespacedKey(plugin, "eggBridgeEntity"), PersistentDataType.STRING), "eggBridgeEntity")){
                        Block block = egg.getLocation().getBlock().getRelative(0,-2,0);
                        if (block.getType() == Material.AIR){
                            if (egg.getOwnerUniqueId() != null){
                                Player p = server.getPlayer(egg.getOwnerUniqueId());
                                if (p != null){
                                    block.setType(getWoolBlockByPlayer(p));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
