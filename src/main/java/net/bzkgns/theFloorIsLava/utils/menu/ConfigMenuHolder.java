package net.bzkgns.theFloorIsLava.utils.menu;

public class ConfigMenuHolder extends MenuHolder{
    private final String configName;
    private final int page;
    public ConfigMenuHolder(MenuType type, String configName, int page) {
        super(type);
        this.configName = configName;
        this.page = page;
    }

    public String getConfigName() {
        return configName;
    }
    public int getPage() { return page; }
}
