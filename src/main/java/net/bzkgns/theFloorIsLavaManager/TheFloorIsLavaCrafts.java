package net.bzkgns.theFloorIsLavaManager;

import net.bzkgns.theFloorIsLavaManager.Items.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import static net.bzkgns.theFloorIsLavaManager.Utils.BlockUtils.RESOURCE_MATERIALS;

public class TheFloorIsLavaCrafts {

    public void setCrafts(Plugin plugin){

        plugin.getServer().addRecipe(new BatteItem().getRecipe());
        plugin.getServer().addRecipe(new EggBridge().getRecipe());
        plugin.getServer().addRecipe(new SnowballPlateItem().getRecipe());
        plugin.getServer().addRecipe(new CiseauxItem().getRecipe());
        plugin.getServer().addRecipe(new PopupTowerItem().getRecipe());
        plugin.getServer().addRecipe(new TeamInventoryItem().getRecipe());
        Plugin firebalPlugin = Bukkit.getPluginManager().getPlugin("ThrowableFireballs");
        if (firebalPlugin!=null){
            plugin.getServer().addRecipe(new FireBallItem().getRecipe());
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
