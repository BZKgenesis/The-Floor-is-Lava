package net.bzkgns.theFloorIsLavaManager.items.abilities.gambling;

import net.bzkgns.theFloorIsLavaManager.config.gambling.GamblingConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum GamblingSymbol {
    CERISE("cerise", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/poisonous_potato>")), //Component.text("\uD83C\uDF52", NamedTextColor.RED)
    CITRON("citron", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/apple>")), //Component.text("\uD83C\uDF4B",NamedTextColor.YELLOW)
    RAISIN("raisin", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/copper_ingot>")), //Component.text("\uD83C\uDF47",NamedTextColor.DARK_PURPLE)
    CLOCHE("cloche", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/iron_ingot>")), //Component.text("\uD83D\uDD14",NamedTextColor.GOLD)
    ETOILE("etoile", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/gold_ingot>")), //Component.text("⭐",NamedTextColor.YELLOW)
    DIAMOND("diamant", MiniMessage.miniMessage().deserialize("<sprite:\"minecraft:items\":item/diamond>")), //Component.text("\uD83D\uDC8E",NamedTextColor.AQUA)
    SEVEN("seven", Component.text("7", NamedTextColor.GREEN));
    private final Component symbol;
    private final String name;

    GamblingSymbol(String name, Component s) {
        this.symbol = s;
        this.name = name;
    }

    public Component getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getProbability(GamblingConfig config) {
        return switch (this) {
            case CERISE -> config.getCeriseProbability();
            case CITRON -> config.getCitronProbability();
            case RAISIN -> config.getRaisinProbability();
            case CLOCHE -> config.getClocheProbability();
            case ETOILE -> config.getEtoileProbability();
            case DIAMOND -> config.getDiamondProbability();
            case SEVEN -> config.getSevenProbability();
        };
    }

    public double getTwoGain(GamblingConfig config) {
        return switch (this) {
            case CERISE -> config.getCeriseTwoKind();
            case CITRON -> config.getCitronTwoKind();
            case RAISIN -> config.getRaisinTwoKind();
            case CLOCHE -> config.getClocheTwoKind();
            case ETOILE -> config.getEtoileTwoKind();
            case DIAMOND -> config.getDiamondTwoKind();
            case SEVEN -> config.getSevenTwoKind();
        };
    }

    public double getOneGain(GamblingConfig config) {
        return switch (this) {
            case CERISE -> config.getCeriseOneKind();
            case CITRON -> config.getCitronOneKind();
            case RAISIN -> config.getRaisinOneKind();
            case CLOCHE -> config.getClocheOneKind();
            case ETOILE -> config.getEtoileOneKind();
            case DIAMOND -> config.getDiamondOneKind();
            case SEVEN -> config.getSevenOneKind();
        };
    }

    public double getJackpotGain(GamblingConfig config) {
        return switch (this) {
            case CERISE -> config.getCeriseJackpot();
            case CITRON -> config.getCitronJackpot();
            case RAISIN -> config.getRaisinJackpot();
            case CLOCHE -> config.getClocheJackpot();
            case ETOILE -> config.getEtoileJackpot();
            case DIAMOND -> config.getDiamondJackpot();
            case SEVEN -> config.getSevenJackpot();
        };
    }
}
