package net.bzkgns.theFloorIsLavaManager.utils.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {

    public enum MenuType {
        TEAM_MAIN,
        TEAM_CONFIRM_LEAVE,
        TEAM_REQUESTS,
        TEAM_MANAGE,
        TEAM_JOIN,

        SHOP,

        CONFIG,

        GIVE_ALL,

        NEW_SHOP_BUY_MENU,
        NEW_SHOP_SELL_MENU,
        NEW_SHOP_MAIN_MENU
    }

    private final MenuType type;
    private Inventory inventory;
    public MenuHolder(MenuType type) {
        this.type = type;
    }

    public MenuType getType() {
        return type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}