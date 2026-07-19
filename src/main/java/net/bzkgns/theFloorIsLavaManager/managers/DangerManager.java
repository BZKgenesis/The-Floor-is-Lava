package net.bzkgns.theFloorIsLavaManager.managers;

import net.bzkgns.theFloorIsLavaManager.config.danger.DangerConfigKeys;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.config.ConfigManager;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.config.danger.DangerConfig;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class DangerManager {

    public enum DangerState {
        NONE,
        PREPARATION,
        RISING,
        PAUSED
    }

    private DangerState stateBeforePause = DangerState.NONE;

    private DangerState state = DangerState.NONE;

    private final TheFloorIsLavaManager plugin;
    private final ConfigManager<DangerConfig> dangerConfigManager;

    private double dangerLevel;
    private int oldDangerLevelPlaced;
    private double increaseAmount;

    private int increaseTask = -1;
    private int damageTask = -1;
    private int particleTask = -1;
    private int placeLavaTask = -1;
    private int phase2Task = -1;

    private static final int DISPLAY_PERIOD = 5;

    public DangerManager(ConfigManager<DangerConfig> dangerConfigManager) {
        this.plugin = TheFloorIsLavaManager.getInstance();
        this.dangerConfigManager = dangerConfigManager;
        resetRuntimeState();
    }

    /** Réinitialise l'état d'une partie (niveau de lave, joueurs en jeu...) sans toucher à la config. */
    private void resetRuntimeState() {
        dangerLevel = dangerConfigManager.getInt(DangerConfigKeys.START_LEVEL);
        oldDangerLevelPlaced = (int) (dangerLevel - 1);
        increaseAmount = dangerConfigManager.getConfig().initialIncreaseAmount();
    }

    private void cancelIfRunning(int taskId) {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    // --- Config ---

    /** La config ne se modifie qu'en dehors d'une partie, pour éviter tout état incohérent en plein jeu. */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEditConfig() {
        return state == DangerState.NONE;
    }

    public DangerState getState() {
        return state;
    }

    // --- Cycle de vie de la partie ---

    public boolean startPreparation(){
        World world = plugin.getWorldManager().getGameWorld();
        if (world == null){
            plugin.getLogger().warning("Impossible de récupérer le monde de jeu pour démarrer la préparation");
            return false;
        }
        state = DangerState.PREPARATION;
        cancelIfRunning(damageTask);
        damageTask = -1;
        cancelIfRunning(particleTask);
        particleTask = -1;
        return true;
    }

    public void reset() {
        state = DangerState.NONE;
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
        if (state != DangerState.PREPARATION && state != DangerState.RISING) {
            return false;
        }
        stateBeforePause = state;
        state = DangerState.PAUSED;
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
        if (state != DangerState.PAUSED) {
            return false;
        }
        state = stateBeforePause;
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickIncrease, 0, 1);
        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickDamage, 0, dangerConfigManager.getInt(DangerConfigKeys.DAMAGE_EVERY));
        if (dangerConfigManager.getBoolean(DangerConfigKeys.SHOW_ALERT) ) {
            particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickParticles, 0, DISPLAY_PERIOD);
        }
        if (dangerConfigManager.getBoolean(DangerConfigKeys.PLACE_LAVA)) {
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
        if (dangerLevel < dangerConfigManager.getInt("end-level")) {
            increaseAmount = (dangerConfigManager.getConfig()).increaseAmountFor(dangerLevel);
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
                p.damage(dangerConfigManager.getDouble(DangerConfigKeys.DAMAGE));
            }
        }
    }

    private void tickParticles() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            double diffLevel = abs(p.getLocation().y() - dangerLevel);
            if (diffLevel < 10) {
                if (diffLevel < 5 && p.getLocation().y() > dangerLevel) {
                    Messages.actionBar(p, "action.lava_near_player",Placeholder.parsed("nb_blocs", String.format("%.2f", diffLevel)));
                } else if (p.getLocation().y() < dangerLevel) {
                    Messages.actionBar(p, "action.lava_under_player");
                }
            }
        }
    }

    private void tickPlaceLava() {
        int increaseSize = dangerConfigManager.getInt(DangerConfigKeys.INCREASE_SIZE);
        if (oldDangerLevelPlaced + increaseSize >= round(dangerLevel)) {
            return;
        }

        double diff = increaseSize / increaseAmount;
        if (diff > 100) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Messages.broadcastActionBar("action_bar.lava_rising_delay", Placeholder.parsed("time", "3")), round(diff) - 40);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Messages.broadcastActionBar("action_bar.lava_rising_delay", Placeholder.parsed("time", "2")), round(diff) - 60);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Messages.broadcastActionBar("action_bar.lava_rising_delay", Placeholder.parsed("time", "1")), round(diff) - 20);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Messages.broadcastActionBar("action_bar.lava_rising_delay"), round(diff));
        }

        World world = plugin.getWorldManager().getGameWorld();
        Location wbCenter = world.getWorldBorder().getCenter();
        double wbSize = world.getWorldBorder().getSize();
        int lavaMargin = dangerConfigManager.getInt(DangerConfigKeys.LAVA_MARGIN);
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

    public void startRising() {
        state = DangerState.RISING;

        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickIncrease, 0, 1);

        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickDamage, 20, dangerConfigManager.getInt(DangerConfigKeys.DAMAGE_EVERY));

        Messages.broadcast("lava_rising_start");
        Messages.broadcast("respawn_disabled");
        Messages.broadcast("arena_shrinking");

        if (dangerConfigManager.getBoolean(DangerConfigKeys.PLACE_LAVA)) {
            placeLavaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickPlaceLava, 1, 1);
        }

        if (dangerConfigManager.getBoolean(DangerConfigKeys.SHOW_ALERT)) {
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

}