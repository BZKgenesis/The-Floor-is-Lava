package net.bzkgns.theFloorIsLava.currency;

import net.bzkgns.theFloorIsLava.lang.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import static net.bzkgns.theFloorIsLava.lang.LangManager.getLocale;

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

    public Component displayResource(Audience audience) {
        return displayResource(audience, true);
    }

    public Component prefixDisplayResource(Audience audience) {
        return Messages.component(audience, "money.resource");
    }

    public Component displayResource(Audience audience, boolean prefix) {
        if (prefix) {
            return prefixDisplayResource(audience)
                    .append(Component.text(String.format(getLocale(audience),"%,d",resource), NamedTextColor.WHITE))
                    .append(Component.text(" $", NamedTextColor.DARK_BLUE));
        } else {
            return Component.text(String.format(getLocale(audience),"%,d",resource), NamedTextColor.WHITE)
                    .append(Component.text(" $", NamedTextColor.DARK_BLUE));
        }
    }

    public Component displayMaterial(Audience audience) {
        return displayMaterial(audience, true);
    }

    public Component prefixDisplayMaterial(Audience audience) {
        return Messages.component(audience, "money.material");
    }

    public Component displayMaterial(Audience audience, boolean prefix) {
        if (prefix) {
            return prefixDisplayMaterial(audience)
                    .append(Component.text(String.format(getLocale(audience) ,"%,d",material), NamedTextColor.WHITE))
                    .append(Component.text(" $", NamedTextColor.DARK_GREEN));
        } else {
            return Component.text(String.format(getLocale(audience) ,"%,d",material), NamedTextColor.WHITE)
                    .append(Component.text(" $", NamedTextColor.DARK_GREEN));
        }
    }
}
