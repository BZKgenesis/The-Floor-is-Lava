package net.bzkgns.theFloorIsLavaManager.utils.menu;

public class PageMenuHolder extends MenuHolder {
    private final int page;
    public PageMenuHolder(MenuType type, int page) {
        super(type);
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
