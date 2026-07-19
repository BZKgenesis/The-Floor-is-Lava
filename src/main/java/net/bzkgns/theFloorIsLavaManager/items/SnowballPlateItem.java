package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

public class SnowballPlateItem extends CustomItem {
    public SnowballPlateItem() {
        super("snowball_plate",
                "items.snowball_plate.display_name",
                "items.snowball_plate.lore",
                Rarity.RARE,
                Material.SNOWBALL,
                true
        );
    }

    @Override
    public ItemStack giveItem(){
        return itemStack.clone();
    }

    @Override
    public CraftingRecipe getRecipe() {
        ShapedRecipe snowballPlateRecipe = new ShapedRecipe(key, giveItem());
        snowballPlateRecipe.shape(" A ","ABA"," A ");
        snowballPlateRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        snowballPlateRecipe.setIngredient('B', Material.IRON_INGOT);
        return snowballPlateRecipe;
    }
}
