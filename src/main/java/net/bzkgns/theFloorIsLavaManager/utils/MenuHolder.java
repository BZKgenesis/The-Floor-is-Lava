package net.bzkgns.theFloorIsLavaManager.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {

    public enum MenuType {
        TEAM_MAIN, TEAM_CONFIRM_LEAVE, TEAM_REQUESTS, TEAM_MANAGE, TEAM_JOIN, SHOP, CONFIG, GIVE_ALL
    }

    private final MenuType type;
    private Inventory inventory;
    private final int page;
    private final String configName;

    public MenuHolder(MenuType type) {
        this(type, 0, null);
    }

    public MenuHolder(MenuType type, int page) {
        this(type, page, null);
    }

    public MenuHolder(MenuType type, String configName) {
        this(type, 0, configName);
    }

    public MenuHolder(MenuType type, int page, String configName) {
        this.type = type;
        this.page = page;
        this.configName = configName;
    }

    public MenuType getType() {
        return type;
    }

    public String getConfigName() {
        return configName;
    }

    public int getPage() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}