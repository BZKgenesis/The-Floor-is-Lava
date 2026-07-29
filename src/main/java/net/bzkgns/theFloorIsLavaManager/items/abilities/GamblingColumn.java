package net.bzkgns.theFloorIsLavaManager.items.abilities;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GamblingColumn {

    private static final int NB_TEXTS = 6;

    // Index du slot (0 = bas ... NB_TEXTS-1 = haut) qui correspond à la ligne
    // de résultat visible par le joueur (là où se trouve le "repère"/la vitre).
    // Si visuellement le symbole gagnant n'est pas sur la bonne ligne dans le
    // monde, c'est CETTE valeur qu'il faut ajuster (0..NB_TEXTS-1).
    private static final int REVEAL_INDEX = 2;

    private static final float SYMBOL_HEIGHT = 0.25f;

    private static final float BOTTOM = -0.625f;

    // Durée (en ticks) pour parcourir UNE case. Plus la valeur est petite,
    // plus ça va vite. On part rapide et on ralentit case par case.
    private static final int START_STEP_TICKS = 2;
    private static final int MAX_STEP_TICKS = 10;
    private static final int STEP_TICKS_INCREMENT = 1;

    private final List<TextDisplay> textDisplays = new ArrayList<>();
    private final Player player;

    // Symbole affiché dans le slot 0 (le plus bas)
    private int currentIndex;
    private final int resultIndex;

    // Nombre minimum de tours complets (sur tous les symboles) avant de
    // pouvoir s'arrêter sur le résultat. Sert à décaler l'arrêt entre les
    // colonnes (ex: 1, 2, 3) comme sur une vraie machine à sous.
    private final int minLoops;

    private int stepTicks = START_STEP_TICKS;
    private int stepsDone = 0;

    private int taskId = -1;
    private boolean stopped = false;

    public GamblingColumn(Player player, int initialIndex, int resultIndex, int minLoops) {

        this.player = player;
        this.currentIndex = initialIndex;
        this.resultIndex = resultIndex;
        this.minLoops = Math.max(1, minLoops);

        for (int i = 0; i < NB_TEXTS; i++) {
            int symbolIndex = modulo(currentIndex + i);
            float y = slotY(i);
            TextDisplay display = player.getWorld().spawn(
                    player.getEyeLocation(),
                    TextDisplay.class,
                    td -> {
                        td.setBillboard(Display.Billboard.CENTER);
                        td.setBackgroundColor(Color.fromARGB(0));
                        td.text(getSymbol(symbolIndex));
                        td.setInterpolationDelay(0);
                        td.setTransformation(createTransformation(y));
                    }
            );
            textDisplays.add(display);
        }
    }

    public void start() {
        scheduleNextStep(0L);
    }

    private void scheduleNextStep(long delay) {
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLavaManager.getInstance(),
                this::performStep,
                delay
        );
    }

    /**
     * Fait glisser chaque texte d'une case (SYMBOL_HEIGHT) vers le bas,
     * de façon fluide, sur "stepTicks" ticks.
     */
    private void performStep() {
        for (TextDisplay display : textDisplays) {
            display.setVisibleByDefault(true);
            Transformation current = display.getTransformation();
            float y = current.getTranslation().y() - SYMBOL_HEIGHT;

            display.setInterpolationDelay(0);
            display.setInterpolationDuration(stepTicks);
            display.setTransformation(createTransformation(y));
        }
        player.playSound(player, "entity.chicken_picky.step", 1f, 0.5f);

        // Une fois le glissement terminé, on recycle le symbole du bas
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLavaManager.getInstance(),
                this::finishStep,
                stepTicks
        );
    }

    /**
     * Le texte qui était en bas est maintenant hors écran (il a parcouru
     * exactement une case de plus que sa position visible). On le replace
     * tout en haut avec le symbole suivant, SANS interpolation puisqu'il
     * est invisible à cet instant (évite l'effet de "flash"/saccade).
     */
    private void finishStep() {
        TextDisplay recycled = textDisplays.removeFirst();
        recycled.setVisibleByDefault(false);

        currentIndex = modulo(currentIndex + 1);
        stepsDone++;

        int newTopSymbolIndex = modulo(currentIndex + NB_TEXTS - 1);
        recycled.text(getSymbol(newTopSymbolIndex));
        recycled.setInterpolationDelay(0);
        recycled.setInterpolationDuration(0); // snap instantané, hors écran
        recycled.setTransformation(createTransformation(slotY(NB_TEXTS - 1)));

        textDisplays.add(recycled);

        int revealSymbolIndex = modulo(currentIndex + REVEAL_INDEX);
        boolean onResult = revealSymbolIndex == resultIndex;

        int symbolCount = GamblingInstance.GamblingSymbol.values().length;
        int loopsCompleted = stepsDone / symbolCount;

        if (onResult && loopsCompleted >= minLoops) {
            stop();
            return;
        }

        if (stepTicks < MAX_STEP_TICKS) {
            stepTicks += STEP_TICKS_INCREMENT;
        }

        scheduleNextStep(0L);
    }

    private void stop() {
        stopped = true;
        float pitch = switch (minLoops) {
            case 1 -> 0.8f;
            case 2 -> 0.95f;
            case 3 -> 1.2f;
            default -> 1.0f;
        };
        player.playSound(player, "minecraft:block.note_block.pling", 1f, pitch);

        // Tout est déjà exactement aligné grâce au système par pas,
        // on force juste une interpolation propre / instantanée.
        for (TextDisplay display : textDisplays) {
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(0);
        }
    }

    private float slotY(int slotIndex) {
        return BOTTOM + slotIndex * SYMBOL_HEIGHT;
    }

    private Transformation createTransformation(float y) {
        return new Transformation(
                new Vector3f(0, y, 0),
                new AxisAngle4f(),
                new Vector3f(1, 1, 1),
                new AxisAngle4f()
        );
    }

    private int modulo(int value) {
        int size = GamblingInstance.GamblingSymbol.values().length;
        return ((value % size) + size) % size;
    }

    private Component getSymbol(int index) {
        return GamblingInstance.GamblingSymbol
                .values()[index]
                .getSymbol();
    }

    public List<TextDisplay> getTextDisplays() {
        return textDisplays;
    }

    public void destroy() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        textDisplays.forEach(Entity::remove);
    }

    public boolean isStopped() {
        return stopped;
    }
}

