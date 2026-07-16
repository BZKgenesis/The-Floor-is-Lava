package net.bzkgns.theFloorIsLavaManager.config;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
        meta.displayName(Component.text("§e" + key.getKey()));
        meta.lore(List.of(
                Component.text("§7Valeur : §f" + key.get(config)),
                Component.text("§8" + key.getDescription()),
                Component.text("§8Clic gauche +1 / Shift +10"),
                Component.text("§8Clic droit -1 / Shift -10")
        ));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
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
            player.sendMessage("§cLa configuration ne peut être modifiée qu'en dehors d'une partie.");
            player.closeInventory();
            return;
        }

        String configName = rawTitle.substring("Configuration TFL - ".length());

        ConfigSection<?> config = plugin.getConfigManager(configName).getConfig();

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
        if (!(key.get(config) instanceof Number number)) {
            player.sendMessage("§cCe paramètre ne se modifie pas au clic.");
            return;
        }

        double current = number.doubleValue();

        int step = event.isShiftClick() ? 10 : 1;
        int direction = event.isRightClick() ? -1 : 1;

        double updated = current + step * direction;

        setNumericValue(key, config, current, updated);

        event.getInventory().setItem(
                event.getSlot(),
                buildItem(key, config)
        );

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

        if (value instanceof Integer) {
            typedKey.set(typedConfig, (int) Math.round(after));
        } else if (value instanceof Double) {
            typedKey.set(typedConfig, after);
        } else if (value instanceof Float) {
            typedKey.set(typedConfig, (float) after);
        } else if (value instanceof Long) {
            typedKey.set(typedConfig, Math.round(after));
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
                (ConfigKey<ConfigSection, ?>) key,
                (ConfigSection) config
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
