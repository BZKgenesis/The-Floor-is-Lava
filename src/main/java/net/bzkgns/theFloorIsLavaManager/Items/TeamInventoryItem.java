package net.bzkgns.theFloorIsLavaManager.Items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class TeamInventoryItem extends CustomItem {
    public TeamInventoryItem() {
        super("teamInv");
    }

    @Override
    public ItemStack giveItem(){
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"teamInv"), PersistentDataType.STRING, "teamInv");
        item.setItemMeta(meta);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Portail d'inventaire d'équipe"));
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return item;
    }

    @Override
    public boolean isItem(ItemStack stack){
        if (stack.getType() == Material.ENDER_CHEST){
            if (stack.getPersistentDataContainer().has(new NamespacedKey(plugin,"teamInv"))){
                return Objects.equals(stack.getPersistentDataContainer().get(new NamespacedKey(plugin, "teamInv"), PersistentDataType.STRING), "teamInv");
            }
        }
        return false;
    }

    @Override
    public CraftingRecipe getRecipe() {

        ShapedRecipe teamInvRecipe = new ShapedRecipe(key, giveItem());
        teamInvRecipe.shape("ABA","BCB","ABA");
        teamInvRecipe.setIngredient('A', Material.DIAMOND);
        teamInvRecipe.setIngredient('B', Material.IRON_INGOT);
        teamInvRecipe.setIngredient('C', Material.CHEST);
        return teamInvRecipe;
    }
}
