package net.bzkgns.theFloorIsLava.items.gui;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.game.currency.PlayerBalance;
import net.bzkgns.theFloorIsLava.game.currency.Price;
import net.bzkgns.theFloorIsLava.items.ItemManager;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.bzkgns.theFloorIsLava.utils.GuiUtils;
import net.bzkgns.theFloorIsLava.utils.menu.MenuHolder;
import net.bzkgns.theFloorIsLava.utils.menu.PageMenuHolder;
import net.bzkgns.theFloorIsLava.utils.menu.ShopSellMenuHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.util.Tuple;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.bzkgns.theFloorIsLava.utils.GuiUtils.*;
import static net.bzkgns.theFloorIsLava.utils.SoundUtils.playError;

public class ShopGUI implements Listener {
    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    private static final int ROW_SIZE = 9;
    private static final int SIZE_BUY_MENU = ROW_SIZE*4;
    private static final int SIZE_SELL_MENU = ROW_SIZE*6;
    private static final int SIZE_MAIN_MENU = ROW_SIZE*3;
    private static final int USABLE_SIZE_BUY_MENU = SIZE_BUY_MENU-ROW_SIZE;
    private static final int USABLE_SIZE_SELL_MENU = SIZE_SELL_MENU-ROW_SIZE;


    public static void openMainMenu(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.NEW_SHOP_MAIN_MENU);
        Inventory inv = Bukkit.createInventory(holder, SIZE_MAIN_MENU, Messages.component(p,"gui.shop.main_title"));
        holder.setInventory(inv);

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        inv.setItem(11, createItem(Material.EMERALD, Messages.string(p, "gui.shop.buy"), "buy_button", ""));

        inv.setItem(15, createItem(Material.EMERALD, Messages.string(p, "gui.shop.sell"), "sell_button", ""));

