package net.bzkgns.theFloorIsLavaManager.managers;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.config.ConfigManager;
import net.bzkgns.theFloorIsLavaManager.config.danger.DangerConfig;
import net.bzkgns.theFloorIsLavaManager.config.game.GameConfig;
import net.bzkgns.theFloorIsLavaManager.items.ShopItem;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.LoggingCommandSender;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.formatTime;

public class GameManager {

    private GameState state = GameState.LOBBY;


    private final DangerManager dangerManager;

    private BossBar bossbar;

    private final ConfigManager<GameConfig> gameConfigManager = new ConfigManager<>(new GameConfig());
    private final ConfigManager<DangerConfig> dangerConfigManager = new ConfigManager<>(new DangerConfig());

    private int phaseRisingTask = -1;
    private int bossBarTask = -1;

    private boolean noRespawn = false;

    private List<Player> playerInGame;

    public GameManager() {
        this.dangerManager = new DangerManager(dangerConfigManager);
        playerInGame = new ArrayList<>();
    }
    public boolean canEditConfig() {
        return state == GameState.LOBBY;
    }


    public boolean startGame() {
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

        plugin.getServer().removeBossBar(new NamespacedKey(plugin, "game_bar"));

        if (gameConfigManager.getInt("min-nb-teams") > TeamManager.getInstance().getTeams().size()) {
            TextUtils.broadcastMessageOp(TextUtils.errorMessage("Impossible de démarrer le jeu, il faut au moins " + gameConfigManager.getInt("min-nb-teams") + " équipe(s)."));
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
        world.getWorldBorder().setSize(gameConfigManager.getInt("border-size-prerise"));
        world.getWorldBorder().setCenter(0, 0);
        if (gameConfigManager.getBoolean("keep-inventory-during-preparation"))
            world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.ADVANCE_TIME, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);

        if (gameConfigManager.getBoolean("disable-pvp-during-preparation")) {
            TheFloorIsLavaManager.pvp = false;
        }
        playerInGame.addAll(plugin.getServer().getOnlinePlayers().stream().filter(p -> plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(p) != null).toList());
        noRespawn = false;

        TextUtils.broadcastMessage(TextUtils.infoMessage("Le jeu commence !"));
        for(Player p : plugin.getServer().getOnlinePlayers()){
            p.removeScoreboardTag("inGame");
            if (!isPlayerInGame(p)){
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(TextUtils.infoMessage("Vous êtes en mode spectateur car vous n'êtes pas dans une équipe."));
            }else{
                p.addScoreboardTag("inGame");
                AttributeInstance healthAttribute = p.getAttribute(Attribute.MAX_HEALTH);
                if (healthAttribute == null) {
                    plugin.getLogger().warning("Impossible de récupérer l'attribut MAX_HEALTH pour le joueur " + p.getName());
                    continue;
                }
                p.setHealth(healthAttribute.getValue());
                p.setFoodLevel(20);
                p.setSaturation(20);
                p.setExhaustion(0);
                p.getInventory().clear();
                p.getInventory().setArmorContents(new ItemStack[0]);
                p.give(new ShopItem().giveItem());
                p.setGameMode(GameMode.SURVIVAL);
            }
        }
        if (gameConfigManager.getBoolean("keep-inventory-during-preparation"))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires sont sauvegardés (keepInventory)"));
        if (gameConfigManager.getBoolean("disable-pvp-during-preparation"))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Le PvP est désactivé"));


        LoggingCommandSender commandSender = new LoggingCommandSender(Bukkit.getConsoleSender());
        Bukkit.dispatchCommand(commandSender, "execute in minecraft:tfl_game run spreadplayers 0 0 50 " + gameConfigManager.getInt("border-size-prerise") / 2 + " under 200 true @a[tag=inGame]");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!commandSender.getMessages().isEmpty())
                System.out.println("Command output: " + commandSender.getMessages().getLast());
            else
                System.out.println("Command output: No output from command");
        });


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a[tag=inGame] at @s run spawnpoint");

        int lavaRisingDelay = gameConfigManager.getInt("lava-rising-delay");
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
                gameConfigManager.getInt("lava-rising-delay")
        );
        return true;
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
        world.getWorldBorder().changeSize(gameConfigManager.getInt("border-size-during-rise"), gameConfigManager.getInt("border-resize-time") * 20L);
        if (gameConfigManager.getBoolean("keep-inventory-during-preparation"))
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires ne sont plus sauvegardés"));
        if (gameConfigManager.getBoolean("disable-pvp-during-preparation"))
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
        int preparationTime = gameConfigManager.getInt("lava-rising-delay");

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

                            remaining--;
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

}
