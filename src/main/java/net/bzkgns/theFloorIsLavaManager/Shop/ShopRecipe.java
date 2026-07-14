package net.bzkgns.theFloorIsLavaManager.Shop;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopRecipe {
    public ItemStack result;
    public List<IngredientEntry> ingredients;

    @SuppressWarnings("unused")
    public ShopRecipe(ItemStack result, IngredientEntry... ingredients) {
        this.result = result;
        this.ingredients = List.of(ingredients);
    }

    @SuppressWarnings("unused")
    public ShopRecipe(ItemStack result, List<IngredientEntry> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public ShopRecipe(ItemStack result, IngredientEntries ingredients) {
        this.result = result;
        this.ingredients = ingredients.toList();
    }
}
