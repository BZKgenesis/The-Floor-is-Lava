package net.bzkgns.theFloorIsLavaManager.lang;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LangManager {

    private static LangManager instance;

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

        File folder = new File(TheFloorIsLavaManager.getInstance().getDataFolder(), "lang");
        folder.mkdirs();

        File file = new File(folder, locale + ".yml");

        if (!file.exists()) {
            TheFloorIsLavaManager.getInstance().saveResource("lang/" + locale + ".yml", false);
        }

        try {
            languages.put(locale, YamlConfiguration.loadConfiguration(file));
        } catch (Exception ignored) {}
    }

    public Component get(Audience audience, String key, TagResolver... placeholders) {

        String locale = ConfigRegistry.getConfigManager("game").getString(GameConfigKeys.DEFAULT_LANG.getKey());
        if (audience instanceof Player player)
            locale = player.locale()
                    .toString()
                    .toLowerCase(Locale.ROOT);

        YamlConfiguration lang = languages.get(locale);

        if (lang == null){
            lang = languages.get("en_us");
            System.out.println("Missing language file for locale " + locale + ", falling back to en_us");
        }

        String text = lang.getString(key);

        if (text == null) {
            text = "<red>Missing translation: " + key;
            System.out.println("Missing translation for key " + key + " in locale " + locale);
        }
        String prefix = lang.getString("prefix", "");

        return mm.deserialize(
                text,
                TagResolver.resolver(
                        Placeholder.parsed("prefix", prefix),
                        TagResolver.resolver(placeholders)
                )
        );
    }

}
