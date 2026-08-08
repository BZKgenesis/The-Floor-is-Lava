package net.bzkgns.theFloorIsLava;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.bzkgns.theFloorIsLava.config.ConfigGUI;
import net.bzkgns.theFloorIsLava.config.ConfigManager;
import net.bzkgns.theFloorIsLava.config.game.GameConfig;
import net.bzkgns.theFloorIsLava.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLava.exception.WorldGenerationException;
import net.bzkgns.theFloorIsLava.game.PvpManager;
import net.bzkgns.theFloorIsLava.items.ItemManager;
import net.bzkgns.theFloorIsLava.items.abilities.HealCampManager;
import net.bzkgns.theFloorIsLava.items.gui.GivelAllGUI;
import net.bzkgns.theFloorIsLava.items.gui.ShopGUI;
import net.bzkgns.theFloorIsLava.items.items.*;
import net.bzkgns.theFloorIsLava.game.kits.KitChoiceGUI;
import net.bzkgns.theFloorIsLava.game.kits.KitCommands;
import net.bzkgns.theFloorIsLava.game.kits.KitManager;
import net.bzkgns.theFloorIsLava.config.lang.LangManager;
import net.bzkgns.theFloorIsLava.listener.*;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.game.GameManager;
import net.bzkgns.theFloorIsLava.resources.ResourcePackManager;
import net.bzkgns.theFloorIsLava.game.sidebar.SidebarManager;
import net.bzkgns.theFloorIsLava.statistics.DatabaseManager;
import net.bzkgns.theFloorIsLava.statistics.StatisticsManager;
import net.bzkgns.theFloorIsLava.statistics.visual.RankingListener;
import net.bzkgns.theFloorIsLava.items.tasks.ThrowableIronGolemTask;
import net.bzkgns.theFloorIsLava.teams.TeamGUI;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.bzkgns.theFloorIsLava.listener.VeinMinerListener;
import net.bzkgns.theFloorIsLava.world.WorldManager;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.util.List;

import static net.bzkgns.theFloorIsLava.TheFloorIsLavaCommands.registerTflCommands;

public final class TheFloorIsLava extends JavaPlugin {


    private GameManager gameManager;
    private ResourcePackManager resourcePackManager;
    private WorldManager worldManager;
    private StatisticsManager statisticsManager;
    private DatabaseManager databaseManager;
    private SidebarManager sidebarManager;
    private ScoreboardLibrary scoreboardLibrary;

    public static TheFloorIsLava getInstance() {
        return JavaPlugin.getPlugin(TheFloorIsLava.class);
    }


    @Override
    public void onEnable() {
        boolean _ = WorldManager.MAPS_FOLDER.mkdir();
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            // If server version is not yet supported, you can fallback to the no-op implementation:
            scoreboardLibrary = new NoopScoreboardLibrary();
            this.getLogger().warning("Server version unsupported, scoreboard functionality will not be visible!");
        }
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
                new ShearsItem(),
                new EggBridgeItem(),
                new PopupTowerItem(),
                new SnowballPlateItem(),
                new TeamInventoryItem(),
                new TeamRespawnAnchorItem(),
                new TeamManagerItem(),
                new GiveAllItem(),
                new InfiniteWoolItem(),
                new FeatherFallingBootsItem(),
                new FireBallItem(),
                new TntItem(),
                new ParachuteItem(),
                new HealCampItem(),
                new ShopItem(),
                new WoolItem(),
                new FoodItem(),
                new GamblingItem()
        );
        if (Bukkit.getWorld(WorldManager.GAME_WORLD) == null) {
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

        PvpManager.setPvpEnabled(true);

        Bukkit.getPluginManager().registerEvents(new TheFloorIslavaListener(), this);

        Bukkit.getPluginManager().registerEvents(new PopupTowerListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new GivelAllGUI(), this);
        Bukkit.getPluginManager().registerEvents(new ShopGUI(), this);
        Bukkit.getPluginManager().registerEvents(new InfiniteWoolListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamGUI(this), this);
        Bukkit.getPluginManager().registerEvents(new ConfigGUI(this), this);
        Bukkit.getPluginManager().registerEvents(new KitChoiceGUI(), this);
        Bukkit.getPluginManager().registerEvents(new FireBallCustomListener(), this);
        Bukkit.getPluginManager().registerEvents(new TntListener(), this);
        Bukkit.getPluginManager().registerEvents(new ParachuteListener(), this);
        Bukkit.getPluginManager().registerEvents(new SnowballListener(), this);
        Bukkit.getPluginManager().registerEvents(new EggBridgeListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamManagerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealCampListener(), this);
        Bukkit.getPluginManager().registerEvents(new ThrowableIronGolemListener(), this);
        Bukkit.getPluginManager().registerEvents(new NewShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new GamblingListener(), this);

        Bukkit.getPluginManager().registerEvents(new AutoSmelt(), this);
        Bukkit.getPluginManager().registerEvents(new VeinMinerListener(), this);
        Bukkit.getPluginManager().registerEvents(new RankingListener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ThrowableIronGolemTask(), 0L, 1L);

        HealCampManager.getInstance().registerHealCampTask();

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
                        ShopGUI.openMainMenu(p);
                    }
                    return Command.SINGLE_SUCCESS;
                } ).build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ShopBuildCommand));

        Objective healthObjective = getServer().getScoreboardManager().getMainScoreboard().getObjective("tfl.health");
        if (healthObjective ==null){
            healthObjective = getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("tfl.health", Criteria.HEALTH, Component.text(""), RenderType.HEARTS);
        }
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

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
        scoreboardLibrary.close();
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

    public ScoreboardLibrary getScoreboardLibrary() {
        return scoreboardLibrary;
    }
}