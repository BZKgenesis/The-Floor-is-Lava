package net.bzkgns.theFloorIsLavaManager.currency;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public record Price(Integer material, Integer resource, Integer money) {

    public TextComponent displayResource(Audience audience){
        return displayResource(audience, true);
    }
    public TextComponent displayResource(Audience audience, boolean enoughMoney) {
        TextColor moneyColor = enoughMoney ? NamedTextColor.WHITE : NamedTextColor.RED;
        return Component.text("Resource: ", NamedTextColor.GRAY).append(Component.text(resource, moneyColor)).append(Component.text(" $", NamedTextColor.DARK_BLUE));
    }

    public TextComponent displayMaterial(Audience audience){
        return displayMaterial(audience, true);
    }

    public TextComponent displayMaterial(Audience audience, boolean enoughMoney) {
        TextColor moneyColor = enoughMoney ? NamedTextColor.WHITE : NamedTextColor.RED;
        return Component.text("Material: ", NamedTextColor.GRAY).append(Component.text(material, moneyColor)).append(Component.text(" $", NamedTextColor.DARK_GREEN));
    }

    public Price add(Price other) {
        return new Price(this.material + other.material, this.resource + other.resource, this.money + other.money);
    }

    public Price mul(int multiplier) {
        return new Price(this.material * multiplier, this.resource * multiplier, this.money * multiplier);
    }
}