        p.openInventory(inv);
    }


    public static void openSellMenu(Player p, Boolean isShowingAllItems, int page) {
        ShopSellMenuHolder holder = new ShopSellMenuHolder(MenuHolder.MenuType.NEW_SHOP_SELL_MENU, page, isShowingAllItems);
        Inventory inv = Bukkit.createInventory(holder, SIZE_SELL_MENU, Messages.component(p,"gui.shop.sell_title", Placeholder.unparsed("page", String.valueOf(page+1))));
        holder.setInventory(inv);

        Map<Material, Price> sellableItems = ItemManager.getAllSellableMap();

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        int nbItems = min(sellableItems.size(), USABLE_SIZE_SELL_MENU);
        int startIndex = page * USABLE_SIZE_SELL_MENU;
        List<Map.Entry<Material, Price>> entries = new ArrayList<>(sellableItems.entrySet());



        if (isShowingAllItems) {
            for (int i = startIndex; i < startIndex + nbItems; i++)
            {
                if (i >= entries.size()) break;
                Map.Entry<Material, Price> entry = entries.get(i);
                if (entry == null) continue;
                Price price = entry.getValue();
                if (price == null) continue;
                if (i-startIndex >= USABLE_SIZE_SELL_MENU) break;
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

                inv.setItem(i-startIndex, displayStack);
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

        inv.setItem(USABLE_SIZE_SELL_MENU, createBackItem(p));
        if (page > 0)
            inv.setItem(USABLE_SIZE_SELL_MENU+1, navItem(p, ArrowDirection.LEFT));
        if (page < getMaxSellPages(isShowingAllItems))
            inv.setItem(USABLE_SIZE_SELL_MENU+8, navItem(p, ArrowDirection.RIGHT));


        inv.setItem(USABLE_SIZE_SELL_MENU+4, createItem(Material.NAME_TAG,
                (isShowingAllItems ?
                        Messages.string(p, "gui.shop.hide_unavailable_items") :
                        Messages.string(p, "gui.shop.show_unavailable_items")),
                "toggle_show_all", ""));

        p.openInventory(inv);
    }

    private static List<Tuple<@NotNull ItemStack, @NotNull Price>> getAllBuyableItemsWithPrice(Player p) {
        List<Tuple<@NotNull ItemStack, @NotNull Price>> buyableItemsWithPrice = new ArrayList<>();
        ItemManager.getAllBuyableItemStacks(p).forEach(
                item -> {
                    Price price = item.getPrice();
                    ItemStack itemStack = item.giveItem(p);
                    if (price != null && itemStack != null) {
                        buyableItemsWithPrice.add(new Tuple<>(itemStack, price));
                    }
                });
        plugin.getGameManager().getShopConfigManager().getConfig().getShopBuyableVanillaItems().forEach(
                item -> {
                    Price price = item.toPrice();
                    ItemStack itemStack = item.toItemStack();
                    if (itemStack != null)
                        buyableItemsWithPrice.add(new Tuple<>(itemStack, price));
                });
        return buyableItemsWithPrice;
    }


    public static void openBuyMenu(Player p, int page) {
        List<Tuple<@NotNull ItemStack, @NotNull Price>> buyableItemsWithPrice = getAllBuyableItemsWithPrice(p);

        PageMenuHolder holder = new PageMenuHolder(MenuHolder.MenuType.NEW_SHOP_BUY_MENU, page);
        Inventory inv = Bukkit.createInventory(holder, SIZE_BUY_MENU, Messages.component(p,"gui.shop.buy_title", Placeholder.unparsed("page", String.valueOf(page+1))));
        holder.setInventory(inv);

        PlayerBalance balance = plugin.getGameManager().getMoneyManager().getBalance(p.getUniqueId());

        int nbItems = min(buyableItemsWithPrice.size(), USABLE_SIZE_BUY_MENU) ;
        int startIndex = page * USABLE_SIZE_BUY_MENU;

        for (int itemIndex = startIndex; itemIndex < startIndex + nbItems; itemIndex++) {
            if (itemIndex >= buyableItemsWithPrice.size()) break;
            Tuple<@NotNull ItemStack, @NotNull Price> display = buyableItemsWithPrice.get(itemIndex);
            ItemStack displayStack = display.getA();
            Price displayPrice = display.getB();

            List<Component> display_lore = displayStack.lore();
            if (display_lore == null) display_lore = new ArrayList<>();
            display_lore.add(displayPrice.displayResource(p, balance.hasEnoughResource(displayPrice)));
            display_lore.add(displayPrice.displayMaterial(p, balance.hasEnoughMaterial(displayPrice)));
            display_lore.add(Messages.component(p, "gui.shop.click_to_exchange"));
            displayStack.lore(display_lore);
            inv.setItem(itemIndex-startIndex, displayStack);
        }

        // boutons
        if (page > 0)
            inv.setItem(USABLE_SIZE_BUY_MENU+1, navItem(p,ArrowDirection.LEFT));

        inv.setItem(USABLE_SIZE_BUY_MENU, createBackItem(p));

        if (page < getMaxBuyPages())
            inv.setItem(USABLE_SIZE_BUY_MENU+8, navItem(p,ArrowDirection.RIGHT));

        p.openInventory(inv);
    }

    private static int getMaxBuyPages(){
        return ItemManager.getAllBuyableItemStacks(Bukkit.getServer()).size()/ USABLE_SIZE_BUY_MENU;
    }
    private static int getMaxSellPages(boolean isShowingAllItems){
        if (!isShowingAllItems) return 0;
        return ItemManager.getAllSellableMap().size()/ USABLE_SIZE_SELL_MENU;
    }

    private static int page(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof PageMenuHolder holder) {
            return holder.getPage();
        }
        return 0;
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

        if (isNavItem(event.getCurrentItem(), ArrowDirection.LEFT)){
            openBuyMenu(player, max(page(event)-1,0));
        }
        if (isNavItem(event.getCurrentItem(), ArrowDirection.RIGHT)){
            openBuyMenu(player, min(getMaxBuyPages(),page(event)+1));
        }
        if (event.getSlot() >= USABLE_SIZE_BUY_MENU) return;
        int idx = event.getSlot() + page(event)* USABLE_SIZE_BUY_MENU;
        List<Tuple<@NotNull ItemStack, @NotNull Price>> buyableItemsWithPrice = getAllBuyableItemsWithPrice(player);
        if (idx >= buyableItemsWithPrice.size()) return;

        Tuple<@NotNull ItemStack, @NotNull Price> item = buyableItemsWithPrice.get(idx);

        if (player.getGameMode() != GameMode.CREATIVE){
            if (plugin.getGameManager().getMoneyManager().subtractBalance(player.getUniqueId(), item.getB())) {
                player.getInventory().addItem(item.getA());
                Messages.sendPing(player, "shop.exchange_success");
                openBuyMenu(player, page(event));
            }else{
                Messages.sendError(player, "shop.not_enough_ingredients");
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

        if (isNavItem(event.getCurrentItem(), ArrowDirection.LEFT)){
            openSellMenu(player, holder.isShowingAllItems(), max(page(event)-1,0));
            return;
        }
        if (isNavItem(event.getCurrentItem(), ArrowDirection.RIGHT)){
            openSellMenu(player, holder.isShowingAllItems(), min(getMaxSellPages(holder.isShowingAllItems()),page(event)+1));
            return;
        }

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
            openSellMenu(player, !(holder).isShowingAllItems(), page(event));
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
            openSellMenu(player, false, 0);
        }
    }
}