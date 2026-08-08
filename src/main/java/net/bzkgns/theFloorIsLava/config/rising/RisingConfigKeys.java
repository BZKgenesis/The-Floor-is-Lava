package net.bzkgns.theFloorIsLava.config.rising;

import net.bzkgns.theFloorIsLava.config.ConfigKey;

public final class RisingConfigKeys {

    private RisingConfigKeys() {
    }

    public static final ConfigKey<RisingConfig, Integer> START_LEVEL =
            new ConfigKey<>(
                    "start-level",
                    "config.rising.start-level",
                    RisingConfig::getStartLevel,
                    RisingConfig::setStartLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Integer> END_LEVEL =
            new ConfigKey<>(
                    "end-level",
                    "config.rising.end-level",
                    RisingConfig::getEndLevel,
                    RisingConfig::setEndLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Integer> SURFACE_LEVEL =
            new ConfigKey<>(
                    "surface-level",
                    "config.rising.surface-level",
                    RisingConfig::getSurfaceLevel,
                    RisingConfig::setSurfaceLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Integer> TOTAL_TIME_BELOW_SURFACE =
            new ConfigKey<>(
                    "total-time-below-surface",
                    "config.rising.total-time-below-surface",
                    RisingConfig::getTotalTimeBelowSurface,
                    RisingConfig::setTotalTimeBelowSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Integer> TOTAL_TIME_ABOVE_SURFACE =
            new ConfigKey<>(
                    "total-time-above-surface",
                    "config.rising.total-time-above-surface",
                    RisingConfig::getTotalTimeAboveSurface,
                    RisingConfig::setTotalTimeAboveSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Double> DAMAGE =
            new ConfigKey<>(
                    "damage",
                    "config.rising.damage",
                    RisingConfig::getDamage,
                    RisingConfig::setDamage,
                    Double::parseDouble
            );

    public static final ConfigKey<RisingConfig, Integer> DAMAGE_EVERY =
            new ConfigKey<>(
                    "damage-every",
                    "config.rising.damage-every",
                    RisingConfig::getDamageEvery,
                    RisingConfig::setDamageEvery,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Boolean> PLACE_LAVA =
            new ConfigKey<>(
                    "place-lava",
                    "config.rising.place-lava",
                    RisingConfig::isPlaceLava,
                    RisingConfig::setPlaceLava,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<RisingConfig, Boolean> SHOW_ALERT =
            new ConfigKey<>(
                    "show-alert",
                    "config.rising.show-alert",
                    RisingConfig::isShowAlert,
                    RisingConfig::setShowAlert,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<RisingConfig, Integer> LAVA_MARGIN =
            new ConfigKey<>(
                    "lava-margin",
                    "config.rising.lava-margin",
                    RisingConfig::getLavaMargin,
                    RisingConfig::setLavaMargin,
                    Integer::parseInt
            );

    public static final ConfigKey<RisingConfig, Integer> INCREASE_SIZE =
            new ConfigKey<>(
                    "increase-size",
                    "config.rising.increase-size",
                    RisingConfig::getIncreaseSize,
                    RisingConfig::setIncreaseSize,
                    Integer::parseInt
            );
}
