package net.bzkgns.theFloorIsLava.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FeatherFallingBootsItem extends CustomItem {
    private static final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();
    public FeatherFallingBootsItem() {
        super("feather_falling_boots",
                "items.feather_falling_boots.display_name",
                "items.feather_falling_boots.lore",
                Rarity.RARE,
                Material.IRON_BOOTS,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack item = createBaseItemStack(audience);
        ItemAttributeModifiers.Builder itemAttribute = ItemAttributeModifiers.itemAttributes();

        itemAttribute.addModifier(
            Attribute.GRAVITY,
            new AttributeModifier(
                    new NamespacedKey(TheFloorIsLava.getInstance(), "feather_falling"),
                    itemsConfig.getFeatherFallingBootsGravity(),
                    AttributeModifier.Operation.ADD_SCALAR), EquipmentSlotGroup.FEET);
        itemAttribute.addModifier(
                        Attribute.JUMP_STRENGTH,
                        new AttributeModifier(
                                new NamespacedKey(TheFloorIsLava.getInstance(), "jump_boost"),
                                itemsConfig.getFeatherFallingBootsJumpStrength(),
                                AttributeModifier.Operation.ADD_SCALAR), EquipmentSlotGroup.FEET);
        itemAttribute.addModifier(
                        Attribute.SAFE_FALL_DISTANCE,
                        new AttributeModifier(
                                new NamespacedKey(TheFloorIsLava.getInstance(), "fall_distance"),
                                itemsConfig.getFeatherFallingBootsSafeFallDistance(),
                                AttributeModifier.Operation.ADD_SCALAR), EquipmentSlotGroup.FEET);
        item.setData(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                itemAttribute.build());
        item.setData(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments().add(Enchantment.FEATHER_FALLING, itemsConfig.getFeatherFallingBootsEnchantmentLevel()));
        item.setData(
                DataComponentTypes.MAX_STACK_SIZE,
                1);
        return item;
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(10,35,0);
    }
}
