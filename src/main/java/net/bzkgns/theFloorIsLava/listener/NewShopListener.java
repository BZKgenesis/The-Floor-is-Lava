package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.items.gui.ShopGUI;
import net.bzkgns.theFloorIsLava.items.items.ShopItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NewShopListener implements Listener {

    @EventHandler
    public void onShopInteract(PlayerInteractEvent event) {
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
        if (!new ShopItem().isItem(item)) return;

        event.setCancelled(true);

        ShopGUI.openMainMenu(event.getPlayer());
    }
}
