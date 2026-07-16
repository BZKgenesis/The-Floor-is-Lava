package net.bzkgns.theFloorIsLavaManager.config;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.plainText;
import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.textF;

/**
 * Point de départ pour un GUI d'édition de la configuration (à faire évoluer avec le
 * même style que TeamGUI/ShopGUI). Un item par paramètre de DangerConfigKey :
 * - clic gauche  = +1 (Shift = +10)
 * - clic droit   = -1 (Shift = -10)
 * <p>
 * Reste inopérante si DangerManager#canEditConfig() est false (partie en cours),
 * pour ne jamais laisser modifier la config en plein jeu.
 */
public class ConfigGUI implements Listener {

    private static final Component TITLE = Component.text("Configuration TFL");

    private final TheFloorIsLavaManager plugin;

    public ConfigGUI(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }

    public static <T extends ConfigSection<T>> void open(Player player, T config) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE.append(Component.text(" - " + config.getName())) );
        List<ConfigKey<T, ?>> keys = config.getKeys();
        int i = 0;
        for (ConfigKey<T,?> key : keys) {
            inv.setItem(i, buildItem(key, config));
            i++;
        }
        player.openInventory(inv);
    }

    private static <T extends ConfigSection<?>> ItemStack buildItem(ConfigKey<T,?> key, T config) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TextUtils.textE(key.getKey()));
        if (key.get(config) instanceof Number number) {
            meta.lore(List.of(
                    TextUtils.text7("Valeur : ").append(textF(number.toString())),
                    TextUtils.text8(key.getDescription()),
                    TextUtils.text8("Clic gauche +1 / Shift +10"),
                    TextUtils.text8("Clic droit -1 / Shift -10")
            ));
        } else if (key.get(config) instanceof Boolean bool) {
            meta.lore(List.of(
                    TextUtils.text7("Valeur : ").append(textF(bool.toString())),
                    TextUtils.text8(key.getDescription()),
                    TextUtils.text8("Clic gauche pour inverser la valeur")
            ));
        } else {
            meta.lore(List.of(
                    TextUtils.text7("Valeur : ").append(textF(key.get(config).toString())),
                    TextUtils.text8(key.getDescription()),
                    TextUtils.text8("Ce paramètre ne se modifie pas au clic.")
            ));
        }
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof PlayerInventory) return;
        if (!plainText(event.getView().title()).startsWith(plainText(TITLE))) return;
        Component title = event.getView().title();

        if (!(title instanceof TextComponent)) {
            return;
        }

        String rawTitle = PlainTextComponentSerializer.plainText().serialize(title);

        if (!rawTitle.startsWith("Configuration TFL - ")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        DangerManager dangerManager = plugin.getGameManager().getDangerManager();

        if (!dangerManager.canEditConfig()) {
            player.sendMessage(TextUtils.errorMessage("La configuration ne peut être modifiée qu'en dehors d'une partie."));
            player.closeInventory();
            return;
        }

        String configName = rawTitle.substring("Configuration TFL - ".length());

        ConfigSection<?> config = ConfigRegistry.getConfigManager(configName).getConfig();

        if (config == null) {
            player.closeInventory();
            return;
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= config.getKeys().size()) {
            return;
        }

        ConfigKey<?, ?> key = config.getKeys().get(slot);

        handleClickUnchecked(key, config, player, event);
    }

    @SuppressWarnings("unchecked")
    private static void handleClickUnchecked(
            ConfigKey<?, ?> key,
            ConfigSection<?> config,
            Player player,
            InventoryClickEvent event
    ) {
        handleClick(
                (ConfigKey<ConfigSection, ?>) key,
                (ConfigSection) config,
                player,
                event
        );
    }

    private static <T extends ConfigSection<T>> void handleClick(
            ConfigKey<T, ?> key,
            T config,
            Player player,
            InventoryClickEvent event
    ) {
        if (key.get(config) instanceof Number number) {
            double current = number.doubleValue();

            int step = event.isShiftClick() ? 10 : 1;
            int direction = event.isRightClick() ? -1 : 1;

            double updated = current + step * direction;

            setNumericValue(key, config, current, updated);

            event.getInventory().setItem(
                    event.getSlot(),
                    buildItem(key, config)
            );
        } else if (key.get(config) instanceof Boolean bool) {

            Boolean updated = !bool;

            setBooleanValue(key, config, bool, updated);

            event.getInventory().setItem(
                    event.getSlot(),
                    buildItem(key, config)
            );

        } else {
            player.sendMessage(TextUtils.errorMessage("Ce paramètre ne se modifie pas au clic."));
        }

    }

    @SuppressWarnings("unchecked")
    private static <T extends ConfigSection<T>> void setNumericValue(
            ConfigKey<?, ?> key,
            ConfigSection<?> config,
            double before,
            double after
    ) {
        ConfigKey<T, Object> typedKey = (ConfigKey<T, Object>) key;
        T typedConfig = (T) config;

        Object value = typedKey.get(typedConfig);

        switch (value) {
            case Integer _ -> typedKey.set(typedConfig, (int) Math.round(after));
            case Double _ -> typedKey.set(typedConfig, after);
            case Float _ -> typedKey.set(typedConfig, (float) after);
            case Long _ -> typedKey.set(typedConfig, Math.round(after));
            case null, default -> throw new IllegalArgumentException("Ce paramètre n'est pas numérique.");
        }
    }

    private static <T extends ConfigSection<T>> void setBooleanValue(
            ConfigKey<?, ?> key,
            ConfigSection<?> config,
            Boolean before,
            Boolean after
    ) {
        ConfigKey<T, Object> typedKey = (ConfigKey<T, Object>) key;
        T typedConfig = (T) config;

        Object value = typedKey.get(typedConfig);

        if (value instanceof Boolean) {
            typedKey.set(typedConfig, after);
        } else {
            throw new IllegalArgumentException("Ce paramètre n'est pas numérique.");
        }
    }

    @SuppressWarnings("unchecked")
    private static ItemStack buildItemUnchecked(
            ConfigKey<?, ?> key,
            ConfigSection<?> config
    ) {
        return buildItem(
                (ConfigKey<ConfigSection<?>, ?>) key,
                config
        );
    }

    // Conserve un entier si la valeur d'origine n'avait pas de décimales
    private static String formatValue(double before, double after) {
        if (before == Math.floor(before)) {
            return Long.toString(Math.round(after));
        }
        return Double.toString(after);
    }
}
