package net.bzkgns.theFloorIsLavaManager.items.popup_tower;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

public class PopupTowerItem extends CustomItem {

    public PopupTowerItem() {
        super("popupTower",
                "Popup Tower",
                "Permet de créer une tour qui se déploie automatiquement",
                Rarity.EPIC,
                Material.CHEST,
                true
        );
    }

    @Override
    public ItemStack giveItem(){

        return itemStack.clone();
    }

    @Override
    public CraftingRecipe getRecipe() {
        ShapedRecipe popupTowerRecipe = new ShapedRecipe(key, giveItem());
        popupTowerRecipe.shape("ABA","BCB","ABA");
        popupTowerRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        popupTowerRecipe.setIngredient('B', Material.IRON_INGOT);
        popupTowerRecipe.setIngredient('C', Material.CHEST);
        return popupTowerRecipe;
    }
}
