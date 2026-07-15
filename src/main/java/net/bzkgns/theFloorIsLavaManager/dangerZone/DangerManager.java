package net.bzkgns.theFloorIsLavaManager.dangerZone;

import net.bzkgns.theFloorIsLavaManager.GameState;
import net.bzkgns.theFloorIsLavaManager.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.items.ShopItem;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.formatTime;

public class DangerManager {

    private final TheFloorIsLavaManager plugin;
    private final DangerConfig config;

    private GameState state = GameState.LOBBY;
    private GameState stateBeforePause;

    private double dangerLevel;
    private int oldDangerLevelPlaced;
    private double increaseAmount;

    private boolean noRespawn = false;

    private List<Player> playerInGame = new ArrayList<>();

    private int increaseTask = -1;
    private int damageTask = -1;
    private int particleTask = -1;
    private int placeLavaTask = -1;
    private int phase2Task = -1;

    private static final int DISPLAY_PERIOD = 5;

    public DangerManager(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
        this.config = DangerConfig.loadFrom(plugin.getConfig());
        resetRuntimeState();
    }

    /** Réinitialise l'état d'une partie (niveau de lave, joueurs en jeu...) sans toucher à la config. */
    private void resetRuntimeState() {
        dangerLevel = config.getStartLevel();
        oldDangerLevelPlaced = config.getStartLevel() - 1;
        increaseAmount = config.initialIncreaseAmount();
        playerInGame = new ArrayList<>();
        noRespawn = false;
    }

    private void cancelIfRunning(int taskId) {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    // --- Config ---

    public DangerConfig getConfig() {
        return config;
    }

    /** La config ne se modifie qu'en dehors d'une partie, pour éviter tout état incohérent en plein jeu. */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEditConfig() {
        return state == GameState.LOBBY;
    }

    public GameState getState() {
        return state;
    }

    // --- Cycle de vie de la partie ---

    public boolean start() {
        if (state != GameState.LOBBY) {
            return false;
        }
        state = GameState.PREPARING;

        World world = plugin.getWorldManager().getGameWorld();
        world.getWorldBorder().setSize(config.getBorderSizePreRise());
        world.getWorldBorder().setCenter(0, 0);
        cancelIfRunning(damageTask);
        damageTask = -1;
        if (config.isKeepInventoryDuringPreparation())
            world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.ADVANCE_TIME, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
        cancelIfRunning(particleTask);
        particleTask = -1;
        if (config.isDisablePvpDuringPreparation()) {
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
            }
        }
        if (config.isKeepInventoryDuringPreparation())
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires sont sauvegardés (keepInventory)"));
        if (config.isDisablePvpDuringPreparation())
            TextUtils.broadcastMessage(TextUtils.infoMessage("Le PvP est désactivé"));

        int lavaRisingDelay = config.getLavaRisingDelay();
        TextUtils.broadcastMessage(TextUtils.infoMessage("La lave va commencer à monter dans " + lavaRisingDelay / (20 * 60) + " minutes"));



        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:tfl_game run spreadplayers 0 0 50 " + config.getBorderSizePreRise() / 2 + " under 200 true @a[tag=inGame]");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a[tag=inGame] at @s run spawnpoint");
        for (Player p : Bukkit.getOnlinePlayers()) {
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
        //             5min  3min  1min  30s  10s   5s  4s  3s  2s  1s
        int[] delay = {6000, 3600, 1200, 600, 200, 100, 80, 60, 40, 20};
        for (int d : delay) {
            if (lavaRisingDelay > d) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave va commencer à monter dans " + formatTime(d, TextUtils.TimeFormat.SHORTEST) + "...")), lavaRisingDelay - d);
            }
        }

