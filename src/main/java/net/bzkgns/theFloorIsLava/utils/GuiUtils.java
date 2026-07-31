package net.bzkgns.theFloorIsLava.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.bzkgns.theFloorIsLava.utils.menu.MenuHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class GuiUtils {

    public static ItemStack createItem(Material mat, String name, String customModelData) {
        return createItem(mat, name, null, customModelData);
    }

    public static ItemStack createItem(Material mat, String name, @SuppressWarnings("SameParameterValue") String id, String customModelData) {
        ItemStack it = new ItemStack(mat);
        if (!customModelData.isBlank()) {
            it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(customModelData).build());
        }
        ItemMeta m = it.getItemMeta();
        m.displayName(Component.text(name).color(TextColor.fromHexString("#FFFF55")));
        if (id != null)
            m.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLava.class), "buttonId"), PersistentDataType.STRING, id);
        it.setItemMeta(m);
        return it;
    }



    public enum ArrowDirection {
        LEFT, RIGHT
    }

    public static ItemStack navItem(Component name, ArrowDirection direction) {
        ItemStack it = new ItemStack(Material.ARROW);
        switch (direction){
            case LEFT -> {
                it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("left"));
                ItemMeta meta = it.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING, "left");
                it.setItemMeta(meta);
            }
            case RIGHT -> {
                it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("right"));
                ItemMeta meta = it.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING, "right");
                it.setItemMeta(meta);
            }
        }

        ItemMeta meta = it.getItemMeta();
        meta.displayName(name);
        it.setItemMeta(meta);
        return it;
    }

    public static boolean isNavItem(ItemStack stack, ArrowDirection direction) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        String buttonId = meta.getPersistentDataContainer().get(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING);
        if (buttonId==null) return false;
        if (buttonId.isEmpty()) return false;
        switch (direction){
            case LEFT -> {
                return "left".equals(buttonId);
            }
            case RIGHT -> {
                return "right".equals(buttonId);
            }
        }
        return false;
    }



    // Variantes acceptant un Component (utilisées pour les libellés traduits via LangManager/Messages)
    public static ItemStack createItem(Material mat, Component name) {
        return createItem(mat, name, null, "");
    }

    public static ItemStack createItem(Material mat, Component name, String customModelData) {
        return createItem(mat, name, null, customModelData);
    }

    public static ItemStack createItem(Material mat, Component name, String id, String customModelData) {
        ItemStack it = new ItemStack(mat);
        if (customModelData != null && !customModelData.isBlank()) {
            it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(customModelData).build());
        }
        ItemMeta m = it.getItemMeta();
        m.displayName(name.color(TextColor.fromHexString("#FFFF55")));
        if (id != null)
            m.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING, id);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack createBackItem(Player p) {
        ItemStack it = new ItemStack(Material.ARROW);
        it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("back").build());
        ItemMeta m = it.getItemMeta();
        m.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING, "back");
        m.displayName(Messages.component(p, "button.back").color(TextColor.fromHexString("#FF5555")));
        it.setItemMeta(m);
        return it;
    }

    /**
     * Récupère l'identifiant du bouton stocké dans le CustomModelData de l'item.
     * On utilise cet identifiant (fixe, non traduit) pour router les clics,
     * plutôt que le texte affiché qui change selon la langue du joueur.
     */
    public static String getButtonCustomModelData(ItemStack stack) {
        if (stack == null) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasCustomModelDataComponent()) {
            List<String> strings = meta.getCustomModelDataComponent().getStrings();
            if (!strings.isEmpty())
                return strings.getFirst();
        }
        return null;
    }

    public static String getButtonId(ItemStack stack) {
        if (stack == null) return null;
        return stack.getPersistentDataContainer().get(new NamespacedKey(TheFloorIsLava.getInstance(), "buttonId"), PersistentDataType.STRING);
    }

    public static boolean isBackItem(ItemStack stack) {
        return "back".equals(getButtonId(stack));
    }

    public static boolean isValidInteractMenu(InventoryClickEvent event, MenuHolder.MenuType... menuTypes){
        if (!(event.getWhoClicked() instanceof Player player)) return false;

        if(!(event.getInventory().getHolder() instanceof MenuHolder holder)) return false;
        if(!List.of(menuTypes).contains(holder.getType()) ) return false;
        if (event.getClickedInventory() instanceof CraftInventoryPlayer) {
            switch (event.getAction()){
                case MOVE_TO_OTHER_INVENTORY -> event.setCancelled(true);
                case COLLECT_TO_CURSOR -> {
                    event.setCancelled(true);

                    ItemStack cursor = event.getCursor();
                    if (cursor.getType().isAir()) return false;

                    int availableSpace = cursor.getMaxStackSize() - cursor.getAmount();

                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) continue;

                        if (!item.isSimilar(cursor))
                            continue;

                        int removeAmount = Math.min(item.getAmount(), availableSpace);
                        item.setAmount(item.getAmount() - removeAmount);
                        availableSpace -= removeAmount;
                        cursor.setAmount(cursor.getAmount() + removeAmount);

                        if (availableSpace <= 0) break;
                    }

                }
            }
            return false;
        }
        return event.getClickedInventory() instanceof CraftInventoryCustom;
    }
}
