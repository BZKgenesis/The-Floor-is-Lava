package net.bzkgns.theFloorIsLavaManager.config.game;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public enum GameConfigKey {
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
    KEEP_INVENTORY_DURING_PREP("keep-inventory-during-preparation", "Garder l'inventaire pendant la préparation",
            c -> Boolean.toString(c.isKeepInventoryDuringPreparation()),
            (c, v) -> c.setKeepInventoryDuringPreparation(Boolean.parseBoolean(v))),
    FALL_DAMAGE_REDUCTION("falldamage-reduction", "Multiplicateur de dégâts de chute (bottes en cuir)",
            c -> Double.toString(c.getFallDamageReduction()),
            (c, v) -> c.setFallDamageReduction(Double.parseDouble(v)));

    private final String key;
    private final String description;
    private final Function<GameConfig, String> getter;
    private final BiConsumer<GameConfig, String> setter;

    GameConfigKey(String key, String description,
                    Function<GameConfig, String> getter,
                    BiConsumer<GameConfig, String> setter) {
        this.key = key;
        this.description = description;
        this.getter = getter;
        this.setter = setter;
    }

    public String getKey() { return key; }
    @SuppressWarnings("unused")
    public String getDescription() { return description; }
    public String get(GameConfig config) { return getter.apply(config); }

    /** @throws NumberFormatException si la valeur ne correspond pas au type attendu */
    public void set(GameConfig config, String rawValue) { setter.accept(config, rawValue); }

    @SuppressWarnings("unused")
    public static GameConfigKey fromKey(String key) {
        for (GameConfigKey k : values()) {
            if (k.key.equalsIgnoreCase(key)) return k;
        }
        return null;
    }
}
