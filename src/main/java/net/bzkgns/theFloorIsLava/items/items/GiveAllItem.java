package net.bzkgns.theFloorIsLava.items.items;

import net.bzkgns.theFloorIsLava.items.CustomItem;
import org.bukkit.Material;

public class GiveAllItem extends CustomItem {
    public GiveAllItem() {
        super("give_all",
                "items.give_all.display_name",
                "items.give_all.lore",
                Rarity.COMMON,
                Material.BOOK,
                true);
    }

}
