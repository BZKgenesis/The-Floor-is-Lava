package net.bzkgns.theFloorIsLavaManager.Items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static net.bzkgns.theFloorIsLavaManager.Utils.BlockUtils.WOOLS_MATERIALS;

public class PopupTowerItem extends CustomItem {

    public PopupTowerItem() {
        super("popupTower");
    }

    @Override
    public ItemStack giveItem(){
        ItemStack popupTowerStack = new ItemStack(Material.CHEST);
        popupTowerStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        popupTowerStack.setData(DataComponentTypes.ITEM_NAME, Component.text("Popup Tower"));
        ItemMeta popupTowerMeta = popupTowerStack.getItemMeta();
        popupTowerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "popup"), PersistentDataType.STRING, "popupTower");
        popupTowerStack.setItemMeta(popupTowerMeta);

        return popupTowerStack;
    }

    @Override
    public boolean isItem(ItemStack stack){
        if (stack.getType() == Material.CHEST){
            if(stack.getPersistentDataContainer().has(new NamespacedKey(plugin, "popup"))){
                return Objects.equals(stack.getPersistentDataContainer().get(new NamespacedKey(plugin, "popup"), PersistentDataType.STRING), "popupTower");
            }
        }
        return false;
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
