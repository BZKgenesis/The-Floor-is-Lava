package net.bzkgns.theFloorIsLavaManager.shop;

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
}
