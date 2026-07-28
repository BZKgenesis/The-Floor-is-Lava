package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WoolItem extends CustomItem {

    public WoolItem() {
        super("wool",
                new ItemStack(Material.LIGHT_GRAY_WOOL, 2)
        );
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(1,0,0);
    }
}
