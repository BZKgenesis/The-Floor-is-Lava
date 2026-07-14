package net.bzkgns.theFloorIsLavaManager;

import net.bzkgns.theFloorIsLavaManager.DangerZone.DangerConfig;
import net.bzkgns.theFloorIsLavaManager.DangerZone.DangerConfigKey;
import net.bzkgns.theFloorIsLavaManager.DangerZone.DangerManager;
import net.kyori.adventure.text.Component;
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
 * Point de départ pour une GUI d'édition de la configuration (à faire évoluer avec le
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

    public static void open(Player player, DangerManager dangerManager) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        DangerConfigKey[] keys = DangerConfigKey.values();
        for (int i = 0; i < keys.length && i < inv.getSize(); i++) {
            inv.setItem(i, buildItem(keys[i], dangerManager.getConfig()));
        }
        player.openInventory(inv);
    }

    private static ItemStack buildItem(DangerConfigKey key, DangerConfig config) {
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
        if (!TITLE.equals(event.getView().title())) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        DangerManager dangerManager = plugin.getDangerManagerInstance();

        if (!dangerManager.canEditConfig()) {
            player.sendMessage("§cLa configuration ne peut être modifiée qu'en dehors d'une partie.");
            player.closeInventory();
            return;
        }

        int slot = event.getSlot();
        DangerConfigKey[] keys = DangerConfigKey.values();
        if (slot < 0 || slot >= keys.length) return;

        DangerConfigKey key = keys[slot];
        int step = event.isShiftClick() ? 10 : 1;
        int direction = event.isRightClick() ? -1 : 1;

        try {
            double current = Double.parseDouble(key.get(dangerManager.getConfig()));
            double updated = current + (step * direction);
            key.set(dangerManager.getConfig(), formatValue(current, updated));
        } catch (NumberFormatException e) {
            player.sendMessage("§cCe paramètre ne se modifie pas au clic (valeur non numérique : " + key.get(dangerManager.getConfig()) + ").");
            return;
        }

        event.getInventory().setItem(slot, buildItem(key, dangerManager.getConfig()));
    }

    // Conserve un entier si la valeur d'origine n'avait pas de décimales
    private static String formatValue(double before, double after) {
        if (before == Math.floor(before)) {
            return Long.toString(Math.round(after));
        }
        return Double.toString(after);
    }
}
