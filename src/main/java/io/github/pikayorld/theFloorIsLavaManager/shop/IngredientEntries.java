package io.github.pikayorld.theFloorIsLavaManager.shop;

import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class   IngredientEntries {
    public List<IngredientEntry> ingredients;

    public  IngredientEntries(){
        this.ingredients = new ArrayList<>();
    }

    public  IngredientEntries(List<IngredientEntry> entries){
        this.ingredients = entries;
    }

    public boolean containsRecipeChoice(RecipeChoice recipeChoice){
        for (IngredientEntry ingredientEntry : ingredients){
            if (ingredientEntry.choice.equals(recipeChoice)){
                return true;
            }
        }
        return false;
    }

    public void addAmount(RecipeChoice recipeChoice,int amount){
        for (IngredientEntry ingredientEntry : ingredients){
            if (ingredientEntry.choice.equals(recipeChoice)){
                ingredientEntry.amount+=amount;
            }
        }
    }

    public List<IngredientEntry> toList(){
        return this.ingredients;
    }

    public void put(IngredientEntry entry){
        this.ingredients.add(entry);
    }

}
