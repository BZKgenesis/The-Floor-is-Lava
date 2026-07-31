package net.bzkgns.theFloorIsLava.config.game;

import net.bzkgns.theFloorIsLava.config.ConfigKey;

public final class GameConfigKeys {
    private GameConfigKeys() {
    }

    public static final ConfigKey<GameConfig, Integer> LAVA_RISING_DELAY =
            new ConfigKey<>(
                    "lava-rising-delay",
                    "config.game.lava-rising-delay",
                    GameConfig::getLavaRisingDelay,
                    GameConfig::setLavaRisingDelay,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_SIZE_PRE_RISE =
            new ConfigKey<>(
                    "border-size-prerise",
                    "config.game.border-size-prerise",
                    GameConfig::getBorderSizePreRise,
                    GameConfig::setBorderSizePreRise,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_SIZE_DURING_RISE =
            new ConfigKey<>(
                    "border-size-during-rise",
                    "config.game.border-size-during-rise",
                    GameConfig::getBorderSizeDuringRise,
                    GameConfig::setBorderSizeDuringRise,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Integer> BORDER_RESIZE_TIME =
            new ConfigKey<>(
                    "border-resize-time",
                    "config.game.border-resize-time",
                    GameConfig::getBorderResizeTime,
                    GameConfig::setBorderResizeTime,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, Boolean> DISABLE_PVP_DURING_PREPARATION =
            new ConfigKey<>(
                    "disable-pvp-during-preparation",
                    "config.game.disable-pvp-during-preparation",
                    GameConfig::isDisablePvpDuringPreparation,
                    GameConfig::setDisablePvpDuringPreparation,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<GameConfig, Boolean> KEEP_INVENTORY_DURING_PREPARATION =
            new ConfigKey<>(
                    "keep-inventory-during-preparation",
                    "config.game.keep-inventory-during-preparation",
                    GameConfig::isKeepInventoryDuringPreparation,
                    GameConfig::setKeepInventoryDuringPreparation,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<GameConfig, Double> FALL_DAMAGE_REDUCTION =
            new ConfigKey<>(
                    "fall-damage-reduction",
                    "config.game.fall-damage-reduction",
                    GameConfig::getFallDamageReduction,
                    GameConfig::setFallDamageReduction,
                    Double::parseDouble
            );

    public static final ConfigKey<GameConfig, Integer> MIN_NB_TEAM =
            new ConfigKey<>(
                    "min-nb-teams",
                    "config.game.min-nb-teams",
                    GameConfig::getMinNbTeam,
                    GameConfig::setMinNbTeam,
                    Integer::parseInt
            );

    public static final ConfigKey<GameConfig, String> DEFAULT_LANG =
            new ConfigKey<>(
                    "default-lang",
                    "config.game.default-lang",
                    GameConfig::getDefaultLang,
                    GameConfig::setDefaultLang,
                    String::valueOf
            );

    public static final ConfigKey<GameConfig, Integer> STARTING_COUNTDOWN =
            new ConfigKey<>(
                    "starting-countdown",
                    "config.game.starting-countdown",
                    GameConfig::getStartingCountdown,
                    GameConfig::setStartingCountdown,
                    Integer::parseInt
            );


}
