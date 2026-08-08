package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.tasks.TntTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class TntItem extends CustomItem {

    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    private static int tntTask = -1;


    public TntItem() {
        if (tntTask == -1){
            tntTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new TntTask(), 1, 1);
        }
        super("tnt",
                Rarity.RARE,
                Material.TNT,
                true);
    }
}
