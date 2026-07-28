package net.bzkgns.theFloorIsLavaManager.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class CiseauxItem extends CustomItem {
    public CiseauxItem() {
        super( "ciseaux",
                "items.ciseaux.display_name",
                "items.ciseaux.lore",
                Rarity.COMMON,
                Material.SHEARS,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack ciseauxItem = createBaseItemStack(audience);
        ciseauxItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.EFFICIENCY,3));
        return ciseauxItem;
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        ShapedRecipe ciseauxRecipe = new ShapedRecipe(key, giveItem(audience));
        ciseauxRecipe.shape(" A","A ");
        ciseauxRecipe.setIngredient('A', Material.IRON_INGOT);
        return ciseauxRecipe;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(10,20,0);
    }
}
