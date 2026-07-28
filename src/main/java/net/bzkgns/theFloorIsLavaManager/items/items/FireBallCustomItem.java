package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

public class FireBallCustomItem extends CustomItem {
    public FireBallCustomItem() {
        super("fireball_custom",
                "items.fireball.display_name",
                "items.fireball.lore",
                Rarity.RARE,
                Material.FIRE_CHARGE,
                true);
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

    @Override
    public @Nullable Price getPrice() {
        return new Price(20,45,0);
    }
}
