package net.bzkgns.theFloorIsLava.lang;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class LangManager {

    private static LangManager instance;

    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    private final Map<String, YamlConfiguration> languages = new HashMap<>();

    private final MiniMessage mm = MiniMessage.miniMessage();

    private LangManager() {}

    public static LangManager getInstance() {
        if (instance == null) {
            instance = new LangManager();
        }
        return instance;
    }

    public void load() {

        loadLanguage("fr_fr");
        loadLanguage("en_us");

    }

    private void loadLanguage(String locale) {

        File folder = new File(TheFloorIsLava.getInstance().getDataFolder(), "lang");
        boolean _ = folder.mkdirs();

        File file = new File(folder, locale + ".yml");

        if (!file.exists()) {
            TheFloorIsLava.getInstance().saveResource("lang/" + locale + ".yml", false);
        }

        try {
            languages.put(locale, YamlConfiguration.loadConfiguration(file));
        } catch (Exception ignored) {}
    }

    public static Locale getLocale(Audience audience) {
        String locale = ConfigRegistry.getConfigManager("game")
                .getString(GameConfigKeys.DEFAULT_LANG.getKey());

        if (audience instanceof Player player) {
            locale = player.locale()
                    .toString()
                    .toLowerCase(Locale.ROOT);
        }

        return Locale.forLanguageTag(locale.replace('_', '-'));
    }

    public Component get(Audience audience, String key, TagResolver... placeholders) {

        String locale = ConfigRegistry.getConfigManager("game")
                .getString(GameConfigKeys.DEFAULT_LANG.getKey());

        if (audience instanceof Player player) {
            locale = player.locale()
                    .toString()
                    .toLowerCase(Locale.ROOT);
        }

        YamlConfiguration lang = languages.get(locale);

        if (lang == null) {
            locale = "en_us";
            lang = languages.get(locale);
            plugin.getLogger().warning("Missing language file for locale '" + locale + "', falling back to en_us.");
        }

        String text = lang.getString(key);

        if (TheFloorIsLava.getDebugMode()){
            if (languages.get("fr_fr").getString(key) == null) {
                reportMissingTranslation("fr_fr", key);
                plugin.getLogger().warning("Missing translation '" + key + "' for locale 'fr_fr'.");
            }

            if (languages.get("en_us").getString(key) == null) {
                reportMissingTranslation("en_us", key);
                plugin.getLogger().warning("Missing translation '" + key + "' for locale 'en_us'.");
            }
        }

        if (text == null) {
            plugin.getLogger().warning("Missing translation '" + key + "' for locale '" + locale + "'.");
            text = "<red>Missing translation: " + key;
        }

        String prefix = lang.getString("prefix", "");
        String prefixOp = lang.getString("prefix_op", "");

        return mm.deserialize(
                text,
                TagResolver.resolver(
                        Placeholder.parsed("prefix", prefix),
                        Placeholder.parsed("prefix_op", prefixOp),
                        TagResolver.resolver(placeholders)
                )
        );
    }
    private final Set<String> missingTranslations = new HashSet<>();

    private void reportMissingTranslation(String locale, String key) {

        String uniqueKey = locale + ":" + key;

        if (!missingTranslations.add(uniqueKey)) {
            return;
        }

        File file = new File(
                new File(TheFloorIsLava.getInstance().getDataFolder(), "lang"),
                "missing_translation_" + locale + ".txt"
        );

        try {
            if (!file.exists()) {
                boolean _ = file.createNewFile();
            }

            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(key + ": \"\"\n");
            }

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error writing missing translation for locale '" + locale + "' and key '" + key + "'", e);
        }
    }

}
