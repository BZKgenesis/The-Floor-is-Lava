package net.bzkgns.theFloorIsLavaManager.Items;

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

import static net.bzkgns.theFloorIsLavaManager.Utils.BlockUtils.RESOURCE_MATERIALS;
import static net.bzkgns.theFloorIsLavaManager.Utils.BlockUtils.WOOLS_MATERIALS;

@SuppressWarnings("UnstableApiUsage")
public class EggBridge extends CustomItem {


    public EggBridge() {
        super("eggBridge");
    }

    @Override
    public ItemStack giveItem(){
        ItemStack stack = new ItemStack(Material.EGG);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class),"eggBridge"), PersistentDataType.STRING, "eggBridge");
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.text("Egg Bridge"));
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    @Override
    public boolean isItem(ItemStack stack){
        if (stack.getType() == Material.EGG){
            ItemMeta meta = stack.getItemMeta();
            return Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "eggBridge"), PersistentDataType.STRING), "eggBridge");
        }
        return false;
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
