package net.bzkgns.theFloorIsLava.listener;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class AutoSmelt implements Listener {
    public static Collection<ItemStack> autoSmeltOre(Collection<ItemStack> items) {
        return items.stream()
                .map(item -> {
                    Material newType = switch (item.getType()) {
                        case RAW_IRON -> Material.IRON_INGOT;
                        case RAW_GOLD -> Material.GOLD_INGOT;
                        case RAW_COPPER -> Material.COPPER_INGOT;
                        default -> item.getType();
                    };

                    return new ItemStack(newType, item.getAmount());
                })
                .toList();
    }
}
