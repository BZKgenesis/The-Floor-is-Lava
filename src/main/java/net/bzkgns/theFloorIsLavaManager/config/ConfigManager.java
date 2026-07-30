package net.bzkgns.theFloorIsLavaManager.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigManager<T extends ConfigSection<T>> {

    private boolean hasBeenModified = false;

    private final T config;

    private final File configFile;

    private final Map<String, ConfigKey<T, ?>> keys;

    public ConfigManager(T config, File configFile) {
        this.config = config;
        this.configFile = configFile;

        this.keys = config.getKeys()
                .stream()
                .collect(Collectors.toMap(
                        ConfigKey::getKey,
                        key -> key
                ));
    }

    public ConfigKey<T, ?> getKey(String key){
        return keys.get(key);
    }

    @SuppressWarnings("unused")
    public List<? extends ConfigKey<T, ?>> getKeys() {
        return List.copyOf(keys.values());
    }


    public Object get(String key) {
        ConfigKey<T, ?> configKey = keys.get(key);

        if (configKey == null)
            throw new IllegalArgumentException("Unknown key " + key);

        return configKey.get(config);
    }

    public Integer getInt(ConfigKey<T,Integer> key) {
        return getInt(key.getKey());
    }

    public Integer getInt(String key) {
        try {
            return (Integer) this.get(key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Key " + key + " is not an Integer");
        }
    }

    public Double getDouble(ConfigKey<T,Double> key) {
        return getDouble(key.getKey());
    }

    public Double getDouble(String key) {
        try {
            return (Double) this.get(key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Key " + key + " is not an Integer");
        }
    }

    public Boolean getBoolean(ConfigKey<T,Boolean> key) {
        return getBoolean(key.getKey());
    }
    public Boolean getBoolean(String key) {
        try {
            return (Boolean) this.get(key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Key " + key + " is not an Integer");
        }
    }

    @SuppressWarnings("unused")
    public String getString(String key) {
        try {
            return (String) this.get(key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Key " + key + " is not an Integer");
        }
    }


    public void set(String key, String value) {
        ConfigKey<T, ?> configKey = keys.get(key);

        if (configKey == null)
            throw new IllegalArgumentException("Unknown key " + key);
        hasBeenModified = true;
        configKey.setFromString(config, value);
    }

    @SuppressWarnings("unused")
    public void load(FileConfiguration fileConfig) {
        hasBeenModified = false;

        for (ConfigKey<T, ?> key : keys.values()) {
            loadKey(fileConfig, key);
        }
    }

    @SuppressWarnings("unused")
    public void save(FileConfiguration fileConfig) {

        for (ConfigKey<T, ?> key : keys.values()) {
            saveKey(fileConfig,key);
        }
    }


    @SuppressWarnings("unchecked")
    private <R> void loadKey(
            FileConfiguration fileConfig,
            ConfigKey<T,R> key
    ) {

        Object value = fileConfig.get(config.getName() + "." + key.getKey());

        if (value != null) {
            System.out.println(key.getKey() + " : " + value);
            key.set(config, (R)value);
        }
    }

    private <R> void saveKey(
            FileConfiguration fileConfig,
            ConfigKey<T,R> key
    ) {
        fileConfig.set(
                config.getName() + "." + key.getKey(),
                key.get(config)
        );
    }

    public T getConfig() {
        return config;
    }

    private void saveToFile(File file) {
        FileConfiguration yaml = new YamlConfiguration();
        hasBeenModified = false;

        save(yaml);

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de sauvegarder la configuration " + file.getName(),
                    e
            );
        }
    }

    public void loadFromFile(File file) {
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        hasBeenModified = false;

        load(yaml);
    }

    public void saveConfig() {
        saveToFile(configFile);
    }

    public void loadConfig() {
        loadFromFile(configFile);
    }

    public boolean hasBeenModified() {
        return hasBeenModified;
    }
}