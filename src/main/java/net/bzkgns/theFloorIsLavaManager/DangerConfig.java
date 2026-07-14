package net.bzkgns.theFloorIsLavaManager;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Regroupe tous les paramètres réglables de la partie (vitesse de montée de la lave,
 * dégâts, taille de bordure, etc.).
 *
 * Cet objet est volontairement séparé de DangerManager : il ne connaît ni les tâches
 * Bukkit, ni l'état de la partie (LOBBY/PREPARING/RISING). Il peut donc être lu et
 * modifié librement (commande, GUI, plus tard une API REST si besoin) sans jamais
 * risquer de démarrer/arrêter une tâche par effet de bord.
 */
public class DangerConfig {

    private int startLevel;
    private int endLevel;
    private int surfaceLevel;
    private int totalTimeBelowSurface;
    private int totalTimeAboveSurface;
    private double damage;
    private int damageEvery;
    private boolean placeLava;
    private boolean showAlert;
    private int lavaMargin;
    private int increaseSize;
    private int lavaRisingDelay;
    private int borderSizePreRise;
    private int borderSizeDuringRise;
    private int borderResizeTime;
    private boolean disablePvpDuringPreparation;
    private boolean keepInventoryDuringPreparation;
    private double fallDamageReduction;

    // Valeurs dérivées : jamais réglées directement, toujours recalculées.
    private double increaseAmountBelow;
    private double increaseAmountAbove;

    public static DangerConfig loadFrom(FileConfiguration config) {
        DangerConfig c = new DangerConfig();
        c.startLevel = config.getInt("danger.start-level");
        c.endLevel = config.getInt("danger.end-level");
        c.surfaceLevel = config.getInt("danger.surface-level");
        c.totalTimeBelowSurface = config.getInt("danger.total-time-below-surface");
        c.totalTimeAboveSurface = config.getInt("danger.total-time-above-surface");
        c.damage = config.getDouble("danger.damage");
        c.damageEvery = config.getInt("danger.damage-every");
        c.placeLava = config.getBoolean("danger.place-lava");
        c.showAlert = config.getBoolean("danger.show-alert");
        c.lavaMargin = config.getInt("danger.lava-margin");
        c.increaseSize = config.getInt("danger.increase-size");
        c.lavaRisingDelay = config.getInt("danger.lava-rising-delay");
        c.borderSizePreRise = config.getInt("danger.border-size-prerise");
        c.borderSizeDuringRise = config.getInt("danger.border-size-during-rise");
        c.borderResizeTime = config.getInt("danger.border-resize-time");
        c.disablePvpDuringPreparation = config.getBoolean("danger.disable-pvp-during-preparation");
        c.keepInventoryDuringPreparation = config.getBoolean("danger.keepinventory-during-preparation");
        c.fallDamageReduction = config.getDouble("danger.falldamage-reduction");
        c.recomputeIncreaseAmounts();
        return c;
    }

    /** Persiste les valeurs actuelles dans le config.yml (appeler plugin.saveConfig() ensuite). */
    public void saveTo(FileConfiguration config) {
        config.set("danger.start-level", startLevel);
        config.set("danger.end-level", endLevel);
        config.set("danger.surface-level", surfaceLevel);
        config.set("danger.total-time-below-surface", totalTimeBelowSurface);
        config.set("danger.total-time-above-surface", totalTimeAboveSurface);
        config.set("danger.damage", damage);
        config.set("danger.damage-every", damageEvery);
        config.set("danger.place-lava", placeLava);
        config.set("danger.show-alert", showAlert);
        config.set("danger.lava-margin", lavaMargin);
        config.set("danger.increase-size", increaseSize);
        config.set("danger.lava-rising-delay", lavaRisingDelay);
        config.set("danger.border-size-prerise", borderSizePreRise);
        config.set("danger.border-size-during-rise", borderSizeDuringRise);
        config.set("danger.border-resize-time", borderResizeTime);
        config.set("danger.disable-pvp-during-preparation", disablePvpDuringPreparation);
        config.set("danger.keepinventory-during-preparation", keepInventoryDuringPreparation);
        config.set("danger.falldamage-reduction", fallDamageReduction);
    }

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

    public int getLavaRisingDelay() { return lavaRisingDelay; }
    public void setLavaRisingDelay(int v) { this.lavaRisingDelay = v; }

    public int getBorderSizePreRise() { return borderSizePreRise; }
    public void setBorderSizePreRise(int v) { this.borderSizePreRise = v; }

    public int getBorderSizeDuringRise() { return borderSizeDuringRise; }
    public void setBorderSizeDuringRise(int v) { this.borderSizeDuringRise = v; }

    public int getBorderResizeTime() { return borderResizeTime; }
    public void setBorderResizeTime(int v) { this.borderResizeTime = v; }

    public boolean isDisablePvpDuringPreparation() { return disablePvpDuringPreparation; }
    public void setDisablePvpDuringPreparation(boolean v) { this.disablePvpDuringPreparation = v; }

    public boolean isKeepInventoryDuringPreparation() { return keepInventoryDuringPreparation; }
    public void setKeepInventoryDuringPreparation(boolean v) { this.keepInventoryDuringPreparation = v; }

    public double getFallDamageReduction() { return fallDamageReduction; }
    public void setFallDamageReduction(double v) { this.fallDamageReduction = v; }
}
