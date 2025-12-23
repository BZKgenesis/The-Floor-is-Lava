package io.github.pikayorld.theFloorIsLavaManager.shop;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopRecipe {
    public ItemStack result;
    public List<IngredientEntry> ingredients;

    public ShopRecipe(ItemStack result, IngredientEntry... ingredients) {
        this.result = result;
        this.ingredients = List.of(ingredients);
    }
    public ShopRecipe(ItemStack result, List<IngredientEntry> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }
    public ShopRecipe(ItemStack result, IngredientEntries ingredients) {
        this.result = result;
        this.ingredients = ingredients.toList();
    }
}
