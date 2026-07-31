package net.bzkgns.theFloorIsLava.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
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
public class ShearsItem extends CustomItem {
    private static final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();
    public ShearsItem() {
        super( "shears",
                Rarity.COMMON,
                Material.SHEARS,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack ciseauxItem = createBaseItemStack(audience);
        ciseauxItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.EFFICIENCY,itemsConfig.getShearsEfficiencyLevel()));
        return ciseauxItem;
    }


    @Override
    public @Nullable Price getPrice() {
        return new Price(10,20,0);
    }
}
