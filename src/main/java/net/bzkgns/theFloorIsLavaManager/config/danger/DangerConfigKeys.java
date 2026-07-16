package net.bzkgns.theFloorIsLavaManager.config.danger;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public final class DangerConfigKeys {

    private DangerConfigKeys() {
    }

    public static final ConfigKey<DangerConfig, Integer> START_LEVEL =
            new ConfigKey<>(
                    "start-level",
                    "Niveau Y de départ de la lave",
                    DangerConfig::getStartLevel,
                    DangerConfig::setStartLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> END_LEVEL =
            new ConfigKey<>(
                    "end-level",
                    "Niveau Y final de la lave",
                    DangerConfig::getEndLevel,
                    DangerConfig::setEndLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> SURFACE_LEVEL =
            new ConfigKey<>(
                    "surface-level",
                    "Niveau Y de la surface (change la vitesse de montée)",
                    DangerConfig::getSurfaceLevel,
                    DangerConfig::setSurfaceLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> TOTAL_TIME_BELOW_SURFACE =
            new ConfigKey<>(
                    "total-time-below-surface",
                    "Durée (ticks) pour monter jusqu'à la surface",
                    DangerConfig::getTotalTimeBelowSurface,
                    DangerConfig::setTotalTimeBelowSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> TOTAL_TIME_ABOVE_SURFACE =
            new ConfigKey<>(
                    "total-time-above-surface",
                    "Durée (ticks) pour monter de la surface au sommet",
                    DangerConfig::getTotalTimeAboveSurface,
                    DangerConfig::setTotalTimeAboveSurface,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Double> DAMAGE =
            new ConfigKey<>(
                    "damage",
                    "Dégâts de la zone",
                    DangerConfig::getDamage,
                    DangerConfig::setDamage,
                    Double::parseDouble
            );

    public static final ConfigKey<DangerConfig, Integer> DAMAGE_EVERY =
            new ConfigKey<>(
                    "damage-every",
                    "Fréquence (ticks) des dégâts",
                    DangerConfig::getDamageEvery,
                    DangerConfig::setDamageEvery,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Boolean> PLACE_LAVA =
            new ConfigKey<>(
                    "place-lava",
                    "Poser réellement de la lave (sinon dégâts seuls)",
                    DangerConfig::isPlaceLava,
                    DangerConfig::setPlaceLava,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<DangerConfig, Boolean> SHOW_ALERT =
            new ConfigKey<>(
                    "show-alert",
                    "Afficher les alertes de proximité de la zone",
                    DangerConfig::isShowAlert,
                    DangerConfig::setShowAlert,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<DangerConfig, Integer> LAVA_MARGIN =
            new ConfigKey<>(
                    "lava-margin",
                    "Marge (blocs) de lave hors bordure",
                    DangerConfig::getLavaMargin,
                    DangerConfig::setLavaMargin,
                    Integer::parseInt
            );

    public static final ConfigKey<DangerConfig, Integer> INCREASE_SIZE =
            new ConfigKey<>(
                    "increase-size",
                    "Hauteur (blocs) posée par palier",
                    DangerConfig::getIncreaseSize,
                    DangerConfig::setIncreaseSize,
                    Integer::parseInt
            );
}
