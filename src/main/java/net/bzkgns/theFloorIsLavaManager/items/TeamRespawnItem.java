package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TeamRespawnItem extends CustomItem{
    public TeamRespawnItem() {
        super("team_respawn");
    }

    @Override
    public ItemStack giveItem() {
        ItemStack teamRespawnAnchorStack = new ItemStack(Material.RESPAWN_ANCHOR);
        teamRespawnAnchorStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        teamRespawnAnchorStack.setData(DataComponentTypes.ITEM_NAME, Component.text("Ancre de réapparition d'équipe"));
        ItemLore teamRespawnLore = ItemLore.lore().lines(List.of(Component.text("Permet de réapparaître à l'endroit où l'ancre est posée"), Component.text("au lieu du spawn du monde de jeu."))).build();
        teamRespawnAnchorStack.setData(DataComponentTypes.LORE, teamRespawnLore);
        return teamRespawnAnchorStack;
    }

    @Override
    public boolean isItem(ItemStack stack) {
        Component itemName = stack.getData(DataComponentTypes.ITEM_NAME);
        if (itemName == null) {
            return false;
        }
        return stack.getType() == Material.RESPAWN_ANCHOR && itemName.equals(Component.text("Ancre de réapparition d'équipe"));
    }

    @Override
    public CraftingRecipe getRecipe() {

        ShapedRecipe teamRespawnAnchorRecipe = new ShapedRecipe(key, giveItem());
        teamRespawnAnchorRecipe.shape("ABA","BCB","ABA");
        teamRespawnAnchorRecipe.setIngredient('A', Material.DIAMOND);
        teamRespawnAnchorRecipe.setIngredient('B', Material.OBSIDIAN);
        teamRespawnAnchorRecipe.setIngredient('C', Material.ENDER_PEARL);
        return teamRespawnAnchorRecipe;
    }
}
