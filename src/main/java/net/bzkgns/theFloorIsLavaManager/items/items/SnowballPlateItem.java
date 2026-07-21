package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
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
    public CraftingRecipe getRecipe(Audience audience) {
        ShapedRecipe snowballPlateRecipe = new ShapedRecipe(key, giveItem(audience));
        snowballPlateRecipe.shape(" A ","ABA"," A ");
        snowballPlateRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        snowballPlateRecipe.setIngredient('B', Material.IRON_INGOT);
        return snowballPlateRecipe;
    }
}
