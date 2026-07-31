package net.bzkgns.theFloorIsLava.managers;

import net.bzkgns.theFloorIsLava.config.gambling.GamblingConfig;
import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.currency.MoneyManager;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.ConfigLoader;
import net.bzkgns.theFloorIsLava.config.ConfigManager;
import net.bzkgns.theFloorIsLava.config.danger.DangerConfig;
import net.bzkgns.theFloorIsLava.config.game.GameConfig;
import net.bzkgns.theFloorIsLava.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLava.config.map.MapConfig;
import net.bzkgns.theFloorIsLava.config.map.MapConfigKeys;
import net.bzkgns.theFloorIsLava.items.items.ShopItem;
import net.bzkgns.theFloorIsLava.kits.KitManager;
import net.bzkgns.theFloorIsLava.lang.LangManager;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.bzkgns.theFloorIsLava.sidebar.provider.GameSidebarProvider;
import net.bzkgns.theFloorIsLava.statistics.StatisticType;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.bzkgns.theFloorIsLava.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.bzkgns.theFloorIsLava.utils.TextUtils.formatTime;
import static net.bzkgns.theFloorIsLava.utils.TextUtils.plainText;

public class GameManager {

    private GameState state = GameState.LOBBY;

    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();


    private final DangerManager dangerManager;

    private final MoneyManager moneyManager;

    private BossBar bossbar;

    private final ConfigManager<GameConfig> gameConfigManager;

    private int phaseRisingTask = -1;
    private final List<Integer> alertTasks = new ArrayList<>();
    private int bossBarTask = -1;

    private boolean noRespawn = false;

    private final List<Player> playerInGame;

    public GameManager() {
        moneyManager = new MoneyManager();

        gameConfigManager = ConfigLoader.load(
                new GameConfig()
        );

        ConfigManager<ItemsConfig> itemsConfigManager = ConfigLoader.load(
                new ItemsConfig()
        );

        ConfigManager<DangerConfig> dangerConfigManager = ConfigLoader.load(
                new DangerConfig()
        );
        ConfigManager<GamblingConfig> gamblingConfigManager = ConfigLoader.load(
                new GamblingConfig()
        );

        ConfigRegistry.addConfig(gameConfigManager);
        ConfigRegistry.addConfig(itemsConfigManager);
        ConfigRegistry.addConfig(dangerConfigManager);
        ConfigRegistry.addConfig(gamblingConfigManager);
        this.dangerManager = new DangerManager(dangerConfigManager);
        playerInGame = new ArrayList<>();
    }
    public boolean canEditConfig() {
        return state == GameState.LOBBY;
    }

