package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.tasks.EggBridgeTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

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
                "items.egg_bridge.display_name",
                "items.egg_bridge.lore",
                Rarity.RARE,
                Material.EGG,
                true
        );

    }



    @Override
    public @Nullable Price getPrice() {
        return new Price(30,45,0);
    }
}
