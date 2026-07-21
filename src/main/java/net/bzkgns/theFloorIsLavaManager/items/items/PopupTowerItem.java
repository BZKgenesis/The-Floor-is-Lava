package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

public class PopupTowerItem extends CustomItem {

    public PopupTowerItem() {
        super("popup_tower",
                "items.popup_tower.display_name",
                "items.popup_tower.lore",
                Rarity.EPIC,
                Material.CHEST,
                true
        );
    }


    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        ShapedRecipe popupTowerRecipe = new ShapedRecipe(key, giveItem(audience));
        popupTowerRecipe.shape("ABA","BCB","ABA");
        popupTowerRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        popupTowerRecipe.setIngredient('B', Material.IRON_INGOT);
        popupTowerRecipe.setIngredient('C', Material.CHEST);
        return popupTowerRecipe;
    }
}
