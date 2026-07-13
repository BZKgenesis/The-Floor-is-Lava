package net.bzkgns.theFloorIsLavaManager;

import net.bzkgns.theFloorIsLavaManager.shop.ShopGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
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
    private int surfaceLevel;
    private double increaseAmount;
    private double increaseAmountBelow;
    private double increaseAmountAbove;
    private int totalTimeBelow;
    private int totalTimeAbove;
    private final double damage;
    private final int damageEvery;
    private final boolean placeLava;
    private final boolean showAlert;
    private final int lavaMargin;
    private final int increaseSize;
    private final int lavaRisingDelay;
    private final int borderSizePreRise;
    private final int borderSizeDuringRise;
    private final int borderResizeTime;
    private final boolean disablePvpDuringPreparation;
    private final boolean keepinventoryDuringPreparation;

    public double fallDamageReduction = 1.0;

    private int increaseTask = -1;
    private int damageTask = -1;
    private int particleTask = -1;
    private int placeLavaTask = -1;
    private int phase2Task = -1;

    private boolean isPaused = false;

    private boolean noRespawn = false;

    private boolean hasStarted = false;

    private List<Player> playerInGame;




    private final int DISPLAY_PERIOD = 5;

    public DangerManager(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;

        mindangerLevel = plugin.getConfig().getInt("danger.start-level");
        maxdangerLevel = plugin.getConfig().getInt("danger.end-level");
        surfaceLevel = plugin.getConfig().getInt("danger.surface-level");
        totalTimeBelow = plugin.getConfig().getInt("danger.total-time-below-surface");
        totalTimeAbove = plugin.getConfig().getInt("danger.total-time-above-surface");
        increaseAmountBelow = (double) (surfaceLevel - mindangerLevel) /totalTimeBelow;
        increaseAmountAbove = (double) (maxdangerLevel - surfaceLevel) /totalTimeAbove;
        damage = plugin.getConfig().getDouble("danger.damage");
        damageEvery = plugin.getConfig().getInt("danger.damage-every");
        placeLava = plugin.getConfig().getBoolean("danger.place-lava");
        showAlert = plugin.getConfig().getBoolean("danger.show-alert");
        lavaMargin = plugin.getConfig().getInt("danger.lava-margin");
        increaseSize = plugin.getConfig().getInt("danger.increase-size");
        lavaRisingDelay = plugin.getConfig().getInt("danger.lava-rising-delay");
        borderSizePreRise = plugin.getConfig().getInt("danger.border-size-prerise");
        borderSizeDuringRise = plugin.getConfig().getInt("danger.border-size-during-rise");
        borderResizeTime = plugin.getConfig().getInt("danger.border-resize-time");
        disablePvpDuringPreparation = plugin.getConfig().getBoolean("danger.disable-pvp-during-preparation");
        keepinventoryDuringPreparation = plugin.getConfig().getBoolean("danger.keepinventory-during-preparation");
        fallDamageReduction = plugin.getConfig().getDouble("danger.falldamage-reduction");

        playerInGame = new ArrayList<>();

        if (mindangerLevel < surfaceLevel){
            increaseAmount = increaseAmountBelow;
        }else{
            increaseAmount = increaseAmountAbove;
        }

        dangerLevel = mindangerLevel;

        Old_dangerLevelPlaced = mindangerLevel-1;
    }

    private void setIncreaseTask(){
        if (dangerLevel < maxdangerLevel){
            if(dangerLevel < surfaceLevel){
                increaseAmount = increaseAmountBelow;
            }else{
                increaseAmount = increaseAmountAbove;
            }
            dangerLevel=dangerLevel+ (double)increaseAmount;
        }else{
            Bukkit.getScheduler().cancelTask(increaseTask);
        }
    }

    public void start() {
        isPaused = false;
        hasStarted = true;
        World world = Bukkit.getServer().getWorlds().getFirst();
        world.getWorldBorder().setSize(borderSizePreRise);
        world.getWorldBorder().setCenter(0,0);
        Bukkit.getScheduler().cancelTask(damageTask);
        if (keepinventoryDuringPreparation)
            world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.ADVANCE_TIME,true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER,0);
        if (particleTask != -1) Bukkit.getScheduler().cancelTask(particleTask);
        if (disablePvpDuringPreparation){
            TheFloorIsLavaManager.pvp = false;
        }
        playerInGame.addAll(plugin.getServer().getOnlinePlayers());
        setNoRespawn(false);

        TheFloorIsLavaManager.sendMessage("Le jeu commence !");
        if (keepinventoryDuringPreparation)
            TheFloorIsLavaManager.sendMessage("Les inventaires sont sauvegardés (keepInventory)");
        if (disablePvpDuringPreparation)
            TheFloorIsLavaManager.sendMessage("Le PvP est désactivé");

        TheFloorIsLavaManager.sendMessage("La lave va commencer à monter dans " + lavaRisingDelay/(20*60) + " minutes");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 50 "+borderSizePreRise/2+" under 200 true @a[gamemode=!creative]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a[gamemode=!creative] at @s run spawnpoint");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setExhaustion(0);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.give(ShopGUI.giveShopItem());
        }

        if (lavaRisingDelay > 6000) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 5 minutes..."),lavaRisingDelay-6000 );
        if (lavaRisingDelay > 3600) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 3 minutes..."),lavaRisingDelay-3600 );
        if (lavaRisingDelay > 1200) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 1 minutes..."),lavaRisingDelay-1200 );
        if (lavaRisingDelay > 600) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 30 secondes..."),lavaRisingDelay-600 );
        if (lavaRisingDelay > 200) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 10 secondes..."),lavaRisingDelay-200 );
        if (lavaRisingDelay > 100) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 5 secondes..."),lavaRisingDelay-100 );
        if (lavaRisingDelay > 80) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 4 secondes..."),lavaRisingDelay-80 );
        if (lavaRisingDelay > 60) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 3 secondes..."),lavaRisingDelay-60 );
        if (lavaRisingDelay > 40) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 2 secondes..."),lavaRisingDelay-40 );
        if (lavaRisingDelay > 20) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va commencer à monter dans 1 secondes..."),lavaRisingDelay-20 );


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
            world.setGameRule(GameRules.KEEP_INVENTORY, false);
        // Tâche qui augmente le niveau
        increaseTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setIncreaseTask, 0, 1);
        TheFloorIsLavaManager.pvp = true;
        setNoRespawn(true);

        // Tâche qui inflige les dégâts
        damageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().getY() < dangerLevel) {
                    p.damage(damage);
                }
            }
        }, 20, damageEvery);
        plugin.getServer().getWorlds().getFirst().getWorldBorder().changeSize(borderSizeDuringRise,borderResizeTime);

        TheFloorIsLavaManager.sendMessage("!!ATTENTION!! La lave commence à monter !");
        if (keepinventoryDuringPreparation)
            TheFloorIsLavaManager.sendMessage("Les inventaires ne sont plus sauvegardés");
        if (disablePvpDuringPreparation)
            TheFloorIsLavaManager.sendMessage("Le PvP est activé");
        TheFloorIsLavaManager.sendMessage("Le respawn est désactivé");
        TheFloorIsLavaManager.sendMessage("La zone se rétrécit");

        if (placeLava){
            placeLavaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                public void run(){
                    if (Old_dangerLevelPlaced+increaseSize < round(dangerLevel)){
                        double diff = increaseSize/increaseAmount;
                        if (diff > 100){
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va monter dans 3 secondes..."),round(diff)-60 );
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va monter dans 2 secondes..."),round(diff)-40 );
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave va monter dans 1 secondes..."),round(diff)-20 );
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TheFloorIsLavaManager.sendActionBar("La lave monte !!"),round(diff) );
                        }
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

    public boolean isPlayerInGame(Player player){
        return playerInGame.contains(player);
    }

    public void setNoRespawn(boolean v){
        noRespawn = v;
    }
    public boolean getNoRespawn(){
        return noRespawn;
    }
    public void setHasStarted(boolean v){
        hasStarted = v;
    }
    public boolean getHasStarted(){
        return hasStarted;
    }
}
