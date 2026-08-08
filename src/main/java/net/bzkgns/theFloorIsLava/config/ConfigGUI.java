package net.bzkgns.theFloorIsLava.config;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.items.ItemManager;
import net.bzkgns.theFloorIsLava.config.lang.LangManager;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.bzkgns.theFloorIsLava.game.RisingManager;
import net.bzkgns.theFloorIsLava.utils.GuiUtils;
import net.bzkgns.theFloorIsLava.utils.TextUtils;
import net.bzkgns.theFloorIsLava.utils.menu.ConfigMenuHolder;
import net.bzkgns.theFloorIsLava.utils.menu.MenuHolder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * GUI d'édition de la configuration. Un item par paramètre "simple" (Number/Boolean) :
 * - clic gauche  = +1 (Shift = +10)
 * - clic droit   = -1 (Shift = -10)
 * <p>
 * Pour les paramètres de type {@link ListConfigKey} (ex: la liste "shop"), chaque
 * élément de la liste est "aplati" en autant d'items que d'{@link ListConfigKey.ElementField}
 * définis (ex: 2 items par ShopItem : un pour "resource", un pour "material"). Chaque
 * champ se comporte comme un paramètre numérique classique (mêmes clics). L'icône et le
 * nom de ces items reprennent le vrai item Minecraft concerné (via elementLabelProvider,
 * ex: "minecraft:flint" -> icône Silex + nom "Flint"). L'ajout et la suppression
 * d'éléments de la liste ne sont PAS gérés par ce GUI : seuls les éléments déjà présents
 * dans la config peuvent être édités.
 * <p>
 * Pagination : 45 items par page (5 lignes), la dernière ligne étant réservée à la
 * navigation (précédent/indicateur/suivant) dès qu'il y a plus d'une page.
 * <p>
 * Reste inopérante si DangerManager#canEditConfig() est false (partie en cours),
 * pour ne jamais laisser modifier la config en plein jeu.
 */
public class ConfigGUI implements Listener {

    private static final int ITEMS_PER_PAGE = 45;
    private static final int SLOT_PREVIOUS_PAGE = 45;
    private static final int SLOT_PAGE_INDICATOR = 49;
    private static final int SLOT_NEXT_PAGE = 53;


    private final TheFloorIsLava plugin;

    public ConfigGUI(TheFloorIsLava plugin) {
        this.plugin = plugin;
    }

    /**
     * Représente un slot du GUI correspondant à un champ éditable d'un élément
     * d'une {@link ListConfigKey} (ex: le "resource" du 3e ShopItem).
     * Types volontairement raw : les entrées du GUI mélangent différentes
     * ListConfigKey<T,E>, seul le runtime garantit la cohérence (comme pour le
     * reste de ce fichier, cf. handleClickUnchecked / buildItemUnchecked).
     */
    @SuppressWarnings("rawtypes")
    private record ListFieldSlot(
            ListConfigKey listKey,
            int index,
            ListConfigKey.ElementField field
    ) {}

    public static <T extends ConfigSection<T>> void open(Player player, T config) {
        openPage(player, config, 0);
    }

