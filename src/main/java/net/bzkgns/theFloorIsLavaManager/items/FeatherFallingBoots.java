package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.RESOURCE_MATERIALS;

@SuppressWarnings("UnstableApiUsage")
public class FeatherFallingBoots extends CustomItem{
    public FeatherFallingBoots() {
        super("feather_falling_boots",
                "items.feather_falling_boots.display_name",
                "items.feather_falling_boots.lore",
                Rarity.RARE,
                Material.LEATHER_BOOTS,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack item = createBaseItemStack(audience);
        item.setData(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.itemAttributes().addModifier(
                        Attribute.GRAVITY,
                        new AttributeModifier(
                                new NamespacedKey(TheFloorIsLavaManager.getInstance(), "feather_falling"),
                                -0.5,
                                AttributeModifier.Operation.ADD_SCALAR)));
        item.setData(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments().add(Enchantment.FEATHER_FALLING, 1));
        return item;
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        ShapedRecipe eggBridgeRecipe = new ShapedRecipe(key, giveItem(audience));
        eggBridgeRecipe.shape("A A","ABA");
        eggBridgeRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(Material.IRON_INGOT));
        eggBridgeRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(RESOURCE_MATERIALS));
        return eggBridgeRecipe;
    }
}
