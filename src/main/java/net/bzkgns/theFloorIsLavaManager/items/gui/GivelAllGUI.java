package net.bzkgns.theFloorIsLavaManager.items.gui;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.bzkgns.theFloorIsLavaManager.items.items.*;
import net.bzkgns.theFloorIsLavaManager.items.items.PopupTowerItem;
import net.bzkgns.theFloorIsLavaManager.items.items.TeamInventoryItem;
import net.bzkgns.theFloorIsLavaManager.items.items.TeamRespawnItem;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.utils.GuiUtils;
import net.bzkgns.theFloorIsLavaManager.utils.menu.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import static net.bzkgns.theFloorIsLavaManager.utils.SoundUtils.*;


public class GivelAllGUI implements Listener {

    private static final int SIZE = 54;

    private static final CustomItem[] AVAILABLE_ITEMS = new CustomItem[]{
            new ShopItem(),
            new BatteItem(),
            new CiseauxItem(),
            new EggBridgeItem(),
            new SnowballPlateItem(),
            new PopupTowerItem(),
            new TeamRespawnItem(),
            new TeamInventoryItem(),
            new InfiniteWoolItem(),
            new FeatherFallingBootsItem(),
            new FireBallCustomItem(),
            new TntItem(),
            new ParachuteItem(),
            new HealCampItem(),
            new ThrowableIronGolemItem(),
            new GamblingItem()
    };

    public static void open(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.GIVE_ALL);
        Inventory inv = Bukkit.createInventory(holder, SIZE, Messages.component(p,"gui.give_all_menu_title"));
        holder.setInventory(inv);

        for (int i = 0; i < AVAILABLE_ITEMS.length; i++) {
            //noinspection ConstantValue
            if (i >= SIZE)
                break;
            inv.setItem(i, AVAILABLE_ITEMS[i].giveItem(p));
        }
        p.openInventory(inv);
    }


    @EventHandler
    public void onGiveAllInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // Ignore la main secondaire
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new GiveAllItem().isItem(item)) return;

        event.setCancelled(true);

        GivelAllGUI.open(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!GuiUtils.isValidInteractMenu(e, MenuHolder.MenuType.GIVE_ALL)) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (p.getInventory().firstEmpty() == -1) {
            playError(p);
            return;
        }
        p.getInventory().addItem(clickedItem.clone());
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }
}