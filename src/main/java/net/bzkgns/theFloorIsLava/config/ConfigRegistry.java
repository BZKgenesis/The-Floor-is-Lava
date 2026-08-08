package net.bzkgns.theFloorIsLava.config;

import java.util.HashMap;
import java.util.Map;

public final class ConfigRegistry {

    private static final Map<String, ConfigManager<?>> configManagers = new HashMap<>();

    private ConfigRegistry() {}

    public static void addConfig(ConfigManager<?> configManager) {
        configManagers.put(configManager.getConfig().getName(), configManager);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ConfigSection<T>> ConfigManager<T> getConfigManager(String configName) {
        return (ConfigManager<T>) configManagers.get(configName);
    }

    public static Map<String, ConfigManager<?>> getConfigManagers() {
        return configManagers;
    }
}
