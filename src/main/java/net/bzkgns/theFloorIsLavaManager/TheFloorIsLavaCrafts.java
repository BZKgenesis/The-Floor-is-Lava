package net.bzkgns.theFloorIsLavaManager;

import net.bzkgns.theFloorIsLavaManager.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.RESOURCE_MATERIALS;

public class TheFloorIsLavaCrafts {

    public void setCrafts(Plugin plugin){

        for (CraftingRecipe craft : ItemManager.getAllCraftingRecipes(Bukkit.getServer())){
            plugin.getServer().addRecipe(craft);
        }

        NamespacedKey patateKey = new NamespacedKey(plugin, "patate");
        ItemStack patateItem = ItemStack.of(Material.BAKED_POTATO);

        ShapelessRecipe patateRecipe = new ShapelessRecipe(patateKey, patateItem);
            patateRecipe.addIngredient( new RecipeChoice.MaterialChoice(Material.DIRT,Material.SAND,Material.RED_SAND));
        plugin.getServer().addRecipe(patateRecipe);



        NamespacedKey blocksKey = new NamespacedKey(plugin, "blocs_en_plus");
        ItemStack blocksItem = ItemStack.of(Material.GRAY_WOOL,8);

        ShapelessRecipe blocksRecipe = new ShapelessRecipe(blocksKey, blocksItem);
            blocksRecipe.addIngredient(Material.COBBLESTONE);
        plugin.getServer().addRecipe(blocksRecipe);


        NamespacedKey enderPearlKey = new NamespacedKey(plugin, "enderPearl");
        ItemStack enderPearlItem = ItemStack.of(Material.ENDER_PEARL);

        ShapedRecipe enderPearlRecipe = new ShapedRecipe(enderPearlKey, enderPearlItem);
            enderPearlRecipe.shape("BA","AB");
            enderPearlRecipe.setIngredient('A', Material.IRON_INGOT);
            enderPearlRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(RESOURCE_MATERIALS));
        plugin.getServer().addRecipe(enderPearlRecipe);
    }
}