    @SuppressWarnings({"rawtypes"})
    private static void openPage(Player player, ConfigSection config, int page) {
        List<Object> allSlots = buildSlots(config);

        int totalPages = Math.max(1, (int) Math.ceil(allSlots.size() / (double) ITEMS_PER_PAGE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        boolean paginated = totalPages > 1;
        int size = paginated ? 54 : inventorySizeFor(allSlots.size());

        ConfigMenuHolder holder = new ConfigMenuHolder(MenuHolder.MenuType.CONFIG, config.getName(), page);
        Inventory inv = Bukkit.createInventory(holder, size,
                LangManager.getInstance().get(player, "gui.config_title", Placeholder.parsed("config_section_name", config.getName())) );
        holder.setInventory(inv);

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allSlots.size());

        for (int i = start; i < end; i++) {
            inv.setItem(i - start, buildSlotItem(allSlots.get(i), config, player));
        }

        if (paginated) {
            if (page > 0) {
                inv.setItem(SLOT_PREVIOUS_PAGE, GuiUtils.navItem(player, GuiUtils.ArrowDirection.LEFT));
            }
            inv.setItem(SLOT_PAGE_INDICATOR, buildPageIndicatorItem(player, page, totalPages));
            if (page < totalPages - 1) {
                inv.setItem(SLOT_NEXT_PAGE,GuiUtils.navItem(player, GuiUtils.ArrowDirection.RIGHT));
            }
        }

        player.openInventory(inv);
    }


    private static ItemStack buildPageIndicatorItem(Audience audience, int page, int totalPages) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LangManager.getInstance().get(audience, "item.config_page_indicator",
                Placeholder.parsed("current", String.valueOf(page + 1)),
                Placeholder.parsed("total", String.valueOf(totalPages))));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Un slot par ConfigKey "simple", ou plusieurs slots par ListConfigKey
     * (un par élément x par champ éditable).
     */
    @SuppressWarnings({"rawtypes"})
    private static List<Object> buildSlots(ConfigSection config) {
        List<Object> slots = new ArrayList<>();

        for (Object keyObj : config.getKeys()) {
            ConfigKey key = (ConfigKey) keyObj;

            if (key instanceof ListConfigKey listKey) {
                addListSlots(slots, listKey, config);
            } else {
                slots.add(key);
            }
        }
        return slots;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addListSlots(List<Object> slots, ListConfigKey listKey, ConfigSection config) {
        List elements = (List) listKey.get(config);

        for (int index = 0; index < elements.size(); index++) {
            for (Object field : listKey.getElementFields()) {
                slots.add(new ListFieldSlot(listKey, index, (ListConfigKey.ElementField) field));
            }
        }
    }

    /**
     * Arrondit à un multiple de 9 (taille de ligne d'inventaire), borné à 54 (6 lignes).
     * Utilisé uniquement quand une seule page suffit (pas de ligne de navigation).
     */
    private static int inventorySizeFor(int slotCount) {
        int size = ((slotCount + 8) / 9) * 9;
        size = Math.max(9, size);
        return Math.min(54, size);
    }

    @SuppressWarnings({"rawtypes"})
    private static ItemStack buildSlotItem(Object entry, ConfigSection config, Audience audience) {
        if (entry instanceof ListFieldSlot listFieldSlot) {
            return buildListFieldItem(listFieldSlot, config, audience);
        }
        return buildItemUnchecked((ConfigKey<?, ?>) entry, config, audience);
    }

    private static <T extends ConfigSection<?>> ItemStack buildItem(ConfigKey<T,?> key, T config, Audience audience) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LangManager.getInstance().get(audience, "item.config_name", Placeholder.parsed("param" , key.getKey())));
        if (key.get(config) instanceof Number number) {
            meta.lore(List.of(
                    LangManager.getInstance().get(audience, "item_lore.config_value", Placeholder.parsed("value", TextUtils.autoClean(number))),
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

    /**
     * Construit l'item pour un champ d'élément de liste (ex: "resource" du ShopItem
     * "minecraft:flint"). L'icône et le nom reprennent l'item Minecraft réellement
     * concerné (résolu depuis l'id renvoyé par elementLabelProvider), pas une simple
     * feuille de papier générique.
     * <p>
     * Nécessite les clés de langue "item.config_list_field_name" (placeholders
     * "element" et "field") en plus des clés "item_lore.config_value/increase/decrease"
     * déjà utilisées pour les paramètres numériques classiques.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ItemStack buildListFieldItem(ListFieldSlot slot, ConfigSection config, Audience audience) {
        Object elements = slot.listKey().get(config);
        Object element = ((List) elements).get(slot.index());

        ListConfigKey.ElementField field = slot.field();
        double value = field.getter().applyAsInt(element);

        String elementId = (String) slot.listKey().getElementLabelProvider().apply(element);
        String fieldLabel = Messages.string(Bukkit.getServer(), field.descriptionTranslationKey());

        Material material = resolveMaterial(elementId);
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LangManager.getInstance().get(audience, "item.config_list_field_name",
                Placeholder.parsed("element", humanizeItemId(elementId)),
                Placeholder.parsed("field", fieldLabel)));
        meta.lore(List.of(
                LangManager.getInstance().get(audience, "item_lore.config_value", Placeholder.parsed("value", TextUtils.autoClean(value))),
                LangManager.getInstance().get(audience, "item_lore.config_increase"),
                LangManager.getInstance().get(audience, "item_lore.config_decrease")
        ));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Résout un id d'item ("minecraft:flint" ou "flint") vers un Material Bukkit.
     * Renvoie null si l'id est inconnu (l'appelant retombe alors sur Material.PAPER).
     */
    private static Material resolveMaterial(String id) {
        if (id == null || id.isBlank()) return null;
        CustomItem customItem = ItemManager.getItemByKey(id);
        if (customItem != null) {
            return customItem.giveItem().getType();
        }
        return Material.matchMaterial(id);
    }

    /**
     * "minecraft:iron_ingot" -> "Iron Ingot". Simple mise en forme lisible,
     * pas une vraie traduction (pas de dépendance à la langue du client ici).
     */
    private static String humanizeItemId(String id) {
        if (id == null) return "";
        String name = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        StringBuilder result = new StringBuilder();
        for (String part : name.split("_")) {
            if (part.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!GuiUtils.isValidInteractMenu(event, MenuHolder.MenuType.CONFIG)) return;
        ConfigMenuHolder holder = (ConfigMenuHolder) event.getInventory().getHolder();
        if (holder == null) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        RisingManager risingManager = plugin.getGameManager().getDangerManager();

        if (!risingManager.canEditConfig()) {
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

        List<Object> allSlots = buildSlots(config);
        int totalPages = Math.max(1, (int) Math.ceil(allSlots.size() / (double) ITEMS_PER_PAGE));
        boolean paginated = totalPages > 1;
        int page = Math.max(0, Math.min(holder.getPage(), totalPages - 1));

        int rawSlot = event.getRawSlot();

        if (paginated && rawSlot == SLOT_PREVIOUS_PAGE && page > 0) {
            openPage(player, config, page - 1);
            return;
        }
        if (paginated && rawSlot == SLOT_NEXT_PAGE && page < totalPages - 1) {
            openPage(player, config, page + 1);
            return;
        }
        if (paginated && rawSlot >= SLOT_PREVIOUS_PAGE) {
            // ligne de navigation (indicateur ou emplacement vide) : rien à faire
            return;
        }

        int overallIndex = page * ITEMS_PER_PAGE + rawSlot;

        if (rawSlot < 0 || overallIndex >= allSlots.size()) {
            return;
        }

        Object entry = allSlots.get(overallIndex);

        if (entry instanceof ListFieldSlot listFieldSlot) {
            handleListFieldClick(listFieldSlot, config, player, event);
            return;
        }

        ConfigKey<?, ?> key = (ConfigKey<?, ?>) entry;
        handleClickUnchecked(key, config, player, event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void handleListFieldClick(
            ListFieldSlot slot,
            ConfigSection config,
            Player player,
            InventoryClickEvent event
    ) {
        List elements = (List) slot.listKey().get(config);
        Object element = elements.get(slot.index());

        ListConfigKey.ElementField field = slot.field();
        double current = field.getter().applyAsInt(element);

        int step = event.isShiftClick() ? 10 : 1;
        int direction = event.isRightClick() ? -1 : 1;
        double updated = current + step * direction;

        Object updatedElement = field.setter().apply(element, updated);

        List updatedElements = new ArrayList<>(elements);
        updatedElements.set(slot.index(), updatedElement);
        slot.listKey().set(config, updatedElements);

        event.getInventory().setItem(
                event.getSlot(),
                buildListFieldItem(slot, config, player)
        );
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

}