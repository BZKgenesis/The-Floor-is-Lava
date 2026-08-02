package net.bzkgns.theFloorIsLava.config.shop;

import net.bzkgns.theFloorIsLava.currency.Price;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ShopBuyVanillaItem(String id, int quantity, int resource, int material) {
    public ShopBuyVanillaItem(String id, int resource, int material) {
        this(id, 1, resource, material);
    }

    @Nullable
    public ItemStack toItemStack() {
        Material mat = Material.getMaterial(id.toUpperCase());
        if (mat == null) {
            return null;
        }
        return new ItemStack(mat, quantity);
    }

    public Price toPrice() {
        return new Price(resource, material, 0);
    }
}
