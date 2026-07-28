package net.bzkgns.theFloorIsLavaManager.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ThrowableIronGolemItem extends CustomItem {
    public ThrowableIronGolemItem() {
        super("throwable_iron_golem",
                "items.throwable_iron_golem.display_name",
                "items.throwable_iron_golem.lore",
                Rarity.LEGENDARY,
                Material.EGG,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack itemStack = createBaseItemStack(audience);
        NamespacedKey key = Registry.ITEM.getKey(ItemType.IRON_GOLEM_SPAWN_EGG);
        if (key == null) {
            throw new IllegalStateException("Could not find key for IRON_GOLEM_SPAWN_EGG");
        }
        itemStack.setData(DataComponentTypes.ITEM_MODEL, key);
        return itemStack;
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(50,50,0);
    }
}
