package io.github.pikayorld.theFloorIsLavaManager;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static io.github.pikayorld.theFloorIsLavaManager.BlockColorUtils.getWoolBlockByPlayer;

public class EggBridgeTask implements Runnable {

    private final Plugin plugin;

    public EggBridgeTask(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Server server = Bukkit.getServer();
        for (World world : server.getWorlds()){
            for (Entity entity : world.getEntities()){
                if (entity instanceof Egg egg){
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
