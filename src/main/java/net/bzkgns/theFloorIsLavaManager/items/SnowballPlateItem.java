package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

@SuppressWarnings("UnstableApiUsage")
public class SnowballPlateItem extends CustomItem {
    public SnowballPlateItem() {
        super("snowballPlate");
    }

    @Override
    public ItemStack giveItem(){
        ItemStack stack = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class),"snowballPlate"), PersistentDataType.STRING, "snowballPlate");
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.text("Snowball Plate"));
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    @Override
    public boolean isItem(ItemStack stack){
        if (stack.getType() == Material.SNOWBALL){
            ItemMeta meta = stack.getItemMeta();
            return Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "snowballPlate"), PersistentDataType.STRING), "snowballPlate");
        }
        return false;
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
