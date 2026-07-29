package net.bzkgns.theFloorIsLavaManager.items.abilities;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.*;

import java.lang.Math;
import java.util.Map;
import java.util.Random;

public class GamblingInstance {
    public enum GamblingSymbol {
        CERISE("cerise",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/poisonous_potato>")), //Component.text("\uD83C\uDF52", NamedTextColor.RED)
        CITRON("citron",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/apple>")), //Component.text("\uD83C\uDF4B",NamedTextColor.YELLOW)
        RAISIN("raisin",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/copper_ingot>")), //Component.text("\uD83C\uDF47",NamedTextColor.DARK_PURPLE)
        CLOCHE("cloche",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/iron_ingot>")), //Component.text("\uD83D\uDD14",NamedTextColor.GOLD)
        ETOILE("etoile",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/gold_ingot>")), //Component.text("⭐",NamedTextColor.YELLOW)
        DIAMANT("diamant",MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/diamond>")), //Component.text("\uD83D\uDC8E",NamedTextColor.AQUA)
        SEVEN("seven",Component.text("7",NamedTextColor.GREEN));
        private Component symbol;
        private String name;
        GamblingSymbol(String name,Component s) {
            this.symbol = s;
            this.name = name;
        }
        public Component getSymbol() {
            return symbol;
        }
        public String getName() {
            return name;
        }
    }
    private static final Map<GamblingSymbol, Double> SYMBOL_PROBABILITIES = Map.of(
            GamblingSymbol.CERISE, 0.35,
            GamblingSymbol.CITRON, 0.25,
            GamblingSymbol.RAISIN, 0.18,
            GamblingSymbol.CLOCHE, 0.1,
            GamblingSymbol.ETOILE, 0.07,
            GamblingSymbol.DIAMANT, 0.04,
            GamblingSymbol.SEVEN, 0.01
    );
    private static final int ANIMATION_DURATION = 10;
    private final Player player;
    private final BlockDisplay blockDisplay1;
    private final BlockDisplay blockDisplay2;
    private final GamblingColumn column1;
    private final GamblingColumn column2;
    private final GamblingColumn column3;
    private Integer gambleStopTaskId;
    private final Price gain;

    private int gamblingTpTaskId;
    public GamblingInstance(Player player, Price bet) {
        System.out.println("Creating GamblingInstance for player: " + player.getName());
        this.player = player;
        World world = player.getWorld();
        Vector3f blockScale = new Vector3f(1f, 0.5f, 0.125f);
        Transformation transformationBasiqueBlock = new Transformation(new Vector3f(blockScale).mul(-0.5f),
                new AxisAngle4f(),
                blockScale,
                new AxisAngle4f());

        this.blockDisplay1 = world.spawn(player.getEyeLocation(), BlockDisplay.class, blockDisplay->{
            blockDisplay.setBlock(Material.RED_CONCRETE.createBlockData());
            blockDisplay.setTransformation(transformationBasiqueBlock);
            blockDisplay.setBillboard(Display.Billboard.CENTER);
        });
        this.blockDisplay2 = world.spawn(player.getEyeLocation(), BlockDisplay.class, blockDisplay->{
            blockDisplay.setBlock(Material.RED_CONCRETE.createBlockData());
            blockDisplay.setTransformation(transformationBasiqueBlock);
            blockDisplay.setBillboard(Display.Billboard.CENTER);
        });
        GamblingSymbol[] rolledSymbols = rollSymbols();
        this.gain = calculateGain(rolledSymbols, bet);
        System.out.println("Rolled symbols for player " + player.getName() + ": " + rolledSymbols[0].getName() + ", " + rolledSymbols[1].getName() + ", " + rolledSymbols[2].getName());
        this.column1 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[0].ordinal(),1);
        this.column2 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[1].ordinal(),2);
        this.column3 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[2].ordinal(),3);
        column1.start();
        column2.start();
        column3.start();

        gamblingTpTaskId = player.getServer().getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(), this::tpElements, 0L, 1L);
        gambleStopTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(),this::stopGambling, 1L,1L);
    }

    private void stopGambling() {
        if (column1.isStopped() && column2.isStopped() && column3.isStopped()) {
            Bukkit.getScheduler().cancelTask(gambleStopTaskId);
            Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                    this::destroy, 60L);
        }
    }

    private void tpElements(){
        Vector viewDirection = this.player.getLocation().getDirection().normalize();
        Vector tangentViewDirection = this.player.getEyeLocation().getDirection().normalize().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector localUp = viewDirection.clone().rotateAroundAxis(tangentViewDirection, Math.PI/2f).normalize();
        viewDirection.multiply(0.75);
        tangentViewDirection.multiply(0.35);
        localUp.multiply(0.5);
        blockDisplay1.teleport(this.player.getEyeLocation().clone().add(viewDirection).add(localUp));
        blockDisplay2.teleport(this.player.getEyeLocation().clone().add(viewDirection).subtract(localUp));
        column1.getTextDisplays().forEach(textDisplay -> textDisplay.teleport(this.player.getEyeLocation().clone().add(viewDirection).add(tangentViewDirection)));
        column2.getTextDisplays().forEach(textDisplay -> textDisplay.teleport(this.player.getEyeLocation().clone().add(viewDirection)));
        column3.getTextDisplays().forEach(textDisplay -> textDisplay.teleport(this.player.getEyeLocation().clone().add(viewDirection).subtract(tangentViewDirection)));
    }

    private void destroyElements(){
        blockDisplay1.remove();
        blockDisplay2.remove();
        column1.destroy();
        column2.destroy();
        column3.destroy();
    }

    private GamblingSymbol[] rollSymbols() {
        GamblingSymbol[] symbols = new GamblingSymbol[3];
        for (int i = 0; i < 3; i++) {
            double randomValue = Math.random();
            double cumulativeProbability = 0.0;
            for (Map.Entry<GamblingSymbol, Double> entry : SYMBOL_PROBABILITIES.entrySet()) {
                cumulativeProbability += entry.getValue();
                if (randomValue <= cumulativeProbability) {
                    symbols[i] = entry.getKey();
                    break;
                }
            }
        }
        return symbols;
    }

    public void destroy() {
        player.getServer().getScheduler().cancelTask(gamblingTpTaskId);
        TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().addBalance(player.getUniqueId(), gain);
        destroyElements();
    }

    public static Price calculateGain(GamblingSymbol[] symbols, Price bet) {

        if (symbols == null || symbols.length != 3)
            return new Price(0,0,0);


        // Trois symboles identiques
        if (symbols[0] == symbols[1] && symbols[1] == symbols[2]) {

            float multiplier = switch (symbols[0]) {

                case SEVEN -> 100.0f;
                case DIAMANT -> 25.0f;
                case ETOILE -> 10.0f;
                case CLOCHE -> 4.0f;
                case RAISIN -> 2.0f;
                case CITRON -> 1.2f;
                case CERISE -> 0.8f;

            };

            return bet.mul(multiplier);
        }


        // Deux symboles identiques
        if (symbols[0] == symbols[1]
                || symbols[1] == symbols[2]
                || symbols[0] == symbols[2]) {

            return bet.mul( 0.2f);
        }


        // Rien gagné
        return new Price(0,0,0);
    }
}
