package net.bzkgns.theFloorIsLava.config.game;

import net.bzkgns.theFloorIsLava.config.ConfigKey;
import net.bzkgns.theFloorIsLava.config.ConfigSection;

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
    private String default_lang = "en_us";
    private int startingCountdown = 10;
    private Boolean useResourcePackOverride = false;
    private String resourcePackUrlOverride = "";
    private String resourcePackSHA1Override = "";

    private static final List<ConfigKey<GameConfig, ?>> KEYS = List.of(
            GameConfigKeys.LAVA_RISING_DELAY,
            GameConfigKeys.BORDER_SIZE_PRE_RISE,
            GameConfigKeys.BORDER_SIZE_DURING_RISE,
            GameConfigKeys.BORDER_RESIZE_TIME,
            GameConfigKeys.DISABLE_PVP_DURING_PREPARATION,
            GameConfigKeys.KEEP_INVENTORY_DURING_PREPARATION,
            GameConfigKeys.FALL_DAMAGE_REDUCTION,
            GameConfigKeys.MIN_NB_TEAM,
            GameConfigKeys.DEFAULT_LANG,
            GameConfigKeys.STARTING_COUNTDOWN,
            GameConfigKeys.USE_RESOURCE_PACK_OVERRIDE,
            GameConfigKeys.RESOURCE_PACK_URL_OVERRIDE,
            GameConfigKeys.RESOURCE_PACK_SHA1_OVERRIDE

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

    public String getDefaultLang() { return default_lang; }
    public void setDefaultLang(String v) { this.default_lang = v; }

    public int getStartingCountdown() { return startingCountdown; }
    public void setStartingCountdown(int v) { this.startingCountdown = v; }

    public Boolean isUseResourcePackOverride() { return useResourcePackOverride; }
    public void setUseResourcePackOverride(Boolean v) { this.useResourcePackOverride = v; }

    public String getResourcePackUrlOverride() { return resourcePackUrlOverride; }
    public void setResourcePackUrlOverride(String v) { this.resourcePackUrlOverride = v; }

    public String getResourcePackSHA1Override() { return resourcePackSHA1Override; }
    public void setResourcePackSHA1Override(String v) { this.resourcePackSHA1Override = v; }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public List<ConfigKey<GameConfig, ?>> getKeys() {
        return List.copyOf(KEYS);
    }
}
