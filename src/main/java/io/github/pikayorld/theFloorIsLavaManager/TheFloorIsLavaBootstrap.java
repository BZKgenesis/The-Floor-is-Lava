package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.List;

public class TheFloorIsLavaBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        // Register a new handler for the compose lifecycle event on the enchantment registry
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event ->  event.registry().register(

                        DialogKeys.create(Key.key("tfl:menu_dialog")),    builder -> builder
                                .base(DialogBase.builder(Component.text("Title")).build())
                                .type(
                                        DialogType.multiAction(List.of(
                                                ActionButton.builder(Component.text("Ouvrir le shop"))
                                                        .action(DialogAction.staticAction(ClickEvent.runCommand("/shop")))
                                                        .build(),
                                                ActionButton.builder(Component.text("Ouvrir le gestionnaire d'équipe"))
                                                        .action(DialogAction.staticAction(ClickEvent.runCommand("/tfl team")))
                                                        .build()
                                        )).build()
                                )
        )));

    }
}
