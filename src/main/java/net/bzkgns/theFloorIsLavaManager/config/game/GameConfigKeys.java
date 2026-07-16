package net.bzkgns.theFloorIsLavaManager.config.game;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public final class GameConfigKeys {
    private GameConfigKeys() {
    }

    public static final ConfigKey<GameConfig, Integer> LAVA_RISING_DELAY =
            new ConfigKey<>(
                    "lava-rising-delay",
                    "Délai (ticks) avant que la lave commence à monter",
                    GameConfig::getLavaRisingDelay,
                    GameConfig::setLavaRisingDelay,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_SIZE_PRE_RISE =
            new ConfigKey<>(
                    "border-size-prerise",
                    "Taille de bordure pendant la préparation",
                    GameConfig::getBorderSizePreRise,
                    GameConfig::setBorderSizePreRise,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_SIZE_DURING_RISE =
            new ConfigKey<>(
                    "border-size-during-rise",
                    "Taille de bordure pendant la montée de la lave",
                    GameConfig::getBorderSizeDuringRise,
                    GameConfig::setBorderSizeDuringRise,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_RESIZE_TIME =
            new ConfigKey<>(
                    "border-resize-time",
                    "Durée (secondes) du rétrécissement de la bordure",
                    GameConfig::getBorderResizeTime,
                    GameConfig::setBorderResizeTime,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Boolean> DISABLE_PVP_DURING_PREPARATION =
            new ConfigKey<>(
                    "disable-pvp-during-preparation",
                    "Désactiver le PvP pendant la préparation",
                    GameConfig::isDisablePvpDuringPreparation,
                    GameConfig::setDisablePvpDuringPreparation,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<GameConfig, Boolean> KEEP_INVENTORY_DURING_PREPARATION =
            new ConfigKey<>(
                    "keep-inventory-during-preparation",
                    "Garder l'inventaire pendant la préparation",
                    GameConfig::isKeepInventoryDuringPreparation,
                    GameConfig::setKeepInventoryDuringPreparation,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<GameConfig, Double> FALL_DAMAGE_REDUCTION =
            new ConfigKey<>(
                    "fall-damage-reduction",
                    "Multiplicateur de dégâts de chute",
                    GameConfig::getFallDamageReduction,
                    GameConfig::setFallDamageReduction,
                    Double::parseDouble
            );

    public static final ConfigKey<GameConfig, Integer> MIN_NB_TEAM =
            new ConfigKey<>(
                    "min-nb-teams",
                    "Nombre minimum d'équipes pour démarrer la partie",
                    GameConfig::getMinNbTeam,
                    GameConfig::setMinNbTeam,
                    Integer::parseInt
            );


}
