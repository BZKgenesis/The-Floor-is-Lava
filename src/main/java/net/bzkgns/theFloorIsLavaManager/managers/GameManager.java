package net.bzkgns.theFloorIsLavaManager.managers;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.config.ConfigLoader;
import net.bzkgns.theFloorIsLavaManager.config.ConfigManager;
import net.bzkgns.theFloorIsLavaManager.config.danger.DangerConfig;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfig;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfigKeys;
import net.bzkgns.theFloorIsLavaManager.items.ShopItem;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.formatTime;

public class GameManager {

    private GameState state = GameState.LOBBY;


    private final DangerManager dangerManager;

    private BossBar bossbar;

    private final ConfigManager<GameConfig> gameConfigManager;

    private int phaseRisingTask = -1;
    private int bossBarTask = -1;

    private boolean noRespawn = false;

    private final List<Entity> playerInGame;

    public GameManager() {



        gameConfigManager = ConfigLoader.load(
                new GameConfig()
        );

        ConfigManager<DangerConfig> dangerConfigManager = ConfigLoader.load(
                new DangerConfig()
        );

        ConfigRegistry.addConfig(gameConfigManager);
        ConfigRegistry.addConfig(dangerConfigManager);
        this.dangerManager = new DangerManager(dangerConfigManager);
        playerInGame = new ArrayList<>();
    }
    public boolean canEditConfig() {
        return state == GameState.LOBBY;
    }

    public static void initGamePlayer(Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute == null) {
            TheFloorIsLavaManager.getInstance().getLogger().warning("Impossible de récupérer l'attribut MAX_HEALTH pour le joueur " + player.getName());
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
    }

    public static void initLobbyPlayer(Player player) {
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(plugin.getWorldManager().getLobbySpawnLocation());
        player.setRespawnLocation(plugin.getWorldManager().getLobbySpawnLocation(), true);
        player.getInventory().clear();
        player.setAllowFlight(true);
    }

