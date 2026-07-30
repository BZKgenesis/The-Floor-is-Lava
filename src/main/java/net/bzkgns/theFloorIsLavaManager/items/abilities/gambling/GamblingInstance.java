package net.bzkgns.theFloorIsLavaManager.items.abilities.gambling;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.*;

import java.lang.Math;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class GamblingInstance {
    private final static float DISPLAYS_DISTANCE = 0.85f;

    // Animation d'entrée/sortie : distance (en unités monde) parcourue
    // depuis/vers le bas, et durée en ticks de chaque phase.
    private static final float DROP_DISTANCE = 1.2f;
    private static final int INTRO_TICKS = 20;
    private static final int OUTRO_TICKS = 15;

    private final Player player;
    private final TextDisplay betDisplay;
    private final TextDisplay gainDisplay;
    private final ItemDisplay itemDisplay;
    private final GamblingColumn column1;
    private final GamblingColumn column2;
    private final GamblingColumn column3;
    private final int value;
    private final Integer gambleStopTaskId;
    private final Float gainMultiplier;
    private final Price gain;

    private final int gamblingTpTaskId;

    // État de l'animation globale d'entrée/sortie
    private int introTick = 0;
    private boolean introDone = false;
    private boolean exiting = false;
    private int exitTick = 0;

    public GamblingInstance(Player player, Price bet) {
        System.out.println("Creating GamblingInstance for player: " + player.getName());
        this.player = player;
        World world = player.getWorld();
        Transformation transformationBasiqueBlock = new Transformation(new Vector3f(0,-0.35f,-0.05f),
                new AxisAngle4f(),
                new Vector3f(1.5f,1,0.5f),
                new AxisAngle4f());

        this.betDisplay = world.spawn(player.getEyeLocation(), TextDisplay.class, textDisplay->{
            textDisplay.text(bet.displayResource(player).appendNewline().append(bet.displayMaterial(player)));
            textDisplay.setBillboard(Display.Billboard.FIXED);
            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(),
                    new Vector3f(0.25f, 0.25f, 0.25f),
                    new AxisAngle4f()
            ));
            textDisplay.setBackgroundColor(Color.fromARGB(0));
        });
        this.gainDisplay = world.spawn(player.getEyeLocation(), TextDisplay.class, textDisplay->{
            textDisplay.setBillboard(Display.Billboard.FIXED);
            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(),
                    new Vector3f(0.35f, 0.35f, 0.35f),
                    new AxisAngle4f()
            ));
            Price defaultPrice = new Price(0,0,0);
            textDisplay.text(defaultPrice.displayResource(player).appendNewline().append(defaultPrice.displayMaterial(player)));
            textDisplay.setBackgroundColor(Color.fromARGB(0));
        });

        this.itemDisplay = world.spawn(player.getEyeLocation(), ItemDisplay.class, itemDisplay->{
            ItemStack itemStack = new ItemStack(Material.EMERALD);
            itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("slot_machine").build());
            itemDisplay.setItemStack(itemStack);
            itemDisplay.setTransformation(transformationBasiqueBlock);
            itemDisplay.setBillboard(Display.Billboard.FIXED);
        });
        GamblingSymbol[] rolledSymbols = GamblingEngine.rollSymbols();
        if (rolledSymbols[0] == rolledSymbols[1] && rolledSymbols[1] == rolledSymbols[2]) {
            this.value = 3;
        } else if (rolledSymbols[0] == rolledSymbols[1] || rolledSymbols[1] == rolledSymbols[2] || rolledSymbols[0] == rolledSymbols[2]) {
            this.value = 2;
        }else{
            this.value = 1;
        }

        this.gainMultiplier = GamblingEngine.calculateGain(rolledSymbols);
        this.gain = bet.mul(gainMultiplier);
        System.out.println("Rolled symbols for player " + player.getName() + ": " + rolledSymbols[0].getName() + ", " + rolledSymbols[1].getName() + ", " + rolledSymbols[2].getName());
        this.column1 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[0].ordinal(),1);
        this.column2 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[1].ordinal(),2);
        this.column3 = new GamblingColumn(player, new Random().nextInt(GamblingSymbol.values().length), rolledSymbols[2].ordinal(),3);

        // On ne lance le spin des colonnes qu'une fois l'animation d'entrée
        // terminée, pour un effet "la machine arrive puis se met en marche".
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(), () -> {
            column1.start();
            column2.start();
            column3.start();
        }, INTRO_TICKS);

        gamblingTpTaskId = player.getServer().getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(), this::tpElements, 0L, 1L);
        gambleStopTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(),this::stopGambling, 1L,1L);
    }

    private void stopGambling() {
        if (column1.isStopped() && column2.isStopped() && column3.isStopped()) {
            Bukkit.getScheduler().cancelTask(gambleStopTaskId);
            switch (value){
                case 3 -> {
                    Messages.send(player, "gambling.jackpot", Placeholder.unparsed("multiplier", String.format("x%.2f",this.gainMultiplier)));
                    Messages.broadcast("gambling.broadcast_jackpot", Placeholder.component("player", player.displayName()), Placeholder.unparsed("multiplier", String.format("x%.2f",this.gainMultiplier)));
                    player.playSound(player, "minecraft:ui.toast.challenge_complete", 0.5f, 1f);
                }
                case 2 -> {
                    Messages.send(player, "gambling.two_symbols", Placeholder.unparsed("multiplier", String.format("x%.2f",this.gainMultiplier)));
                    player.playSound(player, "minecraft:block.amethyst_block.break", 0.5f, 1f);
                }
                case 1 -> {
                    Messages.send(player, "gambling.loose");
                    player.playSound(player, "minecraft:item.trident.hit_ground", 0.5f, 1f);
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                    this::showGain, 10L);
            // Au lieu de détruire directement, on déclenche l'animation de
            // sortie ; la vraie destruction aura lieu à la fin de celle-ci.
            Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                    this::beginExit, 60L);
        }
    }

    /**
     * Démarre la phase de sortie : yOffset repart de 0 vers -DROP_DISTANCE
     * (ease-in) pendant OUTRO_TICKS, puis la destruction réelle a lieu.
     */
    private void beginExit() {
        exiting = true;
        exitTick = 0;
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                this::destroy, OUTRO_TICKS);
    }

    /**
     * Calcule le décalage vertical global à appliquer à tous les éléments
     * pour l'animation d'entrée (montée depuis le bas) et de sortie
     * (redescente). Retourne 0 une fois l'entrée terminée et tant que la
     * sortie n'a pas commencé.
     */
    private float computeYOffset() {
        if (!introDone) {
            int t = Math.min(introTick, INTRO_TICKS);
            float p = (float) t / INTRO_TICKS;
            float eased = 1 - (float) Math.pow(1 - p, 3); // ease-out cubique
            introTick++;
            if (t >= INTRO_TICKS) {
                introDone = true;
                return 0f;
            }
            return -DROP_DISTANCE * (1 - eased);
        }

        if (exiting) {
            float p = Math.min(1f, (float) exitTick / OUTRO_TICKS);
            float eased = p * p * p; // ease-in cubique
            exitTick++;
            return -DROP_DISTANCE * eased;
        }

        return 0f;
    }

    private void tpElements(){
        float yOffset = computeYOffset();

        placeElement(gainDisplay, -0.75f, -0.15f + yOffset);
        placeElement(betDisplay, -0.75f, 0.15f + yOffset);
        placeElement(itemDisplay, 0f, 0.0f + yOffset);
        float OFFSET_COLUMN = -0.0375f/2f;
        column1.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN+0.35f, yOffset));
        column2.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN+0f, yOffset));
        column3.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN-0.35f, yOffset));
    }

    private void destroyElements(){
        gainDisplay.remove();
        betDisplay.remove();
        itemDisplay.remove();
        column1.destroy();
        column2.destroy();
        column3.destroy();
    }

    public void showGain(){
        this.gainDisplay.text(this.gain.displayResource(player).appendNewline().append(this.gain.displayMaterial(player)));
    }

    public void destroy() {
        player.getServer().getScheduler().cancelTask(gamblingTpTaskId);
        TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().addBalance(player.getUniqueId(), gain);
        destroyElements();
    }

    private void placeElement(Entity entity, float x, float y) {
        Vector viewDirection = this.player.getLocation().getDirection().normalize();
        Vector tangentViewDirection = this.player.getEyeLocation().getDirection().normalize().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector localUp = viewDirection.clone().rotateAroundAxis(tangentViewDirection, Math.PI/2f).normalize();

        viewDirection.multiply(DISPLAYS_DISTANCE);
        tangentViewDirection.multiply(x);
        localUp.multiply(y);
        Location newLocation = this.player.getEyeLocation().clone().add(viewDirection).add(localUp).add(tangentViewDirection);

        entity.teleport(newLocation.setDirection(newLocation.getDirection().multiply(-1)));
    }
}
