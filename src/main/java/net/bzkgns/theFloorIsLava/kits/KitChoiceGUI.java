package net.bzkgns.theFloorIsLava.kits;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
                    kit.getDisplayName(),
                    Messages.component(player, "gui.kit.click_to_choose",Placeholder.component("kit_name", kit.getDisplayName())),
                    100,
                    DialogAction.customClick(Key.key(key), null )
            ));
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Messages.component(player, "gui.kit.menu_title")).build())
                .type(DialogType.multiAction(actions).build()
                )
        );
        player.showDialog(dialog);

    }

    @EventHandler
    void handleRecolorDialog(PlayerCustomClickEvent event) {
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
                Messages.send(player, "kit.invalid", Placeholder.parsed("kit_name", kitId));
            }
            return;
        }

        if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            KitManager.getInstance().assignKitToPlayer(player.getUniqueId(),kit.getName());
            KitManager.getInstance().applyKitToPlayer(player);
            Messages.send(player, "kit.self_assigned", Placeholder.component("kit_name", kit.getDisplayName()));
        }
    }
}
