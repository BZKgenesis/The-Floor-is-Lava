package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.tasks.EggBridgeTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class EggBridgeItem extends CustomItem {

    private static int eggBridgeTask = -1;

    public EggBridgeItem() {
        if (eggBridgeTask == -1) {
            eggBridgeTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLava.getInstance(),
                    new EggBridgeTask(),
                    0L,
                    1L
            );
        }
        super("egg_bridge",
                Rarity.RARE,
                Material.EGG,
                true
        );

    }
}
