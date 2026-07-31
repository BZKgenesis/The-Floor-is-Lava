package net.bzkgns.theFloorIsLava.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.gambling.GamblingConfig;
import net.bzkgns.theFloorIsLava.currency.PlayerBalance;
import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.abilities.gambling.GamblingEngine;
import net.bzkgns.theFloorIsLava.items.abilities.gambling.GamblingInstance;
import net.bzkgns.theFloorIsLava.items.abilities.gambling.GamblingSymbol;
import net.bzkgns.theFloorIsLava.items.items.GamblingItem;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

    @EventHandler
    public void onGamblingInfoInteract(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(Key.key("tfl"))) {
            return;
        }

        if (!(event.getCommonConnection() instanceof PlayerGameConnection conn)) {
            return;
        }
        Player player = conn.getPlayer();

        openGambleInfoMenu(player);
    }

    public static void openGambleMenu(Player p) {
        Component clickInfoText = Messages.component(p, "dialog.gamble.info_text")
                .append(Messages.component(p, "dialog.gamble.info_button")
                .clickEvent(
                        ClickEvent.clickEvent(
                                ClickEvent.Action.CUSTOM,
                                ClickEvent.Payload.custom(
                                        Key.key("tfl"),
                                        BinaryTagHolder.binaryTagHolder("a")
                                )
                        )
                ));
        PlayerBalance balance = TheFloorIsLava.getInstance().getGameManager().getMoneyManager().getBalance(p.getUniqueId());
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Messages.component(p, "dialog.gamble.title"))
                        .body(List.of(DialogBody.plainMessage(clickInfoText)))
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


    public static void openGambleInfoMenu(Player p) {
        GamblingConfig config = (GamblingConfig) ConfigRegistry.getConfigManager("gambling").getConfig();
        Component probabilityText = Messages.component(p, "dialog.gamble.info.probabilities.title").appendNewline()
                .append(Messages.component(p, "dialog.gamble.info.probabilities.header")).appendNewline();
        for (GamblingSymbol symbol : GamblingSymbol.values()) {
            String probaStr = String.format("%.0f", symbol.getProbability(config)*100);
            probaStr = " ".repeat(15-probaStr.length()) + probaStr;
            probabilityText = probabilityText.append(symbol.getSymbol()).append(Component.text(probaStr + "%").appendNewline());
        }
        Component jackpotText = Messages.component(p, "dialog.gamble.info.jackpot.title").appendNewline()
                .append(Messages.component(p, "dialog.gamble.info.jackpot.header")).appendNewline();
        for (GamblingSymbol symbol : GamblingSymbol.values()) {
            String jackpotStr = String.format("x%.2f", symbol.getJackpotGain(config));
            jackpotStr = "x3" + " ".repeat(15-jackpotStr.length()) + jackpotStr;
            jackpotText = jackpotText.append(symbol.getSymbol()).append(Component.text(jackpotStr).appendNewline());
        }
        Component twoKindText = Messages.component(p, "dialog.gamble.info.two_kind.title").appendNewline()
                .append(Messages.component(p, "dialog.gamble.info.two_kind.header")).appendNewline();
        for (GamblingSymbol symbol : GamblingSymbol.values()) {
            String twoKindStr = String.format("x%.2f", symbol.getTwoGain(config));
            twoKindStr = "x2" + " ".repeat(15-twoKindStr.length()) + twoKindStr;
            twoKindText = twoKindText.append(symbol.getSymbol()).append(Component.text(twoKindStr).appendNewline());
        }
        Component oneKindText = Messages.component(p, "dialog.gamble.info.one_kind.title").appendNewline()
                .append(Messages.component(p, "dialog.gamble.info.one_kind.header")).appendNewline();
        for (GamblingSymbol symbol : GamblingSymbol.values()) {
            String oneKindStr = String.format("x%.2f", symbol.getOneGain(config));
            oneKindStr = " ".repeat(15-oneKindStr.length()) + oneKindStr;
            oneKindText = oneKindText.append(symbol.getSymbol()).append(Component.text(oneKindStr).appendNewline());
        }
        Component finalProbabilityText = probabilityText;
        Component finalJackpotText = jackpotText;
        Component finalTwoKindText = twoKindText;
        Component finalOneKindText = oneKindText;
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Messages.component(p, "dialog.gamble.info.title"))
                        .body(List.of(
                                DialogBody.plainMessage(finalProbabilityText),
                                DialogBody.plainMessage(finalJackpotText),
                                DialogBody.plainMessage(finalTwoKindText),
                                DialogBody.plainMessage(finalOneKindText),
                                DialogBody.plainMessage(
                                        Messages.component(p, "dialog.gamble.info.rtp").appendNewline()
                                                .append(Component.text(Math.round(GamblingEngine.computeRTP()*100) + "%")))
                            )
                        )
                        .build()
                )
                .type(DialogType.notice())
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

        if (TheFloorIsLava.getInstance().getGameManager().getMoneyManager().subtractBalance(player.getUniqueId(),betPrice)){
            new GamblingInstance(player, betPrice);
        }
    }
}
