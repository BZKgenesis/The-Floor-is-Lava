package net.bzkgns.theFloorIsLavaManager.Items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.Weapon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.Utils.BlockUtils.RESOURCE_MATERIALS;
import static net.bzkgns.theFloorIsLavaManager.Utils.TextUtils.plainText;

@SuppressWarnings("UnstableApiUsage")
public class BatteItem extends CustomItem {
    public BatteItem() {
        super("batte");
    }

    @Override
    public ItemStack giveItem() {
        ItemStack batteItem = ItemStack.of(Material.STICK);
        batteItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Batte"));
        batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 3));
        batteItem.setData(DataComponentTypes.MAX_DAMAGE, 10);
        batteItem.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        batteItem.setData(DataComponentTypes.WEAPON, Weapon.weapon());
        return batteItem;
    }

    @Override
    public boolean isItem(ItemStack stack) {
        if (stack.getType() == Material.STICK && plainText( stack.getData(DataComponentTypes.ITEM_NAME)).equals("Batte")) {
            return stack.getData(DataComponentTypes.WEAPON) != null;
        }
        return false;
    }

    @Override
    public CraftingRecipe getRecipe() {

        ShapedRecipe batteRecipe = new ShapedRecipe(key, giveItem());
        batteRecipe.shape("  A", " B ", "C  ");
        batteRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(RESOURCE_MATERIALS));
        batteRecipe.setIngredient('B', Material.IRON_INGOT);
        batteRecipe.setIngredient('C', Material.STICK);
        return batteRecipe;
    }
}
