package net.bzkgns.theFloorIsLavaManager.kits;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class KitChoiceGUI implements Listener {

    public static void openKitChoiceGUI(org.bukkit.entity.Player player){
        List<ActionButton> actions = new ArrayList<>();

        for (KitData kit : KitManager.getInstance().getAllKits().values()) {
            String key = "tfl:user_input/choose_kit/"+ kit.getName();
            actions.add(ActionButton.create(
                    Component.text(kit.getDisplayName()),
                    Component.text("Cliquer pour choisir le kit " + kit.getDisplayName()),
                    100,
                    DialogAction.customClick(Key.key(key), null )
            ));
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Choisir un kit")).build())
                .type(DialogType.multiAction(actions).build()
                )
        );
        player.showDialog(dialog);

    }

    @EventHandler
    void handleRecolorDialog(PlayerCustomClickEvent event) {
        System.out.println("PlayerCustomClickEvent: " + event.getIdentifier().asString());
        if (!event.getIdentifier().asString().startsWith("tfl:user_input/choose_kit/")) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        String kitId = event.getIdentifier().asString().substring("tfl:user_input/choose_kit/".length());
        KitData kit = KitManager.getInstance().getKit(kitId);
        if (kit == null) {
            if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
                Player player = conn.getPlayer();
                player.sendMessage(TextUtils.errorMessage("Kit invalide."));
            }
            return;
        }

        if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            KitManager.getInstance().assignKitToPlayer(player.getUniqueId(),kit.getName());
            KitManager.getInstance().applyKitToPlayer(player);
            player.sendMessage(TextUtils.validationMessage("Vous avez choisi le kit " + kit.getDisplayName() + "."));
        }
    }
}
