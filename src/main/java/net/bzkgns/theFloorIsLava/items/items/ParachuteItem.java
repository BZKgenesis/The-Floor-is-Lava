package net.bzkgns.theFloorIsLava.items.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLava.items.CustomItem;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ParachuteItem extends CustomItem {
    public ParachuteItem() {
        super("parachute",
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
}
