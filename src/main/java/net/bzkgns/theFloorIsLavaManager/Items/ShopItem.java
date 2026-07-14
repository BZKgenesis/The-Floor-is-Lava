package net.bzkgns.theFloorIsLavaManager.Items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static net.bzkgns.theFloorIsLavaManager.Utils.TextUtils.plainText;

@SuppressWarnings("UnstableApiUsage")
public class ShopItem extends CustomItem{
    public ShopItem() {
        super("shop_item");
    }

    @Override
    public ItemStack giveItem() {
        ItemStack it = new ItemStack(Material.BOOK);
        it.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Component.text("Shop").color(TextColor.fromHexString("#FFAA00")));
        meta.lore(List.of(Component.text("Ouvre le menu du shop").color(TextColor.fromHexString("#AAAAAA"))));
        it.setItemMeta(meta);
        return it;
    }

    @Override
    public boolean isItem(ItemStack stack) {
        if (stack.getType() == Material.BOOK) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String name = plainText(meta.displayName());
                return name.contains("Shop");
            }
        }
        return false;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return null;
    }
}
