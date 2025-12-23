package io.github.pikayorld.theFloorIsLavaManager;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.pikayorld.theFloorIsLavaManager.Teams.TeamGUI;
import io.github.pikayorld.theFloorIsLavaManager.Teams.TeamManager;
import io.github.pikayorld.theFloorIsLavaManager.shop.ShopCommand;
import io.github.pikayorld.theFloorIsLavaManager.shop.ShopGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Team;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

import static io.github.pikayorld.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    public static String[] RECIPES_KEY = {"batte", "eggBridge", "patate", "blocs_en_plus", "fireball", "ciseaux", "enderPearl", "popupTower", "teamInv", "snowballPlate"};

    private DangerManager dangerManager;
    private TeamManager teamManager;

    public static boolean pvp;


    public static String worldToReset = "";

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(new ShopGUI(), this);
        pvp = true;
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new TheFloorIslavaListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(this), this);

        dangerManager = new DangerManager(this);
        teamManager = new TeamManager(this);

        for(Team team : getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            team.unregister();
        }

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(dangerManager, this);
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
                Location pos = new Location(world,-10,279,-10); // position où placer la structure
                structure.place(pos, true, StructureRotation.NONE, Mirror.NONE,0,1.0f,new Random());
            }

            Location spawnPos = new Location(world,0,281,0);
            world.setSpawnLocation(spawnPos);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setTime(0);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EggBridgeTask(this), 1,1);

        new TheFloorIsLavaCrafts().setCrafts(this);

        Objective healthObjective = getServer().getScoreboardManager().getMainScoreboard().getObjective("tfl.health");
        if (healthObjective ==null){
            healthObjective = getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("tfl.health", "health", "", RenderType.HEARTS);
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        ShopGUI.loadRecipes();

        this.getLogger().info("RisingDamage activé !");
    }

    @Override
    public void onDisable() {
        if (dangerManager != null) {
            dangerManager.stop();
        }
        if (!Objects.equals(worldToReset, "")){
            World world = Bukkit.getWorld(worldToReset);
            if (world != null){
                world.setTime(0);
                Bukkit.unloadWorld(world, false); // false = ne sauvegarde pas
                File worldFolder = new File(Bukkit.getWorldContainer(), "world");
                boolean error = !deleteRecursively(worldFolder);
                if (error){
                    this.getLogger().warning("Tous les fichiers du monde n'ont pas pu etre reset");
                }
            }
        }
    }
    public static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) deleteRecursively(f);
            }
        }
        return file.delete();
    }

    public static void sendMessage(String message){
        Bukkit.getServer().sendMessage(Component.text("[").color(TextColor.color(255,255,255))
                .append(Component.text("TFL").color(TextColor.color(255,0,0)))
                .append(Component.text("]").color(TextColor.color(255,255,255)))
                .append(Component.text(" " +message).color(TextColor.color(255,255,255))));
    }

    public static void sendActionBar(String message){

        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendActionBar(Component.text("[").color(TextColor.color(255,255,255))
                    .append(Component.text("TFL").color(TextColor.color(255,0,0)))
                    .append(Component.text("]").color(TextColor.color(255,255,255)))
                    .append(Component.text(" " +message).color(TextColor.color(255,255,255))));
        }
    }

    public double getFallDamageReduction(){
        return dangerManager.fallDamageReduction;
    }

    public DangerManager getDangerManagerInstance(){
        return dangerManager;
    }
    public TeamManager getTeamManager(){
        return teamManager;
    }


}
