package net.bzkgns.theFloorIsLavaManager.config;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.lang.LangManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.bzkgns.theFloorIsLavaManager.utils.MenuHolder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

    private final TheFloorIsLavaManager plugin;

    public ConfigGUI(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }

    public static <T extends ConfigSection<T>> void open(Player player, T config) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.CONFIG, config.getName());
        Inventory inv = Bukkit.createInventory(holder, 54,
                LangManager.getInstance().get(player, "gui.config_title", Placeholder.parsed("config_section_name", config.getName())) );
        holder.setInventory(inv);
        List<ConfigKey<T, ?>> keys = config.getKeys();
        int i = 0;
        for (ConfigKey<T,?> key : keys) {
            inv.setItem(i, buildItem(key, config, player));
            i++;
        }
        player.openInventory(inv);
    }

    private static <T extends ConfigSection<?>> ItemStack buildItem(ConfigKey<T,?> key, T config, Audience audience) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LangManager.getInstance().get(audience, "item.config_name", Placeholder.parsed("param" , key.getKey())));
        if (key.get(config) instanceof Number number) {
            meta.lore(List.of(
                    LangManager.getInstance().get(audience, "item_lore.config_value", Placeholder.parsed("value", number.toString())),
                    LangManager.getInstance().get(audience, "item_lore.config_description", Placeholder.parsed("description", key.getDescription())),
                    LangManager.getInstance().get(audience, "item_lore.config_increase"),
                    LangManager.getInstance().get(audience, "item_lore.config_decrease")
            ));
        } else if (key.get(config) instanceof Boolean bool) {
            meta.lore(List.of(
                    LangManager.getInstance().get(audience, "item_lore.config_value", Placeholder.parsed("value", bool.toString())),
                    LangManager.getInstance().get(audience, "item_lore.config_description", Placeholder.parsed("description", key.getDescription())),
                    LangManager.getInstance().get(audience, "item_lore.config_toggle")
            ));
        } else {
            meta.lore(List.of(
                    LangManager.getInstance().get(audience, "item_lore.config_value", Placeholder.parsed("value", key.get(config).toString())),
                    LangManager.getInstance().get(audience, "item_lore.config_description", Placeholder.parsed("description", key.getDescription())),
                    LangManager.getInstance().get(audience, "item_lore.cannot_modify_config_parameter")
            ));
        }
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getClickedInventory() instanceof PlayerInventory) return;
        if(!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;
        if(holder.getType() != MenuHolder.MenuType.CONFIG) return;
        Component title = event.getView().title();

        if (!(title instanceof TextComponent)) {
            return;
        }
        event.setCancelled(true);

        DangerManager dangerManager = plugin.getGameManager().getDangerManager();

        if (!dangerManager.canEditConfig()) {
            Messages.send(player, "error.cannot_modify_config_during_game");
            player.closeInventory();
            return;
        }

        String configName = holder.getConfigName();

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
        System.out.println("Clicked on config key: " + key.getKey() + " with value: " + key.get(config));
        if (key.get(config) instanceof Number number) {
            double current = number.doubleValue();

            int step = event.isShiftClick() ? 10 : 1;
            int direction = event.isRightClick() ? -1 : 1;

            double updated = current + step * direction;

            setNumericValue(key, config, current, updated);

            event.getInventory().setItem(
                    event.getSlot(),
                    buildItem(key, config, player)
            );
        } else if (key.get(config) instanceof Boolean bool) {

            Boolean updated = !bool;

            setBooleanValue(key, config, bool, updated);

            event.getInventory().setItem(
                    event.getSlot(),
                    buildItem(key, config, player)
            );

        } else {
            Messages.send(player, "error.cannot_modify_config_parameter");
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

    @SuppressWarnings("unchecked")
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
            ConfigSection<?> config,
            Audience audience
    ) {
        return buildItem(
                (ConfigKey<ConfigSection<?>, ?>) key,
                config,
                audience
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
