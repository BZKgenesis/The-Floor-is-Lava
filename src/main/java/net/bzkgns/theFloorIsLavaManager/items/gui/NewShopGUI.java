package net.bzkgns.theFloorIsLavaManager.items.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.bzkgns.theFloorIsLavaManager.currency.PlayerBalance;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.bzkgns.theFloorIsLavaManager.items.ItemManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.shop.*;
import net.bzkgns.theFloorIsLavaManager.utils.GuiUtils;
import net.bzkgns.theFloorIsLavaManager.utils.menu.MenuHolder;
import net.bzkgns.theFloorIsLavaManager.utils.menu.PageMenuHolder;
import net.bzkgns.theFloorIsLavaManager.utils.menu.ShopSellMenuHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.bzkgns.theFloorIsLavaManager.utils.SoundUtils.*;
import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.plainText;

import static net.bzkgns.theFloorIsLavaManager.utils.GuiUtils.*;
@SuppressWarnings("UnstableApiUsage")
public class NewShopGUI implements Listener {

    private static final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    private static final int SIZE_BUY_MENU = 54;
    private static final int SIZE_SELL_MENU = 54;
    private static final int SIZE_MAIN_MENU = 27;
    private static final int USABLE_SIZE_BUY_MENU = 54-9;


    public static void openMainMenu(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.NEW_SHOP_MAIN_MENU);
        Inventory inv = Bukkit.createInventory(holder, SIZE_MAIN_MENU, Messages.component(p,"gui.shop.title"));
        holder.setInventory(inv);

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        inv.setItem(11, createItem(Material.EMERALD, "BUY", "buy_button", ""));

        inv.setItem(15, createItem(Material.EMERALD, "SELL", "sell_button", ""));

