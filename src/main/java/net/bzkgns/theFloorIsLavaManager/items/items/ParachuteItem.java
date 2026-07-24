package net.bzkgns.theFloorIsLavaManager.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ParachuteItem extends CustomItem {
    public ParachuteItem() {
        super("parachute",
                "items.parachute.display_name",
                "items.parachute.lore",
                Rarity.RARE,
                Material.FEATHER,
                true);
    }

    @Override
    public ItemStack giveItem(Audience audience) {
        ItemStack itemStack = super.createBaseItemStack(audience);
        itemStack.setData(
                DataComponentTypes.MAX_STACK_SIZE,
                1
        );
        return itemStack;
    }

    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        return null;
    }
}
