package net.bzkgns.theFloorIsLavaManager.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class FireBallItem extends CustomItem {
    public FireBallItem() {
        super("fireball",
                "items.fireball.display_name",
                "items.fireball.lore",
                Rarity.RARE,
                Material.FIRE_CHARGE,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        Plugin firebalPlugin = Bukkit.getPluginManager().getPlugin("ThrowableFireballs");
        if (firebalPlugin == null) {
            Bukkit.getLogger().warning("ThrowableFireballs plugin not found. Fireball item will not be created.");
            return new ItemStack(Material.AIR); // Return an empty item stack if the plugin is not found
        }
        ItemStack fireballItem = ItemStack.of(Material.FIRE_CHARGE);
        ItemMeta fireballMeta = fireballItem.getItemMeta();
        fireballMeta.getPersistentDataContainer().set(new NamespacedKey(firebalPlugin,"throwable_fireballs"), PersistentDataType.STRING, "fireballxyz");
        fireballItem.setItemMeta(fireballMeta);
        fireballItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Fire Ball").color(TextColor.color(255,165,0)));
        return fireballItem;
    }

    @Override
    public boolean isItem(ItemStack stack) {
        Plugin firebalPlugin = Bukkit.getPluginManager().getPlugin("ThrowableFireballs");
        if (firebalPlugin == null) {
            Bukkit.getLogger().warning("ThrowableFireballs plugin not found. Cannot check if item is Fireball.");
            return false; // Return false if the plugin is not found
        }
        if (stack.getType() == Material.FIRE_CHARGE) {
            ItemMeta meta = stack.getItemMeta();
            return meta != null && "fireballxyz".equals(meta.getPersistentDataContainer().get(new NamespacedKey(firebalPlugin, "throwable_fireballs"), PersistentDataType.STRING));
        }
        return false;
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        ItemStack fireballItem = giveItem(audience);
        if (fireballItem.getType() == Material.AIR) {
            return null; // Return null if the item is not created
        }
        fireballItem.setAmount(2);
        ShapelessRecipe fireballRecipe = new ShapelessRecipe(key, fireballItem);
        fireballRecipe.addIngredient(Material.COBBLESTONE);
        fireballRecipe.addIngredient(Material.GUNPOWDER);
        fireballRecipe.addIngredient(Material.IRON_INGOT);
        return fireballRecipe;
    }
}
