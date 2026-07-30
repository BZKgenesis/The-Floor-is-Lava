package net.bzkgns.theFloorIsLavaManager.items.abilities.gambling;

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
}
