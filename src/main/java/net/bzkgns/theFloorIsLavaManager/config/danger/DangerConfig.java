package net.bzkgns.theFloorIsLavaManager.config.danger;

import net.bzkgns.theFloorIsLavaManager.config.*;

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
public class DangerConfig implements ConfigSection<DangerConfig> {

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

    private static final List<ConfigKey<DangerConfig, ?>> KEYS = List.of(

            new ConfigKey<>(
                    "start-level",
                    "Niveau Y de départ de la lave",
                    DangerConfig::getStartLevel,
                    DangerConfig::setStartLevel,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "end-level",
                    "Niveau Y final de la lave",
                    DangerConfig::getEndLevel,
                    DangerConfig::setEndLevel,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "surface-level",
                    "Niveau Y de la surface (change la vitesse de montée)",
                    DangerConfig::getSurfaceLevel,
                    DangerConfig::setSurfaceLevel,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "total-time-below-surface",
                    "Durée (ticks) pour monter jusqu'à la surface",
                    DangerConfig::getTotalTimeBelowSurface,
                    DangerConfig::setTotalTimeBelowSurface,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "total-time-above-surface",
                    "Durée (ticks) pour monter de la surface au sommet",
                    DangerConfig::getTotalTimeAboveSurface,
                    DangerConfig::setTotalTimeAboveSurface,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "damage",
                    "Dégâts de la zone",
                    DangerConfig::getDamage,
                    DangerConfig::setDamage,
                    Double::parseDouble
            ),

            new ConfigKey<>(
                    "damage-every",
                    "Fréquence (ticks) des dégâts",
                    DangerConfig::getDamageEvery,
                    DangerConfig::setDamageEvery,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "place-lava",
                    "Poser réellement de la lave (sinon dégâts seuls)",
                    DangerConfig::isPlaceLava,
                    DangerConfig::setPlaceLava,
                    Boolean::parseBoolean
            ),

            new ConfigKey<>(
                    "show-alert",
                    "Afficher les alertes de proximité de la zone",
                    DangerConfig::isShowAlert,
                    DangerConfig::setShowAlert,
                    Boolean::parseBoolean
            ),

            new ConfigKey<>(
                    "lava-margin",
                    "Marge (blocs) de lave hors bordure",
                    DangerConfig::getLavaMargin,
                    DangerConfig::setLavaMargin,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "increase-size",
                    "Hauteur (blocs) posée par palier",
                    DangerConfig::getIncreaseSize,
                    DangerConfig::setIncreaseSize,
                    Integer::parseInt
            )

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
    public List<ConfigKey<DangerConfig,?>> getKeys() {
        return List.copyOf(KEYS);
    }

    @SuppressWarnings("unused")
    public Object getValueForKey(ConfigKey<DangerConfig, ?> key) {
        return key.get(this);
    }
}
