package io.github.pikayorld.theFloorIsLavaManager;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static io.github.pikayorld.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    private DangerManager dangerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new TheFloorIslavaListener(this), this);

        dangerManager = new DangerManager(this);

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(dangerManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        World world = Bukkit.getWorld("world");
        StructureManager manager = Bukkit.getStructureManager();

        // charge la structure depuis le fichier

        Structure structure = null; // a() = load
        try {
            InputStream struct_file = this.getResource("tfl_spawn.nbt");
            if (struct_file != null){
                structure = manager.loadStructure(struct_file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (world != null){
            if (structure != null) {
                Location pos = new Location(world,-10, 280, -10); // position où placer la structure
                structure.place(pos, false, StructureRotation.NONE, Mirror.NONE,0,1.0f,new Random());
            }

            world.setSpawnLocation(new Location(world, 0,281,0));
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setTime(0);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EggBridgeTask(this), 1,1);

        new TheFloorIsLavaCrafts().setCrafts(this);

        this.getLogger().info("RisingDamage activé !");
    }

    @Override
    public void onDisable() {
        if (dangerManager != null) {
            dangerManager.stop();
        }
    }

}
