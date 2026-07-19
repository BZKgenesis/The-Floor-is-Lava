package net.bzkgns.theFloorIsLavaManager.shop;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.bzkgns.theFloorIsLavaManager.items.ItemManager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record ShopRecipe(ItemStack result, List<IngredientEntry> ingredients) {
    @SuppressWarnings("unused")
    public ShopRecipe(ItemStack result, IngredientEntry... ingredients) {
        this(result, List.of(ingredients));
    }

    @SuppressWarnings("unused")
    public ShopRecipe {
    }

    public ShopRecipe(ItemStack result, IngredientEntries ingredients) {
        this(result, ingredients.toList());
    }

    public ItemStack result(Audience audience) {
        CustomItem customItem = ItemManager.getAssociatedCustomItem(result);
        if (customItem == null)
            return result;
        return customItem.giveItem(audience);
    }
}
