package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.bzkgns.theFloorIsLavaManager.config.ConfigGUI;
import net.bzkgns.theFloorIsLavaManager.config.ConfigManager;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfig;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLavaManager.exception.WorldGenerationException;
import net.bzkgns.theFloorIsLavaManager.items.abilities.InfiniteWool;
import net.bzkgns.theFloorIsLavaManager.items.gui.GivelAllGUI;
import net.bzkgns.theFloorIsLavaManager.items.items.*;
import net.bzkgns.theFloorIsLavaManager.listener.*;
import net.bzkgns.theFloorIsLavaManager.kits.KitChoiceGUI;
import net.bzkgns.theFloorIsLavaManager.kits.KitCommands;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.lang.LangManager;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
import net.bzkgns.theFloorIsLavaManager.managers.GameManager;
import net.bzkgns.theFloorIsLavaManager.items.*;
import net.bzkgns.theFloorIsLavaManager.items.items.PopupTowerItem;
import net.bzkgns.theFloorIsLavaManager.items.items.TeamInventoryItem;
import net.bzkgns.theFloorIsLavaManager.items.items.TeamRespawnItem;
import net.bzkgns.theFloorIsLavaManager.managers.ResourcePackManager;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarManager;
import net.bzkgns.theFloorIsLavaManager.world.WorldManager;
import net.bzkgns.theFloorIsLavaManager.statistics.DatabaseManager;
import net.bzkgns.theFloorIsLavaManager.statistics.StatisticsManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamGUI;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.shop.ShopGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.util.List;

import static net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    public static final String LOBBY_WORLD = "tfl_lobby";
    public static final String GAME_WORLD = "tfl_game";
    public static final String MAPS_FOLDER = "TheFloorIsLava-maps";

    public static final String[] RECIPES_KEY = {"batte", "eggBridge", "patate", "blocs_en_plus", "ciseaux", "enderPearl", "popupTower", "teamInv", "snowballPlate"};

    private GameManager gameManager;
    private ResourcePackManager resourcePackManager;
    private WorldManager worldManager;
    private StatisticsManager statisticsManager;
    private DatabaseManager databaseManager;
    private SidebarManager sidebarManager;

    public static boolean pvp;

    public static TheFloorIsLavaManager getInstance() {
        return JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
    }


    @Override
    public void onEnable() {
        sidebarManager = new SidebarManager();

        databaseManager = new DatabaseManager();
        try {
            databaseManager.connect();
            databaseManager.initializeDatabase();
        } catch (Exception e) {
            getLogger().severe("Erreur lors de la connexion à la base de données : " + e.getMessage());
            getLogger().severe("Le plugin ne peut pas continuer. Veuillez vérifier les fichiers de configuration et réessayer.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        statisticsManager = new StatisticsManager(databaseManager);
        Bukkit.getPluginManager().registerEvents(statisticsManager, this);

        LangManager.getInstance().load();

        gameManager = new GameManager();

        worldManager = new WorldManager();
        worldManager.initLobbyWorld();
        KitManager.getInstance().loadKits();
        ItemManager.registerAll(
                new BatteItem(),
                new CiseauxItem(),
                new EggBridgeItem(),
                new PopupTowerItem(),
                new ShopItem(),
                new SnowballPlateItem(),
                new TeamInventoryItem(),
                new TeamRespawnItem(),
                new TeamManagerItem(),
                new GiveAllItem(),
                new InfiniteWoolItem(),
                new FeatherFallingBootsItem(),
                new FireBallCustomItem(),
                new TntItem(),
                new ParachuteItem()
        );
        if (Bukkit.getWorld(GAME_WORLD) == null) {
            getLogger().info("Creation du monde de jeu...");
            try {
                worldManager.resetRandomWorld();
            } catch (WorldGenerationException e){
                getLogger().severe("Erreur lors de la génération du monde de jeu : " + e.getMessage());
                getLogger().severe("Le plugin ne peut pas continuer. Veuillez vérifier les fichiers de configuration et réessayer.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        resourcePackManager = new ResourcePackManager(this);
        resourcePackManager.load();

        Bukkit.getPluginManager().registerEvents(new ShopGUI(), this);
        Bukkit.getPluginManager().registerEvents(new PopupTowerListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new GivelAllGUI(), this);
        Bukkit.getPluginManager().registerEvents(new InfiniteWool(), this);
        pvp = true;

        getServer().getPluginManager().registerEvents(new TheFloorIslavaListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(this), this);
        getServer().getPluginManager().registerEvents(new ConfigGUI(this), this);
        getServer().getPluginManager().registerEvents(new KitChoiceGUI(), this);
        getServer().getPluginManager().registerEvents(new FireBallCustomListener(), this);
        getServer().getPluginManager().registerEvents(new TntListener(), this);
        getServer().getPluginManager().registerEvents(new ParachuteListener(), this);

        TeamManager.getInstance().clearTeams();

        for(Team team : getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            team.unregister();
        }

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(gameManager, this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(KitCommands.registerCommands()));

        LiteralCommandNode<CommandSourceStack> ShopBuildCommand = Commands.literal("shop")
                .executes(ctx ->{
                    if (ctx.getSource().getExecutor() instanceof Player p){
                        ShopGUI.open(p,0);
                    }
                    return Command.SINGLE_SUCCESS;
                } ).build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ShopBuildCommand));



        new TheFloorIsLavaCrafts().setCrafts(this);

        Objective healthObjective = getServer().getScoreboardManager().getMainScoreboard().getObjective("tfl.health");
        if (healthObjective ==null){
            healthObjective = getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("tfl.health", Criteria.HEALTH, Component.text(""), RenderType.HEARTS);
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        ShopGUI.loadRecipes();

        NamespacedKey key = new NamespacedKey(this, "game_bar");

        if (Bukkit.getBossBar(key) != null) {
            Bukkit.removeBossBar(key);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                this,
                () -> {
                    World gameWorld = worldManager.getGameWorld();
                    if (gameWorld == null) return;
                    List<Entity> entities = gameWorld.getEntities();
                    entities.addAll(worldManager.getLobbyWorld().getEntities());
                    entities.stream()
                            .filter(e -> e instanceof ArmorStand && e.getScoreboardTags().contains("tfl_respawn_team_effect_armorstand"))
                            .map(e -> (ArmorStand) e)
                            .forEach(a -> {
                                a.setRotation(a.getYaw()+0.15f, 0);
                                a.getWorld().spawnParticle(
                                        Particle.OMINOUS_SPAWNING,
                                        a.getLocation().add(Math.cos(a.getYaw())*.8,-.5,Math.sin(a.getYaw())*.8),
                                        1, 0,
                                        0,
                                        0,
                                        0.1
                                );
                            });
                },
                0L,
                2L
        );


        this.getLogger().info("RisingDamage active !");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopGame();
        }
        KitManager.getInstance().clearAllPlayerKits();
        statisticsManager.saveAll();
        try {
            databaseManager.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SidebarManager getSidebarManager(){
        return sidebarManager;
    }



    public double getFallDamageReduction(){
        ConfigManager<GameConfig> gameConfig =
                ConfigRegistry.getConfigManager("game");
        return gameConfig.getDouble(GameConfigKeys.FALL_DAMAGE_REDUCTION);
    }

    public GameManager getGameManager(){
        return gameManager;
    }

    public ResourcePackManager getResourcePackManager(){
        return resourcePackManager;
    }

    public WorldManager getWorldManager(){
        return worldManager;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public static boolean getDebugMode() {
        return true;
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