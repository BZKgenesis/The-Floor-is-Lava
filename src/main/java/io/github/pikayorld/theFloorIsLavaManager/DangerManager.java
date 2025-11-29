package io.github.pikayorld.theFloorIsLavaManager;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.Player;


import java.time.Duration;

import static java.lang.Math.*;

public class DangerManager {

    private final TheFloorIsLavaManager plugin;



    private double dangerLevel;
    private int maxdangerLevel;
    private int mindangerLevel;
    private int increaseMode;
    private int increaseEvery;
    private int totalTime;
    private final double damage;
    private final int damageEvery;
    private final boolean showParticles;

    private int increaseTask = -1;
    private int damageTask = -1;
    private int particleTask = -1;

    private boolean isPaused = false;


    private final int DISPLAY_PERIOD = 5;

    public DangerManager(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;

        mindangerLevel = plugin.getConfig().getInt("danger.start-level");
        maxdangerLevel = plugin.getConfig().getInt("danger.end-level");
        increaseMode = plugin.getConfig().getInt("danger.increase-mode");
        increaseMode = min(1,max(0,increaseMode));
        if (increaseMode == 0){
            increaseEvery = plugin.getConfig().getInt("danger.increase-every");
            totalTime = (maxdangerLevel - mindangerLevel)*increaseEvery;
        }else{
            totalTime = plugin.getConfig().getInt("danger.total-time");
            increaseEvery = totalTime/(maxdangerLevel - mindangerLevel);
        }
        damage = plugin.getConfig().getDouble("danger.damage");
        damageEvery = plugin.getConfig().getInt("danger.damage-every");
        showParticles = plugin.getConfig().getBoolean("danger.particles");

        dangerLevel = mindangerLevel;
    }

    private void setIncreaseTask(){
        if (dangerLevel < maxdangerLevel){
            dangerLevel=dangerLevel+ (double) 1 /increaseEvery;
        }else{
            Bukkit.getScheduler().cancelTask(increaseTask);
        }
    }

    public void start() {
        isPaused = false;
        Bukkit.getScheduler().cancelTask(damageTask);
        if (particleTask != -1) Bukkit.getScheduler().cancelTask(particleTask);

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

        // Tâche qui affiche les particules
        if (showParticles) {
            particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    double diffLevel = abs(p.getLocation().y()-dangerLevel);
                    if (diffLevel < 10){
                        drawParticlesForPlayer(p);

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

    public void stop() {
        isPaused = false;
        Bukkit.getScheduler().cancelTask(increaseTask);
        Bukkit.getScheduler().cancelTask(damageTask);
        if (particleTask != -1) Bukkit.getScheduler().cancelTask(particleTask);
    }

    public void pause() {
        isPaused = true;
        Bukkit.getScheduler().cancelTask(increaseTask);
    }

    private void drawParticlesForPlayer(Player p) {
        World world = p.getWorld();
        double y = dangerLevel + 0.1;

        // On trace un "mur" de particules autour du joueur (pas besoin d'afficher tout le chunk)
        int radius = 10;
        int count = 3;

        for (int x = -radius; x <= radius; x += 1) {
            for (int z = -radius; z <= radius; z += 1) {
                Location loc = new Location(world,
                        p.getLocation().getBlockX() + x,
                        y,
                        p.getLocation().getBlockZ() + z);
                Particle.DUST.builder()
                    .location(loc)
                    .offset(1,0,0)
                    .count(count)
                    .receivers(p)
                    .color(Color.RED)
                    .spawn();

                Particle.DUST.builder()
                    .location(loc)
                    .offset(0,0,1)
                    .count(count)
                    .receivers(p)
                    .color(Color.RED)
                    .spawn();
            }
        }
    }

    public double getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public int getSpeed() {
        return increaseEvery;
    }

    public void setSpeed(int speed) {
        this.increaseEvery = speed;
        Bukkit.getScheduler().cancelTask(increaseTask);
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setIncreaseTask, 0, 1);
    }
}
