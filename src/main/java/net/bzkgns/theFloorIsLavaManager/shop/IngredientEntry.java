package net.bzkgns.theFloorIsLavaManager.shop;

import org.bukkit.inventory.RecipeChoice;

public class IngredientEntry {
    public RecipeChoice choice;
    public int amount;

    public IngredientEntry(RecipeChoice choice, int amount) {
        this.choice = choice;
        this.amount = amount;
    }
}
