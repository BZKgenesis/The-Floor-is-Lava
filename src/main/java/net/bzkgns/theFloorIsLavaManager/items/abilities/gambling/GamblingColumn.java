package net.bzkgns.theFloorIsLavaManager.items.abilities.gambling;

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
    private static final int REVEAL_INDEX = 2;

    private static final float SYMBOL_HEIGHT = 0.25f;
    private static final float BOTTOM = -0.625f;

    // Rotation appliquée par case parcourue (45°). Augmente pour un effet
    // de "roue" plus prononcé, diminue pour un tilt plus subtil.
    private static final float ROTATION_PER_STEP = (float) (Math.PI / 4f);

    // Décalage entre l'origine locale du TextDisplay (généralement en bas
    // du texte) et son centre visuel. C'est CETTE valeur qui pilote le
    // pivot : si le texte semble encore pivoter par le bas, augmente-la ;
    // si le pivot dépasse le centre, réduis-la. À ajuster visuellement,
    // 0.15f est un bon point de départ pour une échelle de texte par défaut.
    private static final float PIVOT_OFFSET = 0.15f;
    private static final float PIVOT_OFFSET_Z = 0.35f;

    private static final int START_STEP_TICKS = 2;
    private static final int MAX_STEP_TICKS = 12;
    private static final float STEP_TICKS_INCREMENT = 0.25f;

    // On garde la position "logique" de chaque display à part de la
    // Transformation elle-même : la Transformation contient une position
    // Y déjà compensée par le pivot, donc plus utilisable comme source de
    // vérité pour le prochain calcul (sinon on accumule l'erreur / la
    // rotation devient incohérente, cf. bug initial).
    private final List<TextDisplay> textDisplays = new ArrayList<>();
    private final List<Float> logicalYs = new ArrayList<>();

    private final Player player;

    private int currentIndex;
    private final int resultIndex;
    private final int minLoops;

    private float stepTicksFloat = START_STEP_TICKS;
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
                        td.setBillboard(Display.Billboard.FIXED);
                        td.setBackgroundColor(Color.fromARGB(0));
                        td.text(getSymbol(symbolIndex));
                        td.setInterpolationDelay(0);
                        td.setTransformation(createTransformation(y));
                    }
            );
            textDisplays.add(display);
            logicalYs.add(y);
        }
    }

    public void start() {
        scheduleNextStep(0L);
    }

    private void scheduleNextStep(@SuppressWarnings("SameParameterValue") long delay) {
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLavaManager.getInstance(),
                this::performStep,
                delay
        );
    }

    private void performStep() {
        for (int i = 0; i < textDisplays.size(); i++) {
            TextDisplay display = textDisplays.get(i);
            display.setVisibleByDefault(true);

            float y = logicalYs.get(i) - SYMBOL_HEIGHT;
            logicalYs.set(i, y);

            display.setInterpolationDelay(0);
            display.setInterpolationDuration(stepTicks);
            display.setTransformation(createTransformation(y));
        }
        player.playSound(player, "entity.chicken_picky.step", 1f, 0.5f);

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                TheFloorIsLavaManager.getInstance(),
                this::finishStep,
                stepTicks
        );
    }

    private void finishStep() {
        TextDisplay recycled = textDisplays.removeFirst();
        logicalYs.removeFirst();
        recycled.setVisibleByDefault(false);

        currentIndex = modulo(currentIndex + 1);
        stepsDone++;

        int newTopSymbolIndex = modulo(currentIndex + NB_TEXTS - 1);
        float topY = slotY(NB_TEXTS - 1);

        recycled.text(getSymbol(newTopSymbolIndex));
        recycled.setInterpolationDelay(0);
        recycled.setInterpolationDuration(0); // snap instantané, hors écran
        recycled.setTransformation(createTransformation(topY));

        textDisplays.add(recycled);
        logicalYs.add(topY);

        int revealSymbolIndex = modulo(currentIndex + REVEAL_INDEX);
        boolean onResult = revealSymbolIndex == resultIndex;

        int symbolCount = GamblingSymbol.values().length;
        int loopsCompleted = stepsDone / symbolCount;

        if (onResult && loopsCompleted >= minLoops) {
            stop();
            return;
        }

        if (stepTicks < MAX_STEP_TICKS) {
            stepTicksFloat += STEP_TICKS_INCREMENT;
            stepTicks = Math.round(stepTicksFloat);
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

//        for (int i = 0; i < textDisplays.size(); i++) {
//            TextDisplay display = textDisplays.get(i);
//            display.setInterpolationDelay(0);
//            display.setInterpolationDuration(5);
//            display.setTransformation(createTransformation(logicalYs.get(i))); // <- rotation réellement appliquée
//        }
    }

    private float slotY(int slotIndex) {
        return BOTTOM + slotIndex * SYMBOL_HEIGHT;
    }

    /**
     * Angle purement déterministe en fonction de la position Y logique.
     * Fini de relire l'état de la Transformation précédente : ça évite
     * toute dérive/incohérence liée à la normalisation interne du quaternion.
     */
    private float rotationForY(float y) {
        float raw = (float) ((-((y - BOTTOM) / SYMBOL_HEIGHT) * ROTATION_PER_STEP) + Math.PI/2.0); // signe inversé
        float twoPi = (float) (2 * Math.PI);
        raw = raw % twoPi;
        return raw < 0 ? raw + twoPi : raw;
    }

    private Transformation createTransformation(float y) {
        float angle = rotationForY(y);
        return buildTransformation(y, angle);
    }

    private Transformation buildTransformation(float y, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        // Compensation du pivot : sans ça, la rotation se ferait autour du
        // bas du texte au lieu de son centre visuel.
        float compensatedY = y + PIVOT_OFFSET * (1 - cos);
        float compensatedZ = -PIVOT_OFFSET * sin + PIVOT_OFFSET_Z * cos - 0.25f; // <- c'est ce terme qui fait bouger Z

        return new Transformation(
                new Vector3f(0, compensatedY, compensatedZ),
                new AxisAngle4f(angle, 1, 0, 0),
                new Vector3f(1, 1, 1),
                new AxisAngle4f()
        );
    }

    private int modulo(int value) {
        int size = GamblingSymbol.values().length;
        return ((value % size) + size) % size;
    }

    private Component getSymbol(int index) {
        return GamblingSymbol.values()[index].getSymbol();
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
