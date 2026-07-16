package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

@SuppressWarnings("UnstableApiUsage")
public class CiseauxItem extends CustomItem {
    public CiseauxItem() {
        super("ciseaux", "Ciseaux", "Permet de couper les blocs de laine et de les récupérer", Rarity.COMMON, Material.SHEARS, true);
    }

    @Override
    public ItemStack giveItem() {
        ItemStack ciseauxItem = itemStack.clone();
        ciseauxItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.EFFICIENCY,3));
        return ciseauxItem;
    }

    @Override
    public CraftingRecipe getRecipe() {
        ShapedRecipe ciseauxRecipe = new ShapedRecipe(key, giveItem());
        ciseauxRecipe.shape(" A","A ");
        ciseauxRecipe.setIngredient('A', Material.IRON_INGOT);
        return ciseauxRecipe;
    }
}
