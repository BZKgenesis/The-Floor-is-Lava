package net.bzkgns.theFloorIsLavaManager.utils.menu;

public class ShopSellMenuHolder extends MenuHolder {
    private final Boolean isShowingAllItems;
    public ShopSellMenuHolder(MenuType type, Boolean isShowingAllItems) {
        super(type);
        this.isShowingAllItems = isShowingAllItems;
    }

    public Boolean isShowingAllItems() {
        return isShowingAllItems;
    }
}
