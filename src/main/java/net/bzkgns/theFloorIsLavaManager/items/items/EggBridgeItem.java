package net.bzkgns.theFloorIsLavaManager.items.items;

import net.bzkgns.theFloorIsLavaManager.currency.Price;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.CustomItem;
import net.bzkgns.theFloorIsLavaManager.tasks.EggBridgeTask;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.RESOURCE_MATERIALS;
import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.WOOLS_MATERIALS;

public class EggBridgeItem extends CustomItem {

    private static int eggBridgeTask = -1;

    public EggBridgeItem() {
        if (eggBridgeTask == -1) {
            eggBridgeTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(),
                    new EggBridgeTask(),
                    0L,
                    1L
            );
        }
        super("egg_bridge",
                "items.egg_bridge.display_name",
                "items.egg_bridge.lore",
                Rarity.RARE,
                Material.EGG,
                true
        );

    }


    @Override
    public CraftingRecipe getRecipe(Audience audience) {
        ShapedRecipe eggBridgeRecipe = new ShapedRecipe(key, giveItem(audience));
        eggBridgeRecipe.shape("AAA","ABA","AAA");
        eggBridgeRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(WOOLS_MATERIALS));
        eggBridgeRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(RESOURCE_MATERIALS));
        return eggBridgeRecipe;
    }

    @Override
    public @Nullable Price getPrice() {
        return new Price(30,45,0);
    }
}
