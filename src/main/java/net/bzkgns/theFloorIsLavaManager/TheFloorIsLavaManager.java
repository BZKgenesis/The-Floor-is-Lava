package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.bzkgns.theFloorIsLavaManager.config.ConfigGUI;
import net.bzkgns.theFloorIsLavaManager.config.ConfigManager;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfig;
import net.bzkgns.theFloorIsLavaManager.items.team_inventory.TeamInventoryListener;
import net.bzkgns.theFloorIsLavaManager.managers.GameManager;
import net.bzkgns.theFloorIsLavaManager.config.danger.DangerConfig;
import net.bzkgns.theFloorIsLavaManager.items.*;
import net.bzkgns.theFloorIsLavaManager.items.popup_tower.PopupTowerItem;
import net.bzkgns.theFloorIsLavaManager.items.popup_tower.PopupTowerListener;
import net.bzkgns.theFloorIsLavaManager.items.team_inventory.TeamInventoryItem;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnItem;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnListener;
import net.bzkgns.theFloorIsLavaManager.managers.ResourcePackManager;
import net.bzkgns.theFloorIsLavaManager.managers.WorldManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamGUI;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.shop.ShopGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

import static net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLavaManager extends JavaPlugin {

    public static final String LOBBY_WORLD = "tfl_lobby";
    public static final String GAME_WORLD = "tfl_game";
    public static final String MAPS_FOLDER = "TheFloorIsLava-maps";

    public static final String[] RECIPES_KEY = {"batte", "eggBridge", "patate", "blocs_en_plus", "fireball", "ciseaux", "enderPearl", "popupTower", "teamInv", "snowballPlate"};

    private GameManager gameManager;
    private ResourcePackManager resourcePackManager;
    private WorldManager worldManager;

    private final Map<String, ConfigManager<?>> configManagers = new HashMap<>();

    public ConfigManager<?> getConfigManager(String configName) {
        return configManagers.get(configName);
    }

    public static boolean pvp;

    public static TheFloorIsLavaManager getInstance() {
        return JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
    }


    @Override
    public void onEnable() {
        // saveDefaultConfig() et la création de dangerManager doivent précéder toute
        // opération de WorldManager : resetRandomWorld()/loadMap() appellent
        // dangerManager.stop() en interne, donc dangerManager ne doit jamais être null
        // au moment où onEnable() les invoque (cas du tout premier démarrage, quand
        // GAME_WORLD n'existe pas encore).
        saveDefaultConfig();
        gameManager = new GameManager();

        worldManager = new WorldManager(this);
        worldManager.initLobbyWorld();
        ItemManager.registerAll(
                new BatteItem(),
                new CiseauxItem(),
                new EggBridgeItem(),
                new FireBallItem(),
                new PopupTowerItem(),
                new ShopItem(),
                new SnowballPlateItem(),
                new TeamInventoryItem(),
                new TeamRespawnItem(),
                new TeamManagerItem(),
                new GiveAllItem(),
                new InfiniteWoolItem(),
                new FeatherFallingBoots()
        );

        ConfigManager<DangerConfig> dangerConfigManager = new ConfigManager<>(new DangerConfig());
        ConfigManager<GameConfig> gameConfigManager = new ConfigManager<>(new GameConfig());
        configManagers.put(dangerConfigManager.getConfig().getName(), dangerConfigManager);
        configManagers.put(gameConfigManager.getConfig().getName(), gameConfigManager);
        if (Bukkit.getWorld(GAME_WORLD) == null) {
            getLogger().info("Creation du monde de jeu...");
            worldManager.resetRandomWorld();
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

        TeamManager.getInstance().clearTeams();

        for(Team team : getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            team.unregister();
        }

        LiteralCommandNode<CommandSourceStack> buildCommand = registerTflCommands(gameManager, this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));

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

        this.getLogger().info("RisingDamage active !");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopGame();
        }
    }


    public double getFallDamageReduction(){
        return getConfigManager("game").getDouble("fall-damage-reduction");
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