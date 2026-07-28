package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.bzkgns.theFloorIsLavaManager.tasks.TntTask;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

public class TntItem extends CustomItem {

    private static final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    private static int tntTask = -1;


    public TntItem() {
        if (tntTask == -1){
            tntTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new TntTask(), 1, 1);
        }
        super("tnt",
                "items.tnt.display_name",
                "items.tnt.lore",
                Rarity.RARE,
                Material.TNT,
                true);
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(25,35,0);
    }
}
