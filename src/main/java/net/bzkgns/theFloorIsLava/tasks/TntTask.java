package net.bzkgns.theFloorIsLava.tasks;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.Plugin;

public class TntTask implements Runnable {

    private final Plugin plugin;

    public TntTask() {
        this.plugin = TheFloorIsLava.getInstance();
    }

    @Override
    public void run() {
        Server server = Bukkit.getServer();
        for (World world : server.getWorlds()){
            world.getEntities().stream()
                    .filter(e -> e instanceof TNTPrimed)
                    .map(e -> (TNTPrimed) e)
                    .filter(tnt -> tnt.getPersistentDataContainer().has(new NamespacedKey(plugin, "tnt_source")))
                    .forEach(tnt -> tnt.customName(Component.text("|".repeat(tnt.getFuseTicks()/5))));
        }
    }
}
