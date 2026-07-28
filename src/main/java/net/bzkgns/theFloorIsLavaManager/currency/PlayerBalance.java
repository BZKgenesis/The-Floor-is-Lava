package net.bzkgns.theFloorIsLavaManager.currency;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

public class PlayerBalance {
    private Integer material;
    private Integer resource;
    private Integer money;

    public PlayerBalance(Integer material, Integer resource, Integer money) {
        this.material = material;
        this.resource = resource;
        this.money = money;
    }

    public Integer material() {
        return material;
    }

    public Integer resource() {
        return resource;
    }

    public Integer money() {
        return money;
    }

    public boolean hasEnough(@Nullable Price price) {
        if (price == null) return false;
        return hasEnoughMaterial(price) && hasEnoughResource(price) && hasEnoughMoney(price);
    }

    public boolean hasEnoughMaterial(@Nullable Price price) {
        if (price == null) return false;
        return material >= price.material();
    }

    public boolean hasEnoughResource(@Nullable Price price) {
        if (price == null) return false;
        return resource >= price.resource();
    }

    public boolean hasEnoughMoney(@Nullable Price price) {
        if (price == null) return false;
        return money >= price.money();
    }

    public boolean pay(@Nullable Price price) {
        if (price == null) return false;
        if (hasEnough(price)) {
            material -= price.material();
            resource -= price.resource();
            money -= price.money();
            return true;
        }
        return false;
    }

    public void add(Price price) {
        material += price.material();
        resource += price.resource();
        money += price.money();
    }

    public void set(Integer material, Integer resource, Integer money) {
        this.material = material;
        this.resource = resource;
        this.money = money;
    }

    public TextComponent displayResource(Audience audience) {
        return Component.text("Resource: ", NamedTextColor.GRAY).append(Component.text(resource, NamedTextColor.WHITE)).append(Component.text(" $", NamedTextColor.DARK_BLUE));
    }

    public TextComponent displayMaterial(Audience audience) {
        return Component.text("Material: ", NamedTextColor.GRAY).append(Component.text(material, NamedTextColor.WHITE)).append(Component.text(" $", NamedTextColor.DARK_GREEN));
    }
}
