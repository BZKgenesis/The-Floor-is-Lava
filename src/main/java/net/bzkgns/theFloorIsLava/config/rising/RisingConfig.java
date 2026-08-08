package net.bzkgns.theFloorIsLava.config.rising;

import net.bzkgns.theFloorIsLava.config.ConfigKey;
import net.bzkgns.theFloorIsLava.config.ConfigSection;

import java.util.List;

/**
 * Regroupe tous les paramètres réglables de la partie (vitesse de montée de la lave,
 * dégâts, taille de bordure, etc.).
 * <p>
 * Cet objet est volontairement séparé de DangerManager : il ne connaît ni les tâches
 * Bukkit, ni l'état de la partie (LOBBY/PREPARING/RISING). Il peut donc être lu et
 * modifié librement (commande, GUI, plus tard une API REST si besoin) sans jamais
 * risquer de démarrer/arrêter une tâche par effet de bord.
 */
public class RisingConfig implements ConfigSection<RisingConfig> {

    private int startLevel = -64;
    private int endLevel = 250;
    private int surfaceLevel = 64;
    private int totalTimeBelowSurface = 18000;
    private int totalTimeAboveSurface = 30000;
    private double damage = 1.0;
    private int damageEvery = 40;
    private boolean placeLava = true;
    private boolean showAlert = true;
    private int lavaMargin = 30;
    private int increaseSize = 2;

    private static final List<ConfigKey<RisingConfig, ?>> KEYS = List.of(
            RisingConfigKeys.START_LEVEL,
            RisingConfigKeys.END_LEVEL,
            RisingConfigKeys.SURFACE_LEVEL,
            RisingConfigKeys.TOTAL_TIME_BELOW_SURFACE,
            RisingConfigKeys.TOTAL_TIME_ABOVE_SURFACE,
            RisingConfigKeys.DAMAGE,
            RisingConfigKeys.DAMAGE_EVERY,
            RisingConfigKeys.PLACE_LAVA,
            RisingConfigKeys.SHOW_ALERT,
            RisingConfigKeys.LAVA_MARGIN,
            RisingConfigKeys.INCREASE_SIZE
    );

    // Valeurs dérivées : jamais réglées directement, toujours recalculées.
    private double increaseAmountBelow;
    private double increaseAmountAbove;


    /** À appeler après toute modification de startLevel/surfaceLevel/endLevel/totalTimeXxx. */
    public void recomputeIncreaseAmounts() {
        increaseAmountBelow = totalTimeBelowSurface == 0 ? 0 : (double) (surfaceLevel - startLevel) / totalTimeBelowSurface;
        increaseAmountAbove = totalTimeAboveSurface == 0 ? 0 : (double) (endLevel - surfaceLevel) / totalTimeAboveSurface;
    }

    public double initialIncreaseAmount() {
        return startLevel < surfaceLevel ? increaseAmountBelow : increaseAmountAbove;
    }

    public double increaseAmountFor(double currentDangerLevel) {
        return currentDangerLevel < surfaceLevel ? increaseAmountBelow : increaseAmountAbove;
    }

    // --- Getters / setters ---

    public int getStartLevel() { return startLevel; }
    public void setStartLevel(int v) { this.startLevel = v; recomputeIncreaseAmounts(); }

    public int getEndLevel() { return endLevel; }
    public void setEndLevel(int v) { this.endLevel = v; recomputeIncreaseAmounts(); }

    public int getSurfaceLevel() { return surfaceLevel; }
    public void setSurfaceLevel(int v) { this.surfaceLevel = v; recomputeIncreaseAmounts(); }

    public int getTotalTimeBelowSurface() { return totalTimeBelowSurface; }
    public void setTotalTimeBelowSurface(int v) { this.totalTimeBelowSurface = v; recomputeIncreaseAmounts(); }

    public int getTotalTimeAboveSurface() { return totalTimeAboveSurface; }
    public void setTotalTimeAboveSurface(int v) { this.totalTimeAboveSurface = v; recomputeIncreaseAmounts(); }

    public double getDamage() { return damage; }
    public void setDamage(double v) { this.damage = v; }

    public int getDamageEvery() { return damageEvery; }
    public void setDamageEvery(int v) { this.damageEvery = v; }

    public boolean isPlaceLava() { return placeLava; }
    public void setPlaceLava(boolean v) { this.placeLava = v; }

    public boolean isShowAlert() { return showAlert; }
    public void setShowAlert(boolean v) { this.showAlert = v; }

    public int getLavaMargin() { return lavaMargin; }
    public void setLavaMargin(int v) { this.lavaMargin = v; }

    public int getIncreaseSize() { return increaseSize; }
    public void setIncreaseSize(int v) { this.increaseSize = v; }

    @Override
    public String getName() {
        return "danger";
    }

    @Override
    public List<ConfigKey<RisingConfig,?>> getKeys() {
        return List.copyOf(KEYS);
    }

    @SuppressWarnings("unused")
    public Object getValueForKey(ConfigKey<RisingConfig, ?> key) {
        return key.get(this);
    }
}
