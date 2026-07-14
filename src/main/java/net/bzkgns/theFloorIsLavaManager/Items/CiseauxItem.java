package net.bzkgns.theFloorIsLavaManager.Items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.Utils.TextUtils.plainText;

@SuppressWarnings("UnstableApiUsage")
public class CiseauxItem extends CustomItem {
    public CiseauxItem() {
        super("ciseaux");
    }

    @Override
    public ItemStack giveItem() {
        ItemStack ciseauxItem = ItemStack.of(Material.SHEARS);
        ciseauxItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Ciseaux"));
        ciseauxItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.EFFICIENCY,3));
        return ciseauxItem;
    }

    @Override
    public boolean isItem(ItemStack stack) {
        if (stack.getType() == Material.SHEARS && plainText(stack.getData(DataComponentTypes.ITEM_NAME)).equals("Ciseaux")) {
            return stack.getData(DataComponentTypes.ENCHANTMENTS) != null;
        }
        return false;
    }

    @Override
    public CraftingRecipe getRecipe() {
        ShapedRecipe ciseauxRecipe = new ShapedRecipe(key, giveItem());
        ciseauxRecipe.shape(" A","A ");
        ciseauxRecipe.setIngredient('A', Material.IRON_INGOT);
        return ciseauxRecipe;
    }
}
