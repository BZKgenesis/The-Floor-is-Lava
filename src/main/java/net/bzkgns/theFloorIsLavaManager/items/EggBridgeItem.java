package net.bzkgns.theFloorIsLavaManager.items;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.RESOURCE_MATERIALS;
import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

public class EggBridgeItem extends CustomItem {

    private static int eggBridgeTask = -1;

    public EggBridgeItem() {
        if (eggBridgeTask == -1) {
            eggBridgeTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(), new EggBridgeTask(), 1, 1);
        }
        super("egg_bridge",
                "Pont d'oeufs",
                "Permet de créer un pont d'oeufs",
                Rarity.RARE,
                Material.EGG,
                true
        );

    }

    @Override
    public ItemStack giveItem(){
        return itemStack.clone();
    }

    @Override
    public CraftingRecipe getRecipe() {
        ShapedRecipe eggBridgeRecipe = new ShapedRecipe(key, giveItem());
        eggBridgeRecipe.shape("AAA","ABA","AAA");
        eggBridgeRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        eggBridgeRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(RESOURCE_MATERIALS));
        return eggBridgeRecipe;
    }
}
