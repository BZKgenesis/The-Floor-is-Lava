package net.bzkgns.theFloorIsLava.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.Weapon;
import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.currency.Price;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class BatteItem extends CustomItem {
    private static final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();
    public BatteItem() {
        super("batte",
                "items.batte.display_name",
                "items.batte.lore",
                Rarity.COMMON,
                Material.STICK,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack batteItem = createBaseItemStack(audience);
        batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, itemsConfig.getBatteKnockbackLevel()));
        batteItem.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        batteItem.setData(DataComponentTypes.WEAPON, Weapon.weapon());
        return batteItem;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(0,45,0);
    }
}
