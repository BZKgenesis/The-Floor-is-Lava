package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.items.popup_tower.PopupTowerItem;
import net.bzkgns.theFloorIsLavaManager.items.team_inventory.TeamInventoryItem;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnItem;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.utils.MenuHolder;
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


public class GivelAllGUI implements Listener {

    private static final int SIZE = 54;

    private static final ItemStack[] AVAILABLE_ITEMS = new ItemStack[]{
            new ShopItem().giveItem(),
            new BatteItem().giveItem(),
            new CiseauxItem().giveItem(),
            new EggBridgeItem().giveItem(),
            new FireBallItem().giveItem(),
            new SnowballPlateItem().giveItem(),
            new PopupTowerItem().giveItem(),
            new TeamRespawnItem().giveItem(),
            new TeamInventoryItem().giveItem(),
            new InfiniteWoolItem().giveItem(),
            new FeatherFallingBoots().giveItem()
    };

    public static void open(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.GIVE_ALL);
        Inventory inv = Bukkit.createInventory(holder, SIZE, Messages.component(p,"gui.give_all_menu_title"));
        holder.setInventory(inv);

        for (int i = 0; i < AVAILABLE_ITEMS.length; i++) {
            //noinspection ConstantValue
            if (i >= SIZE)
                break;
            inv.setItem(i, AVAILABLE_ITEMS[i]);
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
        if (e.getClickedInventory() instanceof PlayerInventory) return;
        if (!(e.getInventory().getHolder() instanceof MenuHolder holder)) return;
        if (holder.getType() != MenuHolder.MenuType.GIVE_ALL) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        p.getInventory().addItem(clickedItem.clone());
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }
}