        phase2Task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::startPhase2, lavaRisingDelay);
        return true;
    }

    public void stop() {
        World world = plugin.getWorldManager().getGameWorld();
        if (world != null)
            world.getWorldBorder().setSize(world.getWorldBorder().getSize());
        state = GameState.LOBBY;
        cancelIfRunning(placeLavaTask);
        placeLavaTask = -1;
        cancelIfRunning(increaseTask);
        increaseTask = -1;
        cancelIfRunning(damageTask);
        damageTask = -1;
        cancelIfRunning(phase2Task);
        phase2Task = -1;
        cancelIfRunning(particleTask);
        particleTask = -1;
        resetRuntimeState();
    }

    /** Ne gère la pause que pendant la montée de la lave (RISING). Renvoie false sinon. */
    public boolean pause() {
        if (state != GameState.RISING && state != GameState.PREPARING) {
            return false;
        }
        stateBeforePause = state;
        state = GameState.PAUSED;
        cancelIfRunning(increaseTask);
        increaseTask = -1;
        cancelIfRunning(damageTask);
        damageTask = -1;
        cancelIfRunning(particleTask);
        particleTask = -1;
        cancelIfRunning(placeLavaTask);
        placeLavaTask = -1;
        return true;
    }

    /** Relance les tâches annulées par pause(). Renvoie false si la partie n'était pas en pause. */
    public boolean resume() {
        if (state != GameState.PAUSED) {
            return false;
        }
        state = stateBeforePause;
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickIncrease, 0, 1);
        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickDamage, 0, config.getDamageEvery());
        if (config.isShowAlert()) {
            particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickParticles, 0, DISPLAY_PERIOD);
        }
        if (config.isPlaceLava()) {
            placeLavaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickPlaceLava, 0, 1);
        }
        return true;
    }

    // --- Niveau de danger ---

    public double getDangerLevel() {
        return dangerLevel;
    }

    /** Recale oldDangerLevelPlaced pour éviter un rattrapage massif de pose de lave au tick suivant. */
    public void setDangerLevel(double level) {
        this.dangerLevel = level;
        this.oldDangerLevelPlaced = (int) round(level) - 1;
    }

    public double getIncreaseAmount() {
        return increaseAmount;
    }

    /**
     * Ajuste uniquement la valeur courante. Ne démarre ni n'arrête aucune tâche :
     * c'est start()/startPhase2()/pause()/resume() qui possèdent le cycle de vie du scheduler.
     * Si la lave est déjà en train de monter, ce réglage sera écrasé au prochain tick
     * par tickIncrease() qui recalcule la vitesse selon la zone (sous/au-dessus de la surface).
     */
    public void setIncreaseAmount(double amount) {
        this.increaseAmount = amount;
    }

    // --- Tâches (extraites en méthodes nommées pour être réutilisables par resume()) ---

    private void tickIncrease() {
        if (dangerLevel < config.getEndLevel()) {
            increaseAmount = config.increaseAmountFor(dangerLevel);
            dangerLevel += increaseAmount;
        } else if (increaseTask != -1) {
            Bukkit.getScheduler().cancelTask(increaseTask);
            increaseTask = -1;
        }
        TeamRespawnManager.getInstance().checkRespawnPointValidity();
    }

    private void tickDamage() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().getY() < dangerLevel) {
                p.damage(config.getDamage());
            }
        }
    }

    private void tickParticles() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            double diffLevel = abs(p.getLocation().y() - dangerLevel);
            if (diffLevel < 10) {
                if (diffLevel < 5 && p.getLocation().y() > dangerLevel) {
                    p.sendActionBar(Component.text(
                            "!!ATTENTION!! La zone se rapproche vous êtes à " + String.format("%.2f", diffLevel) + " blocs de la zone !!"
                    ).color(TextColor.color(Color.RED.asRGB())));
                } else if (p.getLocation().y() < dangerLevel) {
                    p.sendActionBar(Component.text(
                            "VOUS ETES DANS LA ZONE REMONTEZ VIIITE !!!"
                    ).color(TextColor.color(Color.RED.asRGB())));
                }
            }
        }
    }

    private void tickPlaceLava() {
        int increaseSize = config.getIncreaseSize();
        if (oldDangerLevelPlaced + increaseSize >= round(dangerLevel)) {
            return;
        }

        double diff = increaseSize / increaseAmount;
        if (diff > 100) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave va monter dans 2 secondes...")), round(diff) - 40);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave va monter dans 3 secondes...")), round(diff) - 60);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave va monter dans 1 secondes...")), round(diff) - 20);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TextUtils.sendActionBar(TextUtils.infoMessage("La lave monte !!")), round(diff));
        }

        World world = plugin.getWorldManager().getGameWorld();
        Location wbCenter = world.getWorldBorder().getCenter();
        double wbSize = world.getWorldBorder().getSize();
        int lavaMargin = config.getLavaMargin();
        Vector3i edgeMin = new Vector3i((int) (wbCenter.getX() - round(wbSize / 2)) - lavaMargin, oldDangerLevelPlaced + 1, (int) (wbCenter.getZ() - round(wbSize / 2)) - lavaMargin);
        Vector3i edgeMax = new Vector3i((int) (wbCenter.getX() + round(wbSize / 2)) + lavaMargin, (int) round(dangerLevel), (int) (wbCenter.getZ() + round(wbSize / 2)) + lavaMargin);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Block> toUpdate = new ArrayList<>();

            for (int x = edgeMin.x; x <= edgeMax.x; ++x) {
                for (int y = edgeMin.y; y <= edgeMax.y; ++y) {
                    for (int z = edgeMin.z; z <= edgeMax.z; ++z) {
                        Block block = (new Location(world, x, y, z)).getBlock();
                        if (block.getType() == Material.AIR) {
                            toUpdate.add(block);
                        }
                    }
                }
            }
            startBatchPlacement(toUpdate);
        });

        oldDangerLevelPlaced = (int) round(dangerLevel);
    }

    private void startPhase2() {
        state = GameState.RISING;
        World world = plugin.getWorldManager().getGameWorld();
        if (world != null)
            world.setGameRule(GameRules.KEEP_INVENTORY, false);

        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickIncrease, 0, 1);
        TheFloorIsLavaManager.pvp = true;
        noRespawn = true;

        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickDamage, 20, config.getDamageEvery());
        if (world == null){
            plugin.getLogger().warning("Impossible de récupérer le monde de jeu pour démarrer la phase 2");
            return;
        }
        world.getWorldBorder().changeSize(config.getBorderSizeDuringRise(), config.getBorderResizeTime() * 20L);

        TextUtils.broadcastMessage(TextUtils.infoMessage("!!ATTENTION!! La lave commence à monter !"));
        if (config.isKeepInventoryDuringPreparation())
            TextUtils.broadcastMessage(TextUtils.infoMessage("Les inventaires ne sont plus sauvegardés"));
        if (config.isDisablePvpDuringPreparation())
            TextUtils.broadcastMessage(TextUtils.infoMessage("Le PvP est activé"));
        TextUtils.broadcastMessage(TextUtils.infoMessage("Le respawn est désactivé"));
        TextUtils.broadcastMessage(TextUtils.infoMessage("La zone se rétrécit"));

        if (config.isPlaceLava()) {
            placeLavaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickPlaceLava, 1, 1);
        }

        if (config.isShowAlert()) {
            particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickParticles, 0, DISPLAY_PERIOD);
        }
    }

    private void startBatchPlacement(List<Block> blocks) {
        final int batchSize = 2000;

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            for (int i = 0; i < batchSize && !blocks.isEmpty(); i++) {
                blocks.removeLast().setType(Material.LAVA);
            }

            if (blocks.isEmpty()) {
                task.cancel();
            }
        }, 1, 1);
    }

    // --- Joueurs / compatibilité ---

    public boolean isPlayerInGame(Player player) {
        return playerInGame.contains(player);
    }

    public boolean getNoRespawn() {
        return noRespawn;
    }

    /**
     * Conservé pour compatibilité avec le code existant (TheFloorIslavaListener, /tfl team) :
     * remplace l'ancien booléen hasStarted. true dès la préparation jusqu'à l'arrêt.
     */
    public boolean getHasStarted() {
        return state == GameState.PREPARING || state == GameState.RISING || state == GameState.PAUSED;
    }
}