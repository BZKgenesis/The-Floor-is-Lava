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
        column1.start();
        column2.start();
        column3.start();

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
            Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                    this::destroy, 60L);
        }
    }


    private void tpElements(){
        placeElement(gainDisplay, -0.75f, -0.15f);
        placeElement(betDisplay, -0.75f, 0.15f);
        placeElement(itemDisplay, 0f, 0.0f);
        float OFFSET_COLUMN = -0.0375f/2f;
        column1.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN+0.35f, 0f));
        column2.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN+0f, 0f));
        column3.getTextDisplays().forEach(textDisplay -> placeElement(textDisplay, OFFSET_COLUMN-0.35f, 0f));
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
