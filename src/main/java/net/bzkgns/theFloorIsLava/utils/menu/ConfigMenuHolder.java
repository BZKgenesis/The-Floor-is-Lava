package net.bzkgns.theFloorIsLava.utils.menu;

public class ConfigMenuHolder extends MenuHolder{
    private final String configName;
    public ConfigMenuHolder(MenuType type, String configName) {
        super(type);
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }
}
