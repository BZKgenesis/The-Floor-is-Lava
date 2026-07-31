package net.bzkgns.theFloorIsLava.config.danger;

import net.bzkgns.theFloorIsLava.config.ConfigKey;

public final class DangerConfigKeys {

    private DangerConfigKeys() {
    }

    public static final ConfigKey<DangerConfig, Integer> START_LEVEL =
            new ConfigKey<>(
                    "start-level",
                    "config.danger.start-level",
                    DangerConfig::getStartLevel,
                    DangerConfig::setStartLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> END_LEVEL =
            new ConfigKey<>(
                    "end-level",
                    "config.danger.end-level",
                    DangerConfig::getEndLevel,
                    DangerConfig::setEndLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> SURFACE_LEVEL =
            new ConfigKey<>(
                    "surface-level",
                    "config.danger.surface-level",
                    DangerConfig::getSurfaceLevel,
                    DangerConfig::setSurfaceLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> TOTAL_TIME_BELOW_SURFACE =
            new ConfigKey<>(
                    "total-time-below-surface",
                    "config.danger.total-time-below-surface",
                    DangerConfig::getTotalTimeBelowSurface,
                    DangerConfig::setTotalTimeBelowSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> TOTAL_TIME_ABOVE_SURFACE =
            new ConfigKey<>(
                    "total-time-above-surface",
                    "config.danger.total-time-above-surface",
                    DangerConfig::getTotalTimeAboveSurface,
                    DangerConfig::setTotalTimeAboveSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Double> DAMAGE =
            new ConfigKey<>(
                    "damage",
                    "config.danger.damage",
                    DangerConfig::getDamage,
                    DangerConfig::setDamage,
                    Double::parseDouble
            );

    public static final ConfigKey<DangerConfig, Integer> DAMAGE_EVERY =
            new ConfigKey<>(
                    "damage-every",
                    "config.danger.damage-every",
                    DangerConfig::getDamageEvery,
                    DangerConfig::setDamageEvery,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Boolean> PLACE_LAVA =
            new ConfigKey<>(
                    "place-lava",
                    "config.danger.place-lava",
                    DangerConfig::isPlaceLava,
                    DangerConfig::setPlaceLava,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<DangerConfig, Boolean> SHOW_ALERT =
            new ConfigKey<>(
                    "show-alert",
                    "config.danger.show-alert",
                    DangerConfig::isShowAlert,
                    DangerConfig::setShowAlert,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<DangerConfig, Integer> LAVA_MARGIN =
            new ConfigKey<>(
                    "lava-margin",
                    "config.danger.lava-margin",
                    DangerConfig::getLavaMargin,
                    DangerConfig::setLavaMargin,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> INCREASE_SIZE =
            new ConfigKey<>(
                    "increase-size",
                    "config.danger.increase-size",
                    DangerConfig::getIncreaseSize,
                    DangerConfig::setIncreaseSize,
                    Integer::parseInt
            );
}
