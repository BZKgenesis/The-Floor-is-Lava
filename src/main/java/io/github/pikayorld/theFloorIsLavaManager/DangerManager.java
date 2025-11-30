package io.github.pikayorld.theFloorIsLavaManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.joml.Vector3i;


import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.round;

public class DangerManager {

    private final TheFloorIsLavaManager plugin;



    private double dangerLevel;
    private int Old_dangerLevelPlaced;
    private int maxdangerLevel;
    private int mindangerLevel;
    private double increaseAmount;
    private int totalTime;
    private final double damage;
    private final int damageEvery;
    private final boolean placeLava;
    private final boolean showAlert;
    private final int lavaMargin;
    private final int increaseSize;
    private final int lavaRisingDelay;

    private int increaseTask = -1;
    private int damageTask = -1;
    private int particleTask = -1;
    private int placeLavaTask = -1;
    private int phase2Task = -1;

    private boolean isPaused = false;




    private final int DISPLAY_PERIOD = 5;

    public DangerManager(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;

        mindangerLevel = plugin.getConfig().getInt("danger.start-level");
        maxdangerLevel = plugin.getConfig().getInt("danger.end-level");
        totalTime = plugin.getConfig().getInt("danger.total-time");
        increaseAmount = (double) (maxdangerLevel - mindangerLevel) /totalTime;
        damage = plugin.getConfig().getDouble("danger.damage");
        damageEvery = plugin.getConfig().getInt("danger.damage-every");
        placeLava = plugin.getConfig().getBoolean("danger.place-lava");
        showAlert = plugin.getConfig().getBoolean("danger.show-alert");
        lavaMargin = plugin.getConfig().getInt("danger.lava-margin");
        increaseSize = plugin.getConfig().getInt("danger.increase-size");
        lavaRisingDelay = plugin.getConfig().getInt("danger.lava-rising-delay");

        dangerLevel = mindangerLevel;

        Old_dangerLevelPlaced = mindangerLevel-1;
    }

    private void setIncreaseTask(){
        if (dangerLevel < maxdangerLevel){
            dangerLevel=dangerLevel+ (double)increaseAmount;
        }else{
            Bukkit.getScheduler().cancelTask(increaseTask);
        }
    }

    public void start() {
        isPaused = false;
        World world = Bukkit.getServer().getWorlds().getFirst();
        world.getWorldBorder().setSize(200);
        world.getWorldBorder().setCenter(world.getSpawnLocation());
        Bukkit.getScheduler().cancelTask(damageTask);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        if (particleTask != -1) Bukkit.getScheduler().cancelTask(particleTask);

        phase2Task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::startPhase2,lavaRisingDelay);
    }

    public void stop() {
        isPaused = false;
        if (increaseTask != -1) Bukkit.getScheduler().cancelTask(increaseTask);
        if (damageTask != -1) Bukkit.getScheduler().cancelTask(damageTask);
        if (phase2Task != -1) Bukkit.getScheduler().cancelTask(phase2Task);
        if (particleTask != -1) Bukkit.getScheduler().cancelTask(particleTask);
    }

    public void pause() {
        isPaused = true;
        Bukkit.getScheduler().cancelTask(increaseTask);
    }

    public double getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public double getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIncreaseAmount(double amount) {
        this.increaseAmount = amount;
        Bukkit.getScheduler().cancelTask(increaseTask);
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setIncreaseTask, 0, 1);
    }

    private void startPhase2(){
        World world = Bukkit.getWorld("world");
        if (world != null)
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
        // Tâche qui augmente le niveau
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setIncreaseTask, 0, 1);

        // Tâche qui inflige les dégâts
        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().getY() < dangerLevel) {
                    p.damage(damage);
                }
            }
        }, 20, damageEvery);
        plugin.getServer().getWorlds().getFirst().getWorldBorder().setSize(75,5*60);


        if (placeLava){
            placeLavaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                public void run(){
                    if (Old_dangerLevelPlaced+increaseSize < round(dangerLevel)){
                        World world = plugin.getServer().getWorlds().getFirst();
                        Location wbCenter = world.getWorldBorder().getCenter();
                        double wbSize = world.getWorldBorder().getSize();
                        Vector3i edgeMin = new Vector3i((int) (wbCenter.getX()-round(wbSize/2))-lavaMargin, Old_dangerLevelPlaced+1, (int) (wbCenter.getZ()-round(wbSize/2))-lavaMargin);
                        Vector3i edgeMax = new Vector3i((int) (wbCenter.getX()+round(wbSize/2))+lavaMargin, (int)round(dangerLevel), (int) (wbCenter.getZ()+round(wbSize/2))+lavaMargin);

                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            List<Block> toUpdate = new ArrayList<>();

                            for(int x = edgeMin.x; x <= edgeMax.x; ++x) {
                                for(int y = edgeMin.y; y <= edgeMax.y; ++y) {
                                    for(int z = edgeMin.z; z <= edgeMax.z; ++z) {
                                        Block block = (new Location(world, (double)x, (double)y, (double)z)).getBlock();
                                        if (block.getType() == Material.AIR) {
                                            toUpdate.add(block);
                                        }
                                    }
                                }
                            }
                            // Maintenant traitement batché sur le thread principal
                            startBatchPlacement(toUpdate);
                        });

                        Old_dangerLevelPlaced = (int) round(dangerLevel);

                    }
                }
            }, 1,1);
        }

        // Tâche qui affiche les particules
        if (showAlert) {
            particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    double diffLevel = abs(p.getLocation().y()-dangerLevel);
                    if (diffLevel < 10){
                        if (diffLevel < 5 && p.getLocation().y() > dangerLevel){
                            p.sendActionBar(Component.text(
                                    "!!ATTENTION!! La zone se rapproche vous êtes à " + String.format("%.2f",diffLevel) + " blocs de la zone !!"
                            ).color(TextColor.color(Color.RED.asRGB())));
                        }else if (p.getLocation().y() < dangerLevel){
                            p.sendActionBar(Component.text(
                                    "VOUS ETES DANS LA ZONE REMONTEZ VIIITE !!!"
                            ).color(TextColor.color(Color.RED.asRGB())));
                        }
                    }
                }
            }, 0, DISPLAY_PERIOD);
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
