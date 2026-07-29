package net.bzkgns.theFloorIsLavaManager.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.currency.PlayerBalance;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.items.abilities.gambling.GamblingInstance;
import net.bzkgns.theFloorIsLavaManager.items.items.GamblingItem;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class GamblingListener implements Listener {

    @EventHandler
    public void onGamblingInteract(PlayerInteractEvent event) {
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
        if (!new GamblingItem().isItem(item)) return;

        event.setCancelled(true);


        openGambleMenu(event.getPlayer());
    }

    public static void openGambleMenu(Player p) {
        PlayerBalance balance = TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().getBalance(p.getUniqueId());
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Messages.component(p, "dialog.gamble.title"))
                        .inputs(List.of(
                                DialogInput.numberRange("bet_material", Messages.component(p, "dialog.gamble.bet_label_material"), 0, balance.material())
                                        .initial(0f).step(1f)
                                        .build(),
                                DialogInput.numberRange("bet_resource", Messages.component(p, "dialog.gamble.bet_label_resource"), 0, balance.resource())
                                        .initial(0f).step(1f)
                                        .build()
                        ))
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Messages.component(p, "dialog.gamble.bet_button").color(TextColor.color(0xAEFFC1)),
                                Messages.component(p, "dialog.gamble.bet_button_desc"),
                                100,
                                DialogAction.customClick(Key.key("tfl:bet/confirm"), null)
                        ),
                        ActionButton.create(
                                Messages.component(p, "dialog.cancel_button").color(TextColor.color(0xFFA0B1)),
                                Messages.component(p, "dialog.gamble.cancel_bet_desc"),
                                100,
                                null // If we set the action to null, it doesn't do anything and closes the dialog
                        )
                ))
        );
        p.showDialog(dialog);
    }

    @EventHandler
    void handleGambleDialog(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(Key.key("tfl:bet/confirm"))) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        if (!(event.getCommonConnection() instanceof PlayerGameConnection conn)) {
            return;
        }
        Player player = conn.getPlayer();

        Float betResourceFloat = view.getFloat("bet_resource");
        if (betResourceFloat == null) {
            betResourceFloat = 0f;
        }
        Float betMaterialFloat = view.getFloat("bet_material");
        if (betMaterialFloat == null) {
            betMaterialFloat = 0f;
        }
        int betResource = Math.round(betResourceFloat);
        int betMaterial = Math.round(betMaterialFloat);

        Price betPrice = new Price(betMaterial, betResource, 0);

        if (TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().subtractBalance(player.getUniqueId(),betPrice)){
            new GamblingInstance(player, betPrice);
        }
    }
}