    public static void initGamePlayer(Player player) {

        plugin.getSidebarManager().hide(player);
        plugin.getSidebarManager().show(player, new GameSidebarProvider(player));
        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute == null) {
            TheFloorIsLava.getInstance().getLogger().warning("Impossible de récupérer l'attribut MAX_HEALTH pour le joueur " + player.getName());
            return;
        }
        player.setHealth(healthAttribute.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[0]);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.removePotionEffect(PotionEffectType.SATURATION);
        plugin.getGameManager().getMoneyManager().setBalance(player.getUniqueId(), 0, 0, 0);

    }

    public static void initLobbyPlayer(Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute == null) {
            TheFloorIsLava.getInstance().getLogger().warning("Impossible de récupérer l'attribut MAX_HEALTH pour le joueur " + player.getName());
            return;
        }
        player.setHealth(healthAttribute.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        TheFloorIsLava plugin = TheFloorIsLava.getInstance();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(plugin.getWorldManager().getLobbySpawnLocation());
        player.setRespawnLocation(plugin.getWorldManager().getLobbySpawnLocation(), true);
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 1, false, false, false));

        plugin.getGameManager().getMoneyManager().setBalance(player.getUniqueId(), 10000, 10000, 0);
    }


    public boolean startGame() {
        TheFloorIsLava plugin = TheFloorIsLava.getInstance();

        NamespacedKey key = new NamespacedKey(plugin, "game_bar");

        if (Bukkit.getBossBar(key) != null) {
            Bukkit.removeBossBar(key);
        }
        if (gameConfigManager.getInt(GameConfigKeys.MIN_NB_TEAM) > TeamManager.getInstance().getTeams().size()) {
            Messages.broadcastOpError("error.cannot_start_game_not_enough_teams", Placeholder.unparsed("min_teams", String.valueOf(gameConfigManager.getInt(GameConfigKeys.MIN_NB_TEAM))));
            return false;
        }
        return startStartingPhase();
    }


    private boolean startStartingPhase() {
        TheFloorIsLava plugin = TheFloorIsLava.getInstance();
        if (state != GameState.LOBBY) {
            return false;
        }
        state = GameState.STARTING;

        playerInGame.forEach(e -> {
            if (e instanceof Player p){
                p.setAllowFlight(false);
            }
        });


        World world = plugin.getWorldManager().getGameWorld();
        world.getWorldBorder().setSize(gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_PRE_RISE));
        ConfigManager<MapConfig> mapConfigManager = ConfigRegistry.getConfigManager("map");
        world.getWorldBorder().setCenter(
                mapConfigManager.getInt(MapConfigKeys.CENTER_X),
                mapConfigManager.getInt(MapConfigKeys.CENTER_Z));
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.ADVANCE_TIME, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);

        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION)) {
            TheFloorIsLava.pvp = false;
        }
        plugin.getServer().getOnlinePlayers().forEach(p -> p.getScoreboardTags().remove("inGame"));
        plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(p) != null)
                .forEach(p -> p.addScoreboardTag("inGame")
                );
        playerInGame.addAll(plugin.getServer().getOnlinePlayers().stream().filter(p -> p.getScoreboardTags().contains("inGame")).toList());
        noRespawn = false;


        Messages.broadcast("info.starting_game");
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if (!isPlayerInGame(p)){
                p.setGameMode(GameMode.SPECTATOR);
                Messages.send(p, "info.spectator_mode_not_in_team");
            }else{
                initGamePlayer(p);
                p.give(new ShopItem().giveItem(p));
            }
        }
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            Messages.broadcast("info.keep_inventory_during_preparation");
        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION))
            Messages.broadcast("info.pvp_disabled_during_preparation");

        SpreadEntityManager spreadentityManager = new SpreadEntityManager();

        Map<UUID, Location> positions = spreadentityManager.spread(
                playerInGame,
                new Location(world,
                        mapConfigManager.getInt(MapConfigKeys.CENTER_X),
                        200,
                        mapConfigManager.getInt(MapConfigKeys.CENTER_Z)),
                gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_PRE_RISE) / 2.
                , 50, true, 200
        );

        if (positions==null) {
            plugin.getLogger().warning("Impossible de répartir les joueurs, vérifiez la configuration !");
            Messages.broadcastOpError("error.cannot_spread_player");
            Bukkit.removeBossBar(new NamespacedKey(plugin, "game_bar"));
            state = GameState.LOBBY;
            cancelBossBarTask();
            return false;
        }
        if (ConfigRegistry.getConfigManager("map").getBoolean(MapConfigKeys.SPAWN_SPAWN_STRUCTURE.getKey()))
            plugin.getWorldManager().placeStructureAtSpawn();

        Bukkit.getServer().getOnlinePlayers().forEach(p -> p.teleport(plugin.getWorldManager().getPreGameSpawnLocation()));
        int startingCountdown = gameConfigManager.getInt(GameConfigKeys.STARTING_COUNTDOWN);

        if (startingCountdown > 3)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> plugin.getServer().getOnlinePlayers().forEach(
                            p->Messages.send(p,
                                    "info.starting_countdown",
                                    Placeholder.unparsed("time", formatTime(p,3*20, TextUtils.TimeFormat.SHORTEST)))
                    ),
                    startingCountdown*20L - 3*20L
            );
        if (startingCountdown > 2)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () ->  plugin.getServer().getOnlinePlayers().forEach(
                            p->Messages.send(p,
                                    "info.starting_countdown",
                                    Placeholder.unparsed("time", formatTime(p,2*20, TextUtils.TimeFormat.SHORTEST)))),
                    startingCountdown*20L - 2*20L
            );
        if (startingCountdown > 1)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () ->  plugin.getServer().getOnlinePlayers().forEach(
                            p->Messages.send(p,
                                    "info.starting_countdown",
                                    Placeholder.unparsed("time", formatTime(p,20, TextUtils.TimeFormat.SHORTEST)))),
                    startingCountdown*20L - 20L
            );
        if (startingCountdown > 0) {
            plugin.getServer().getOnlinePlayers().forEach(
                    p->Messages.send(p,
                            "info.starting_countdown", Placeholder.unparsed("time", formatTime(p,startingCountdown*20, TextUtils.TimeFormat.SHORTEST))));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> startRunningPhase(plugin, positions), startingCountdown*20L);
        return true;
    }

    private void startRunningPhase(TheFloorIsLava plugin, Map<UUID, Location> positions) {
        if (state != GameState.STARTING) {
            return;
        }
        state = GameState.RUNNING;


        bossbar = plugin.getServer().createBossBar(new NamespacedKey(plugin, "game_bar"), plainText(LangManager.getInstance().get(Bukkit.getServer(), "bossbar.lava_rising_delay")),  BarColor.BLUE, BarStyle.SOLID);
        startPreparationBossBar();

        plugin.getServer().getOnlinePlayers().forEach(bossbar::addPlayer);

        if (positions != null) {
            this.state = GameState.RUNNING;
            positions.forEach((uuid, location) -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null && playerInGame.contains(player)) {
                    player.teleport(location);
                }
            });
        }


        playerInGame.stream()
                .filter(Objects::nonNull)
                .forEach(p -> p.setRespawnLocation(p.getLocation(), true));

        int lavaRisingDelay = gameConfigManager.getInt(GameConfigKeys.LAVA_RISING_DELAY);
        plugin.getServer().getOnlinePlayers().forEach(
                p->Messages.send(p,
                        "info.lava_rising_delay",
                        Placeholder.unparsed("time", formatTime(p,lavaRisingDelay, TextUtils.TimeFormat.SHORTEST))));
        //             5min  3min  1min  30s  10s   5s  4s  3s  2s  1s
        int[] delay = {6000, 3600, 1200, 600, 200, 100, 80, 60, 40, 20};
        alertTasks.clear();
        for (int d : delay) {
            if (lavaRisingDelay > d) {
                alertTasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> plugin.getServer().getOnlinePlayers().forEach(
                                p->Messages.actionBar(p,
                                        "info.lava_rising_delay",
                                        Placeholder.unparsed("time", formatTime(p,lavaRisingDelay, TextUtils.TimeFormat.SHORTEST)))), lavaRisingDelay - d));
            }
        }
        if (!dangerManager.startPreparation()){
            plugin.getServer().getOperators().forEach(
                    off_op -> {
                        Player op = off_op.getPlayer();
                        if (op != null) {
                            Messages.send(op, "error.cannot_start_preparation_phase");
                        }
                    }
            );
        }

        playerInGame.forEach(p->
                KitManager.getInstance().applyKitToPlayer(p));

        playerInGame.forEach(p->plugin.getStatisticsManager().increment(p, StatisticType.GAMES_PLAYED));

        phaseRisingTask = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLava.getInstance(),
                this::startRisingPhase,
                gameConfigManager.getInt(GameConfigKeys.LAVA_RISING_DELAY)
        );
    }

    public boolean earlyStartRisingPhase() {
        if (state != GameState.RUNNING) {
            return false;
        }
        if (dangerManager.getState() != DangerManager.DangerState.PREPARATION) return false;
        cancelBossBarTask();
        startRisingPhase();
        Bukkit.getScheduler().cancelTask(phaseRisingTask);
        for (int taskId : alertTasks) {
            if (Bukkit.getScheduler().isQueued(taskId))
                Bukkit.getScheduler().cancelTask(taskId);
        }
        return true;
    }

    public boolean isGameWinning() {
        if (state != GameState.RUNNING) {
            return false;
        }
        if (dangerManager.getState() == DangerManager.DangerState.PREPARATION) {
            return false;
        }
        return TeamManager.getInstance().getTeamAlive().size() <= 1;
    }

    private void startRisingPhase() {
        cancelBossBarTask();

        bossbar.setTitle("La lave monte !");
        bossbar.setProgress(1.0);
        bossbar.setColor(BarColor.RED);

        TheFloorIsLava plugin = TheFloorIsLava.getInstance();
        World world = plugin.getWorldManager().getGameWorld();
        if (world != null)
            world.setGameRule(GameRules.KEEP_INVENTORY, false);
        TheFloorIsLava.pvp = true;
        noRespawn = true;
        if (world == null){
            plugin.getLogger().warning("Impossible de récupérer le monde de jeu pour démarrer la phase 2");
            return;
        }
        world.getWorldBorder().changeSize(gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_DURING_RISE), gameConfigManager.getInt(GameConfigKeys.BORDER_RESIZE_TIME) * 20L);
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            Messages.broadcast("info.inventory_no_longer_saved");
        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION))
            Messages.broadcast("info.pvp_enabled");
        dangerManager.startRising();
    }

    // --- Joueurs / compatibilité ---

    public boolean isPlayerInGame(Player player) {
        return playerInGame.contains(player);
    }

    public boolean getNoRespawn() {
        return noRespawn;
    }

    public void stopGame() {
        TheFloorIsLava plugin = TheFloorIsLava.getInstance();
        cancelBossBarTask();

        if (bossbar != null) {
            bossbar.removeAll();
            bossbar = null;
        }
        World world = plugin.getWorldManager().getGameWorld();
        if (world != null)
            world.getWorldBorder().setSize(world.getWorldBorder().getSize());
        if (state == GameState.LOBBY) {
            return;
        }
        Bukkit.getScheduler().cancelTask(phaseRisingTask);
        dangerManager.reset();
        state = GameState.LOBBY;
        playerInGame.clear();
        noRespawn = false;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            initLobbyPlayer(p);
        }
        Messages.broadcastOp("info.game_stopped");
    }

    public GameState getState() {
        return state;
    }

    public DangerManager getDangerManager() {
        return dangerManager;
    }

    private void startPreparationBossBar() {
        int preparationTime = gameConfigManager.getInt(GameConfigKeys.LAVA_RISING_DELAY);

        bossBarTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                TheFloorIsLava.getInstance(),
                new Runnable() {

                    int remaining = preparationTime;

                    @Override
                    public void run() {

                        if (state != GameState.LOBBY) {
                            if (remaining <= 0) {
                                bossbar.setTitle("La lave monte !");
                                bossbar.setProgress(1.0);
                                cancelBossBarTask();
                                return;
                            }
                            bossbar.setTitle(
                                    plainText(Messages.component(Bukkit.getServer(), "bossbar.lava_rising_delay", Placeholder.unparsed("time", formatTime(remaining, TextUtils.TimeFormat.SHORTEST_CLOCK_LIKE))))
                            );

                            bossbar.setProgress(
                                    Math.max(0, Math.min(1,
                                            (double) remaining / preparationTime
                                    ))
                            );

                            remaining = remaining - 20;
                        }
                    }
                },
                0,
                20 // toutes les secondes
        );
    }
    private void cancelBossBarTask() {
        if (bossBarTask != -1) {
            Bukkit.getScheduler().cancelTask(bossBarTask);
            bossBarTask = -1;
        }
    }

    public void addPlayerToBossBar(Player player) {
        if (bossbar != null) {
            bossbar.addPlayer(player);
        }
    }

    public void endGame() {
        if (state != GameState.RUNNING) {
            return;
        }
        state = GameState.ENDING;
        cancelBossBarTask();
        if (bossbar != null) {
            bossbar.removeAll();
            bossbar = null;
        }
        dangerManager.reset();
        playerInGame.clear();
        noRespawn = false;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::stopGame, 20L * 5); // Attendre 5 secondes avant de revenir au lobby
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

}