        p.openInventory(inv);
    }


    public static void openSellMenu(Player p, Boolean isShowingAllItems) {
        ShopSellMenuHolder holder = new ShopSellMenuHolder(MenuHolder.MenuType.NEW_SHOP_SELL_MENU, isShowingAllItems);
        Inventory inv = Bukkit.createInventory(holder, SIZE_SELL_MENU, Messages.component(p,"gui.shop.title"));
        holder.setInventory(inv);

        Map<Material, Price> sellableItems = ItemManager.getAllSellableMap();

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        if (isShowingAllItems) {
            int i = 0;
            for (Map.Entry<Material, Price> entry : sellableItems.entrySet())
            {
                if (entry == null) continue;
                Price price = entry.getValue();
                if (price == null) continue;
                if (i >= USABLE_SIZE_BUY_MENU) break;
                Material mat = entry.getKey();

                ItemStack displayStack = new ItemStack(mat);
                ItemMeta meta = displayStack.getItemMeta();
                List<Component> lore = new ArrayList<>();
                lore.add(price.displayResource(p));
                lore.add(price.displayMaterial(p));
                lore.add(Messages.component(p, "gui.shop.click_to_exchange"));
                meta.lore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "buttonId"), PersistentDataType.STRING, "sellable_item");
                displayStack.setItemMeta(meta);

                inv.setItem(i, displayStack);
                i++;
            }
        }else{
            int i = 0;
            for (ItemStack entry : p.getInventory().getContents())
            {
                if (entry == null) continue;
                Price price = sellableItems.get(entry.getType());
                if (price == null) continue;
                if (i >= USABLE_SIZE_BUY_MENU) break;
                Material mat = entry.getType();

                ItemStack displayStack = new ItemStack(mat);
                ItemMeta meta = displayStack.getItemMeta();
                List<Component> lore = new ArrayList<>();
                lore.add(price.displayResource(p));
                lore.add(price.displayMaterial(p));
                lore.add(Messages.component(p, "gui.shop.click_to_exchange"));
                meta.lore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "buttonId"), PersistentDataType.STRING, "sellable_item");
                displayStack.setItemMeta(meta);

                inv.setItem(i, displayStack);
                i++;
            }
        }

        inv.setItem(45, createBackItem(p));


        inv.setItem(49, createItem(Material.NAME_TAG, (isShowingAllItems ? "Show only items in inventory" : "Show all sellable items"), "toggle_show_all", ""));

        p.openInventory(inv);
    }


    public static void openBuyMenu(Player p, int page) {
        List<CustomItem> buyableItems = ItemManager.getAllBuyableItemStacks(p);
        PageMenuHolder holder = new PageMenuHolder(MenuHolder.MenuType.NEW_SHOP_BUY_MENU, page);
        Inventory inv = Bukkit.createInventory(holder, SIZE_BUY_MENU, Messages.component(p,"gui.shop.title", Placeholder.unparsed("page", String.valueOf(page+1))));
        holder.setInventory(inv);
        Map<Integer, IngredientDisplay> animated = new HashMap<>();

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        int nbItems = min(buyableItems.size(), USABLE_SIZE_BUY_MENU) ;
        int startIndex = page * USABLE_SIZE_BUY_MENU;

        for (int itemIndex = startIndex; itemIndex < startIndex + nbItems; itemIndex++) {
            if (itemIndex >= buyableItems.size()) break;
            CustomItem display = buyableItems.get(itemIndex);
            ItemStack displayStack = display.giveItem(p);

            List<Component> display_lore = displayStack.lore();
            if (display_lore == null) display_lore = new ArrayList<>();
            Price displayPrice = display.getPrice();
            if (displayPrice != null) {
                display_lore.add(displayPrice.displayResource(p, balance.hasEnoughResource(displayPrice)));
                display_lore.add(displayPrice.displayMaterial(p, balance.hasEnoughMaterial(displayPrice)));
            }
            display_lore.add(Messages.component(p, "gui.shop.click_to_exchange"));
            displayStack.lore(display_lore);
            inv.setItem(itemIndex-startIndex, displayStack);

        }

        // boutons
        if (page > 0)
            inv.setItem(46, navItem(Messages.component(p, "gui.shop.previous_page"), ArrowDirection.LEFT));

        inv.setItem(45, createBackItem(p));

        if (page < getMaxPages())
            inv.setItem(53, navItem(Messages.component(p, "gui.shop.next_page"), ArrowDirection.RIGHT));

        p.openInventory(inv);

    }


    enum ArrowDirection {
        LEFT, RIGHT
    }

    private static ItemStack navItem(Component name, ArrowDirection direction) {
        ItemStack it = new ItemStack(Material.ARROW);
        switch (direction){
            case LEFT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("left"));
            case RIGHT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("right"));
        }

        ItemMeta meta = it.getItemMeta();
        meta.displayName(name);
        it.setItemMeta(meta);
        return it;
    }

    private static int getMaxPages(){
        return ItemManager.getAllBuyableItemStacks(Bukkit.getServer()).size()/ USABLE_SIZE_BUY_MENU;
    }

    private static int page(InventoryClickEvent event) {
        String title = plainText(event.getView().title());

        int start = title.indexOf("Page ") + 5;
        int end = title.indexOf(")", start);

        try {
            return max(0, Integer.parseInt(title.substring(start, end)) - 1);
        } catch (Exception ex) {
            return 0;
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!GuiUtils.isValidInteractMenu(event, MenuHolder.MenuType.NEW_SHOP_BUY_MENU, MenuHolder.MenuType.NEW_SHOP_SELL_MENU, MenuHolder.MenuType.NEW_SHOP_MAIN_MENU)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;
        switch (holder.getType()){
            case NEW_SHOP_BUY_MENU -> handleBuyMenu(event);
            case NEW_SHOP_SELL_MENU -> handleSellMenu(event);
            case NEW_SHOP_MAIN_MENU -> handleMainMenu(event);
        }
    }

    private void handleBuyMenu(InventoryClickEvent event){
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (isBackItem(event.getCurrentItem())){
            openMainMenu(player);
            return;
        }

        if (event.getSlot() == 46) openBuyMenu(player, max(page(event)-1,0));
        if (event.getSlot() == 53) openBuyMenu(player, min(getMaxPages(),page(event)+1));

        int idx = event.getSlot() + page(event)* USABLE_SIZE_BUY_MENU;
        if (idx >= ItemManager.getAllBuyableItemStacks(player).size()) return;

        CustomItem item = ItemManager.getAllBuyableItemStacks(player).get(idx);

        if (player.getGameMode() != GameMode.CREATIVE){
            if (plugin.getGameManager().getMoneyManager().subtractBalance(player.getUniqueId(), item.getPrice())) {
                player.getInventory().addItem(item.giveItem(player));
                Messages.send(player, "shop.exchange_success");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
                openBuyMenu(player, page(event));
            }else{
                Messages.send(player, "shop.not_enough_ingredients");
                playError(player);
            }
        }
    }

    private void handleSellMenu(InventoryClickEvent event){
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (isBackItem(event.getCurrentItem())){
            openMainMenu(player);
            return;
        }
        ShopSellMenuHolder holder = (ShopSellMenuHolder)event.getView().getTopInventory().getHolder();
        if (holder == null) return;

        if (Objects.equals(getButtonId(event.getCurrentItem()), "sellable_item")){
            Price price = ItemManager.getAllSellableMap().get(event.getCurrentItem().getType());
            if (price == null) return;
            if (player.getGameMode() != GameMode.CREATIVE){
                int amountToRemove = event.isShiftClick()?64:1;
                int totalRemoved = 0;
                for (ItemStack item :player.getInventory().getContents()){
                    if (item == null) continue;
                    if (item.getType() == event.getCurrentItem().getType()){
                        int removeAmount = min(item.getAmount(), amountToRemove);
                        item.setAmount(item.getAmount() - removeAmount);
                        amountToRemove -= removeAmount;
                        totalRemoved += removeAmount;
                        if (amountToRemove <= 0) break;
                    }
                }
                if (totalRemoved <= 0) {
                    Messages.send(player, "shop.not_enough_ingredients");
                    playError(player);
                    return;
                }
                plugin.getGameManager().getMoneyManager().addBalance(player.getUniqueId(), price.mul(totalRemoved));
                Messages.send(player, "shop.exchange_success");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
            }
        }

        if (Objects.equals(getButtonId(event.getCurrentItem()), "toggle_show_all")){
            openSellMenu(player, !(holder).isShowingAllItems());
        }
    }

    private void handleMainMenu(InventoryClickEvent event){
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (Objects.equals(getButtonId(event.getCurrentItem()), "buy_button")){
            openBuyMenu(player,0);
            return;
        }
        if (Objects.equals(getButtonId(event.getCurrentItem()), "sell_button")){
            openSellMenu(player, false);
            return;
        }
    }
}