    public boolean startGame() {
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

        NamespacedKey key = new NamespacedKey(plugin, "game_bar");

        if (Bukkit.getBossBar(key) != null) {
            Bukkit.removeBossBar(key);
        }

        if (gameConfigManager.getInt(GameConfigKeys.MIN_NB_TEAM) > TeamManager.getInstance().getTeams().size()) {
            TextUtils.broadcastMessageOp(TextUtils.errorMessage("Impossible de démarrer le jeu, il faut au moins " + gameConfigManager.getInt(GameConfigKeys.MIN_NB_TEAM) + " équipe(s)."));
            return false;
        }


        bossbar = plugin.getServer().createBossBar(new NamespacedKey(plugin, "game_bar"),"Phase de Préparation - Temps restant : 0:00",  BarColor.BLUE, BarStyle.SOLID);
        startPreparationBossBar();

        plugin.getServer().getOnlinePlayers().forEach(bossbar::addPlayer);

        if (state != GameState.LOBBY) {
            return false;
        }
        state = GameState.RUNNING;
        World world = plugin.getWorldManager().getGameWorld();
        world.getWorldBorder().setSize(gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_PRE_RISE));
        world.getWorldBorder().setCenter(0, 0);
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.ADVANCE_TIME, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);

        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION)) {
            TheFloorIsLavaManager.pvp = false;
        }
        plugin.getServer().getOnlinePlayers().forEach(p -> p.getScoreboardTags().remove("inGame"));
                plugin.getServer().getOnlinePlayers().stream()
                        .filter(p -> plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(p) != null)
                        .forEach(p -> p.addScoreboardTag("inGame")
                        );
        playerInGame.addAll(plugin.getServer().getOnlinePlayers().stream().filter(p -> p.getScoreboardTags().contains("inGame")).toList());
        playerInGame.addAll(plugin.getWorldManager().getGameWorld().getEntities().stream().filter(e -> e.getScoreboardTags().contains("inGame")).toList());
        noRespawn = false;

        TextUtils.broadcastMessage(TextUtils.infoMessage("Le jeu commence !"));
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if (!isPlayerInGame(p)){
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(TextUtils.infoMessage("Vous êtes en mode spectateur car vous n'êtes pas dans une équipe."));
            }else{
                initGamePlayer(p);
                p.give(new ShopItem().giveItem());
            }
        }
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires sont sauvegardés (keepInventory)"));
        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Le PvP est désactivé"));


        SpreadEntityManager spreadentityManager = new SpreadEntityManager();
        boolean success = spreadentityManager.spread(
                playerInGame,
                new Location(world, 0, 200, 0),
                gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_PRE_RISE) / 2.
                , 50, true, 200
        );

        if (!success) {
            plugin.getLogger().warning("Impossible de répartir les joueurs, vérifiez la configuration !");
            TextUtils.broadcastMessageOp(TextUtils.errorMessage("Impossible de répartir les joueurs, vérifiez la configuration !"));
            Bukkit.removeBossBar(new NamespacedKey(plugin, "game_bar"));
            state = GameState.LOBBY;
            cancelBossBarTask();
            return false;
        }
        playerInGame.forEach(e -> {
            if (e instanceof Player p){
                p.setAllowFlight(false);
            }
        });

        playerInGame.stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e )
                .forEach(p -> p.setRespawnLocation(p.getLocation(), true));

        int lavaRisingDelay = gameConfigManager.getInt(GameConfigKeys.LAVA_RISING_DELAY);
        TextUtils.broadcastMessage(TextUtils.infoMessage("La lave va commencer à monter dans " + lavaRisingDelay / (20 * 60) + " minutes"));
        //             5min  3min  1min  30s  10s   5s  4s  3s  2s  1s
        int[] delay = {6000, 3600, 1200, 600, 200, 100, 80, 60, 40, 20};
        for (int d : delay) {
            if (lavaRisingDelay > d) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave va commencer à monter dans " + formatTime(d, TextUtils.TimeFormat.SHORTEST) + "...")), lavaRisingDelay - d);
            }
        }
        if (!dangerManager.startPreparation()){
            plugin.getServer().getOperators().forEach(
                    off_op -> {
                        Player op = off_op.getPlayer();
                        if (op != null) {
                            op.sendMessage(TextUtils.errorMessage("Impossible de démarrer la phase de préparation, vérifiez la configuration !"));
                        }
                    }
            );
        }

        phaseRisingTask = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLavaManager.getInstance(),
                this::startRisingPhase,
                gameConfigManager.getInt(GameConfigKeys.LAVA_RISING_DELAY)
        );
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

        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
        World world = plugin.getWorldManager().getGameWorld();
        if (world != null)
            world.setGameRule(GameRules.KEEP_INVENTORY, false);
        TheFloorIsLavaManager.pvp = true;
        noRespawn = true;
        if (world == null){
            plugin.getLogger().warning("Impossible de récupérer le monde de jeu pour démarrer la phase 2");
            return;
        }
        world.getWorldBorder().changeSize(gameConfigManager.getInt(GameConfigKeys.BORDER_SIZE_DURING_RISE), gameConfigManager.getInt(GameConfigKeys.BORDER_RESIZE_TIME) * 20L);
        if (gameConfigManager.getBoolean(GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires ne sont plus sauvegardés"));
        if (gameConfigManager.getBoolean(GameConfigKeys.DISABLE_PVP_DURING_PREPARATION))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Le PvP est activé"));
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
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
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
                TheFloorIsLavaManager.getInstance(),
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
                                    "Phase de préparation - Temps restant : "
                                            + formatTime(remaining, TextUtils.TimeFormat.SHORTEST)
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
        state = GameState.LOBBY;
        cancelBossBarTask();
        if (bossbar != null) {
            bossbar.removeAll();
            bossbar = null;
        }
        dangerManager.reset();
        playerInGame.clear();
        noRespawn = false;
    }

}
