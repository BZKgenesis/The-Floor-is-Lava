package net.bzkgns.theFloorIsLavaManager;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class TheFloorIsLavaBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        // Register a new handler for the compose lifecycle event on the enchantment registry
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event -> event.registry().register(
                        DialogKeys.create(Key.key("tfl:menu_dialog")), builder -> builder
                                .base(DialogBase.builder(Component.text("Quick actions")).build())
                                .type(
                                        DialogType.multiAction(List.of(
                                                ActionButton.builder(Component.text("Open shop"))
                                                        .action(DialogAction.staticAction(ClickEvent.runCommand("/shop")))
                                                        .build(),
                                                ActionButton.builder(Component.text("Open team menu"))
                                                        .action(DialogAction.staticAction(ClickEvent.runCommand("/tfl team")))
                                                        .build()
                                        )).build()
                                )
                )));

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {

                        // Retrieve the URI of the datapack folder.

                        URL url = this.getClass().getResource("/tfl");
                        if (url == null) {
                            throw new RuntimeException("Datapack folder not found");
                        }
                        URI uri = url.toURI();

                        // Discover the pack. The ID is set to "provided", which indicates to

                        // a server owner that your plugin includes this data pack.

                        event.registrar().discoverPack(uri, "provided");

                    } catch (URISyntaxException | IOException e) {

                        throw new RuntimeException(e);

                    }
                }
        ));

    }
}
