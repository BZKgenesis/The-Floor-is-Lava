package io.github.pikayorld.theFloorIsLavaManager;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TheFloorIsLavaCrafts {

    public void setCrafts(Plugin plugin){
        List<Material> ressourceMaterials = List.of(Material.DIAMOND,Material.GOLD_INGOT,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.AMETHYST_SHARD);
        List<Material> woolMaterials = List.of(Material.WHITE_WOOL,Material.ORANGE_WOOL,Material.MAGENTA_WOOL,Material.LIGHT_BLUE_WOOL,Material.YELLOW_WOOL,Material.LIME_WOOL,Material.PINK_WOOL,Material.GRAY_WOOL,Material.LIGHT_GRAY_WOOL,Material.CYAN_WOOL,Material.PURPLE_WOOL,Material.BLUE_WOOL,Material.BROWN_WOOL,Material.GREEN_WOOL,Material.RED_WOOL,Material.BLACK_WOOL);

        NamespacedKey batteKey = new NamespacedKey(plugin, "batte");
        ItemStack batteItem = ItemStack.of(Material.STICK);
            batteItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Batte"));
            batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 3));


        ShapedRecipe batteRecipe = new ShapedRecipe(batteKey, batteItem);
            batteRecipe.shape("  A", " B ", "C  ");
            batteRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(ressourceMaterials));
            batteRecipe.setIngredient('B', Material.IRON_INGOT);
            batteRecipe.setIngredient('C', Material.STICK);

        plugin.getServer().addRecipe(batteRecipe);


        NamespacedKey eggBridgeKey = new NamespacedKey(plugin, "eggBridge");
        ItemStack eggBridgeItem = ItemStack.of(Material.EGG);
        eggBridgeItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Pont oeuf"));


        ShapedRecipe eggBridgeRecipe = new ShapedRecipe(eggBridgeKey, eggBridgeItem);
        eggBridgeRecipe.shape("AAA","ABA","AAA");
        eggBridgeRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(woolMaterials));
        eggBridgeRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(ressourceMaterials));

        plugin.getServer().addRecipe(eggBridgeRecipe);


        NamespacedKey patateKey = new NamespacedKey(plugin, "patate");
        ItemStack patateItem = ItemStack.of(Material.BAKED_POTATO);


        ShapelessRecipe patateRecipe = new ShapelessRecipe(patateKey, patateItem);
        patateRecipe.addIngredient(Material.DIRT);

        plugin.getServer().addRecipe(patateRecipe);


        NamespacedKey blocksKey = new NamespacedKey(plugin, "blocs_en_plus");
        ItemStack blocksItem = ItemStack.of(Material.GRAY_WOOL,8);


        ShapelessRecipe blocksRecipe = new ShapelessRecipe(blocksKey, blocksItem);
        blocksRecipe.addIngredient(Material.COBBLESTONE);

        plugin.getServer().addRecipe(blocksRecipe);

    }




}
