package io.github.pikayorld.theFloorIsLavaManager;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.pikayorld.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    private DangerManager dangerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        dangerManager = new DangerManager(this);

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(dangerManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->{
            commands.registrar().register(buildCommand);
        });

        this.getLogger().info("RisingDamage activé !");
    }

    @Override
    public void onDisable() {
        if (dangerManager != null) {
            dangerManager.stop();
        }
    }
}
