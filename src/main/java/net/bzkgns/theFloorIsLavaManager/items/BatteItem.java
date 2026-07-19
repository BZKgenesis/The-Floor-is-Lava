package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.RESOURCE_MATERIALS;

@SuppressWarnings("UnstableApiUsage")
public class BatteItem extends CustomItem {
    public BatteItem() {
        super("batte",
                "items.batte.display_name",
                "items.batte.lore",
                Rarity.COMMON,
                Material.STICK,
                true);
    }

    @Override
    public ItemStack giveItem() {
        ItemStack batteItem = itemStack.clone();
        batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 3));
        batteItem.setData(DataComponentTypes.MAX_DAMAGE, 10);
        batteItem.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        batteItem.setData(DataComponentTypes.WEAPON, Weapon.weapon());
        return batteItem;
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
