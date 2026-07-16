package net.bzkgns.theFloorIsLavaManager.config.game;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;
import net.bzkgns.theFloorIsLavaManager.config.ConfigSection;

import java.util.List;

public class GameConfig implements ConfigSection<GameConfig> {
    private int lavaRisingDelay = 24000;
    private int borderSizePreRise = 200;
    private int borderSizeDuringRise = 75;
    private int borderResizeTime = 300;
    private boolean disablePvpDuringPreparation = true;
    private boolean keepInventoryDuringPreparation = true;
    private double fallDamageReduction = 0.5;
    private int minNbTeam = 1;

    private static final List<ConfigKey<GameConfig, ?>> KEYS = List.of(

            new ConfigKey<>(
                    "lava-rising-delay",
                    "Délai (ticks) avant que la lave commence à monter",
                    GameConfig::getLavaRisingDelay,
                    GameConfig::setLavaRisingDelay,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "border-size-prerise",
                    "Taille de bordure pendant la préparation",
                    GameConfig::getBorderSizePreRise,
                    GameConfig::setBorderSizePreRise,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "border-size-during-rise",
                    "Taille de bordure pendant la montée de la lave",
                    GameConfig::getBorderSizeDuringRise,
                    GameConfig::setBorderSizeDuringRise,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "border-resize-time",
                    "Durée (secondes) du rétrécissement de la bordure",
                    GameConfig::getBorderResizeTime,
                    GameConfig::setBorderResizeTime,
                    Integer::parseInt
            ),

            new ConfigKey<>(
                    "disable-pvp-during-preparation",
                    "Désactiver le PvP pendant la préparation",
                    GameConfig::isDisablePvpDuringPreparation,
                    GameConfig::setDisablePvpDuringPreparation,
                    Boolean::parseBoolean
            ),

            new ConfigKey<>(
                    "keep-inventory-during-preparation",
                    "Garder l'inventaire pendant la préparation",
                    GameConfig::isKeepInventoryDuringPreparation,
                    GameConfig::setKeepInventoryDuringPreparation,
                    Boolean::parseBoolean
            ),

            new ConfigKey<>(
                    "fall-damage-reduction",
                    "Multiplicateur de dégâts de chute",
                    GameConfig::getFallDamageReduction,
                    GameConfig::setFallDamageReduction,
                    Double::parseDouble
            ),

            new ConfigKey<>(
                    "min-nb-teams",
                    "Nombre minimum d'équipes pour démarrer la partie",
                    GameConfig::getMinNbTeam,
                    GameConfig::setMinNbTeam,
                    Integer::parseInt
            )

    );

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

    public int getMinNbTeam() { return minNbTeam; }
    public void setMinNbTeam(int v) { this.minNbTeam = v; }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public List<ConfigKey<GameConfig, ?>> getKeys() {
        return List.copyOf(KEYS);
    }
}
