package net.bzkgns.theFloorIsLavaManager;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Registre des paramètres de DangerConfig exposés aux commandes/GUI.
 * Ajouter un paramètre ici suffit : /tfl config et ConfigGUI n'ont rien à changer.
 */
public enum DangerConfigKey {
    START_LEVEL("start-level", "Niveau Y de départ de la lave",
            c -> Integer.toString(c.getStartLevel()),
            (c, v) -> c.setStartLevel(Integer.parseInt(v))),
    END_LEVEL("end-level", "Niveau Y final de la lave",
            c -> Integer.toString(c.getEndLevel()),
            (c, v) -> c.setEndLevel(Integer.parseInt(v))),
    SURFACE_LEVEL("surface-level", "Niveau Y de la surface (change la vitesse de montée)",
            c -> Integer.toString(c.getSurfaceLevel()),
            (c, v) -> c.setSurfaceLevel(Integer.parseInt(v))),
    TOTAL_TIME_BELOW("total-time-below-surface", "Durée (ticks) pour monter jusqu'à la surface",
            c -> Integer.toString(c.getTotalTimeBelowSurface()),
            (c, v) -> c.setTotalTimeBelowSurface(Integer.parseInt(v))),
    TOTAL_TIME_ABOVE("total-time-above-surface", "Durée (ticks) pour monter de la surface au sommet",
            c -> Integer.toString(c.getTotalTimeAboveSurface()),
            (c, v) -> c.setTotalTimeAboveSurface(Integer.parseInt(v))),
    DAMAGE("damage", "Dégâts infligés sous le niveau de danger",
            c -> Double.toString(c.getDamage()),
            (c, v) -> c.setDamage(Double.parseDouble(v))),
    DAMAGE_EVERY("damage-every", "Fréquence (ticks) des dégâts",
            c -> Integer.toString(c.getDamageEvery()),
            (c, v) -> c.setDamageEvery(Integer.parseInt(v))),
    PLACE_LAVA("place-lava", "Poser réellement de la lave (sinon dégâts seuls)",
            c -> Boolean.toString(c.isPlaceLava()),
            (c, v) -> c.setPlaceLava(Boolean.parseBoolean(v))),
    SHOW_ALERT("show-alert", "Afficher les alertes de proximité de la zone",
            c -> Boolean.toString(c.isShowAlert()),
            (c, v) -> c.setShowAlert(Boolean.parseBoolean(v))),
    LAVA_MARGIN("lava-margin", "Marge (blocs) de lave hors bordure",
            c -> Integer.toString(c.getLavaMargin()),
            (c, v) -> c.setLavaMargin(Integer.parseInt(v))),
    INCREASE_SIZE("increase-size", "Hauteur (blocs) posée par palier",
            c -> Integer.toString(c.getIncreaseSize()),
            (c, v) -> c.setIncreaseSize(Integer.parseInt(v))),
    LAVA_RISING_DELAY("lava-rising-delay", "Délai (ticks) avant le début de la montée",
            c -> Integer.toString(c.getLavaRisingDelay()),
            (c, v) -> c.setLavaRisingDelay(Integer.parseInt(v))),
    BORDER_SIZE_PRERISE("border-size-prerise", "Taille de bordure pendant la préparation",
            c -> Integer.toString(c.getBorderSizePreRise()),
            (c, v) -> c.setBorderSizePreRise(Integer.parseInt(v))),
    BORDER_SIZE_DURING_RISE("border-size-during-rise", "Taille de bordure finale",
            c -> Integer.toString(c.getBorderSizeDuringRise()),
            (c, v) -> c.setBorderSizeDuringRise(Integer.parseInt(v))),
    BORDER_RESIZE_TIME("border-resize-time", "Durée (secondes) du rétrécissement",
            c -> Integer.toString(c.getBorderResizeTime()),
            (c, v) -> c.setBorderResizeTime(Integer.parseInt(v))),
    DISABLE_PVP_DURING_PREP("disable-pvp-during-preparation", "Désactiver le PvP pendant la préparation",
            c -> Boolean.toString(c.isDisablePvpDuringPreparation()),
            (c, v) -> c.setDisablePvpDuringPreparation(Boolean.parseBoolean(v))),
    KEEP_INVENTORY_DURING_PREP("keepinventory-during-preparation", "Garder l'inventaire pendant la préparation",
            c -> Boolean.toString(c.isKeepInventoryDuringPreparation()),
            (c, v) -> c.setKeepInventoryDuringPreparation(Boolean.parseBoolean(v))),
    FALL_DAMAGE_REDUCTION("falldamage-reduction", "Multiplicateur de dégâts de chute (bottes en cuir)",
            c -> Double.toString(c.getFallDamageReduction()),
            (c, v) -> c.setFallDamageReduction(Double.parseDouble(v)));

    private final String key;
    private final String description;
    private final Function<DangerConfig, String> getter;
    private final BiConsumer<DangerConfig, String> setter;

    DangerConfigKey(String key, String description,
                    Function<DangerConfig, String> getter,
                    BiConsumer<DangerConfig, String> setter) {
        this.key = key;
        this.description = description;
        this.getter = getter;
        this.setter = setter;
    }

    public String getKey() { return key; }
    public String getDescription() { return description; }
    public String get(DangerConfig config) { return getter.apply(config); }

    /** @throws NumberFormatException si la valeur ne correspond pas au type attendu */
    public void set(DangerConfig config, String rawValue) { setter.accept(config, rawValue); }

    public static DangerConfigKey fromKey(String key) {
        for (DangerConfigKey k : values()) {
            if (k.key.equalsIgnoreCase(key)) return k;
        }
        return null;
    }
}
