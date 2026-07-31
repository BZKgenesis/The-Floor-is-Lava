package net.bzkgns.theFloorIsLava.utils.menu;

public class ShopSellMenuHolder extends PageMenuHolder {
    private final Boolean isShowingAllItems;
    public ShopSellMenuHolder(MenuType type, int page, Boolean isShowingAllItems) {
        super(type,page);
        this.isShowingAllItems = isShowingAllItems;
    }

    public Boolean isShowingAllItems() {
        return isShowingAllItems;
    }
}
