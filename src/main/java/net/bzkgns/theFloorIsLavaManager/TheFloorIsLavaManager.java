package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.bzkgns.theFloorIsLavaManager.DangerZone.DangerManager;
import net.bzkgns.theFloorIsLavaManager.Items.EggBridgeTask;
import net.bzkgns.theFloorIsLavaManager.Teams.TeamGUI;
import net.bzkgns.theFloorIsLavaManager.Teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.Shop.ShopGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import static net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    public static final String LOBBY_WORLD = "tfl_lobby";
    public static final String GAME_WORLD = "tfl_game";
    public static final String MAPS_FOLDER = "TheFloorIsLava-maps";

    public static String[] RECIPES_KEY = {"batte", "eggBridge", "patate", "blocs_en_plus", "fireball", "ciseaux", "enderPearl", "popupTower", "teamInv", "snowballPlate"};

    private DangerManager dangerManager;
    private TeamManager teamManager;
    private ResourcePackManager resourcePackManager;
    private WorldManager worldManager;


    public static boolean pvp;


    @Override
    public void onEnable() {
        // saveDefaultConfig() et la création de dangerManager doivent précéder toute
        // opération de WorldManager : resetRandomWorld()/loadMap() appellent
        // dangerManager.stop() en interne, donc dangerManager ne doit jamais être null
        // au moment où onEnable() les invoque (cas du tout premier démarrage, quand
        // GAME_WORLD n'existe pas encore).
        saveDefaultConfig();
        initLobbyWorld();

        dangerManager = new DangerManager(this);

        if (Bukkit.getWorld(GAME_WORLD) == null) {
            getLogger().info("Creation du monde de jeu...");
            worldManager = new WorldManager(this);
            worldManager.resetRandomWorld();
        } else {
            worldManager = new WorldManager(this);
        }
        resourcePackManager = new ResourcePackManager(this);
        try {
            resourcePackManager.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Bukkit.getPluginManager().registerEvents(new ShopGUI(), this);
        pvp = true;

        getServer().getPluginManager().registerEvents(new TheFloorIslavaListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(this), this);
        getServer().getPluginManager().registerEvents(new ConfigGUI(this), this);

        teamManager = new TeamManager(this);

        for(Team team : getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            team.unregister();
        }

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(dangerManager, this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));

        LiteralCommandNode<CommandSourceStack> ShopBuildCommand = Commands.literal("shop")
                .executes(ctx ->{
                    if (ctx.getSource().getExecutor() instanceof Player p){
                        ShopGUI.open(p,0);
                    }
                    return Command.SINGLE_SUCCESS;
                } ).build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ShopBuildCommand));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EggBridgeTask(this), 1,1);

        new TheFloorIsLavaCrafts().setCrafts(this);

        Objective healthObjective = getServer().getScoreboardManager().getMainScoreboard().getObjective("tfl.health");
        if (healthObjective ==null){
            healthObjective = getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("tfl.health", Criteria.HEALTH, Component.text(""), RenderType.HEARTS);
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        ShopGUI.loadRecipes();

        this.getLogger().info("RisingDamage active !");
    }

    @Override
    public void onDisable() {
        if (dangerManager != null) {
            dangerManager.stop();
        }
    }

    private void initLobbyWorld() {
        World lobby = Bukkit.getWorld(LOBBY_WORLD);

        if (lobby == null) {
            WorldCreator creator = new WorldCreator(LOBBY_WORLD);
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.NORMAL);
            creator.generateStructures(false);

            lobby = Bukkit.createWorld(creator);
        }

        if (lobby == null) {
            getLogger().severe("Impossible de charger le monde \"" + LOBBY_WORLD + "\" !");
            return;
        }

        // Configuration du lobby
        lobby.setAutoSave(false);
        lobby.setTime(6000);
        lobby.setGameRule(GameRules.ADVANCE_TIME, false);
        lobby.setGameRule(GameRules.ADVANCE_TIME, false);
        lobby.setGameRule(GameRules.ADVANCE_WEATHER, false);
        lobby.setStorm(false);
        lobby.setThundering(false);

        Location spawn = new Location(lobby, 0.5, 100, 0.5);
        lobby.setSpawnLocation(spawn);

        getLogger().info("Monde lobby charge !");
    }

    public double getFallDamageReduction(){
        return dangerManager.getConfig().getFallDamageReduction();
    }

    public DangerManager getDangerManagerInstance(){
        return dangerManager;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }

    public ResourcePackManager getResourcePackManager(){
        return resourcePackManager;
    }

    public WorldManager getWorldManager(){
        return worldManager;
    }
}

/*
/summon armor_stand ~ ~-1.5 ~
    {
        NoGravity:1b,
        Silent:1b,
        Invulnerable:1b,
        Invisible:1b,
        Tags:["tfl_spawn_mannequin"],
        attributes:[
            {
                id:"minecraft:scale",
                base:.5
            }
        ],
        Passengers:[
            {
                id:"minecraft:mannequin",
                NoGravity:1b,
                Silent:1b,
                Invulnerable:1b,
                immovable:true,
                hide_description:false,
                Rotation:[180F,0F],
                Tags:["tfl_spawn_mannequin"],
                attributes:[
                    {
                        id:"minecraft:scale",
                        base:.5
                    }
                ],
                profile:{
                    "name":"BZK_genesis",
                    "id":[I;674201676,-138196797,-1254556747,-1700660538],
                    "properties":[
                        {
                            "name":"textures",
                            "value":"ewogICJ0aW1lc3RhbXAiIDogMTc4Mzk4MDA5MjMzOCwKICAicHJvZmlsZUlkIiA6ICIyODJmODA0Y2Y3YzM0OGMzYjUzOGZiYjU5YWExZmFjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCWktfZ2VuZXNpcyIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81OTQ1MDI2MmNiODMzYmRhNWViY2VhN2U2N2ExOWJkNGQ0NmJiYzVmOTRhNDMyMDEzNmUwYmM4OTU5MWI0YzlkIgogICAgfQogIH0KfQ=="
                        }
                    ]
                },
                description:"Créateur"
            }
        ],
        Rotation:[180F,0F]
    }
 */