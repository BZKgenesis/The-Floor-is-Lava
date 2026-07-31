package net.bzkgns.theFloorIsLava.config;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;

import java.io.File;

public class ConfigLoader {

    private ConfigLoader() {}

    public static <T extends ConfigSection<T>>
    ConfigManager<T> load(T defaultConfig) {

        return load(
                defaultConfig,
                pluginConfigFile(defaultConfig.getName()),
                true
        );
    }

    public static <T extends ConfigSection<T>>
    ConfigManager<T> load(T defaultConfig, File file, boolean createIfNotExists) {

        ConfigManager<T> manager =
                new ConfigManager<>(defaultConfig, file);

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            Boolean _ = parent.mkdirs();
        }

        if (!file.exists()) {
            if (createIfNotExists)
                manager.saveConfig();
        } else {
            manager.loadConfig();
        }

        return manager;
    }


    public static File pluginConfigFile(String name) {
        return new File(
                TheFloorIsLava.getInstance().getDataFolder(),
                name + ".yml"
        );
    }
}
