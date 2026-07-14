package net.bzkgns.theFloorIsLavaManager.Shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean hasEnough(Player p, ItemStack item, int amount) {
        int found = 0;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it != null && it.isSimilar(item)) {
                found += it.getAmount();
                if (found >= amount) return true;
            }
        }
        return false;
    }

    public static void remove(Player p, ItemStack item, int amount) {
        int toRemove = amount;

        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack it = p.getInventory().getItem(i);
            if (it == null || !it.isSimilar(item)) continue;

            int take = Math.min(it.getAmount(), toRemove);
            it.setAmount(it.getAmount() - take);
            toRemove -= take;

            if (it.getAmount() <= 0) {
                p.getInventory().setItem(i, null);
            }
            if (toRemove <= 0) break;
        }
    }
}
