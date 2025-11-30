package io.github.pikayorld.theFloorIsLavaManager;

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
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TheFloorIsLavaCrafts {

    public void setCrafts(Plugin plugin){
        List<Material> materials = List.of(Material.DIAMOND,Material.GOLD_INGOT,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.AMETHYST_SHARD);

        NamespacedKey batteKey = new NamespacedKey(plugin, "batte");
        ItemStack batteItem = ItemStack.of(Material.STICK);
            batteItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Batte"));
            batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 3));


        ShapedRecipe batteRecipe = new ShapedRecipe(batteKey, batteItem);
            batteRecipe.shape("  A", " B ", "C  ");
            batteRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(materials));
            batteRecipe.setIngredient('B', Material.IRON_INGOT);
            batteRecipe.setIngredient('C', Material.STICK);

        plugin.getServer().addRecipe(batteRecipe);


        NamespacedKey key = new NamespacedKey(plugin, "fireball");
        ItemStack fireballItem = ItemStack.of(Material.STICK);
        fireballItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Boule de Feu"));


        ShapedRecipe fireballRecipe = new ShapedRecipe(key, fireballItem);
        fireballRecipe.shape("  A", " B ", "C  ");
        fireballRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(materials));
        fireballRecipe.setIngredient('B', Material.IRON_INGOT);
        fireballRecipe.setIngredient('C', Material.STICK);

        plugin.getServer().addRecipe(fireballRecipe);

    }




}
