package io.github.pikayorld.theFloorIsLavaManager;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TeamInventory implements InventoryHolder {
    private final Inventory inventory;

    public TeamInventory(Plugin plugin,int size) {
        this.inventory = plugin.getServer().createInventory(this,size);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
