package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TheFloorIsLavaCrafts {

    public void setCrafts(Plugin plugin){
        List<Material> ressourceMaterials = List.of(Material.DIAMOND,Material.GOLD_INGOT,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.AMETHYST_SHARD);
        List<Material> woolMaterials = List.of(Material.WHITE_WOOL,Material.ORANGE_WOOL,Material.MAGENTA_WOOL,Material.LIGHT_BLUE_WOOL,Material.YELLOW_WOOL,Material.LIME_WOOL,Material.PINK_WOOL,Material.GRAY_WOOL,Material.LIGHT_GRAY_WOOL,Material.CYAN_WOOL,Material.PURPLE_WOOL,Material.BLUE_WOOL,Material.BROWN_WOOL,Material.GREEN_WOOL,Material.RED_WOOL,Material.BLACK_WOOL);


        NamespacedKey batteKey = new NamespacedKey(plugin, "batte");
        ItemStack batteItem = ItemStack.of(Material.STICK);
            batteItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Batte"));
            batteItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 3));
            batteItem.setData(DataComponentTypes.MAX_DAMAGE, 10);
            batteItem.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
            batteItem.setData(DataComponentTypes.WEAPON, Weapon.weapon());

        ShapedRecipe batteRecipe = new ShapedRecipe(batteKey, batteItem);
            batteRecipe.shape("  A", " B ", "C  ");
            batteRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(ressourceMaterials));
            batteRecipe.setIngredient('B', Material.IRON_INGOT);
            batteRecipe.setIngredient('C', Material.STICK);
        plugin.getServer().addRecipe(batteRecipe);


        NamespacedKey eggBridgeKey = new NamespacedKey(plugin, "eggBridge");
        ItemStack eggBridgeItem = EggBridge.giveEggBridgeItem();

        ShapedRecipe eggBridgeRecipe = new ShapedRecipe(eggBridgeKey, eggBridgeItem);
            eggBridgeRecipe.shape("AAA","ABA","AAA");
            eggBridgeRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(woolMaterials));
            eggBridgeRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(ressourceMaterials));
        plugin.getServer().addRecipe(eggBridgeRecipe);


        NamespacedKey snowballPlateKey = new NamespacedKey(plugin, "snowballPlate");
        ItemStack snowballPlateItem = SnowballPlate.giveSnowballPlate();

        ShapedRecipe snowballPlateRecipe = new ShapedRecipe(snowballPlateKey, snowballPlateItem);
        snowballPlateRecipe.shape("AAA","ABA","AAA");
        snowballPlateRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(woolMaterials));
        snowballPlateRecipe.setIngredient('B', Material.IRON_INGOT);
        plugin.getServer().addRecipe(snowballPlateRecipe);


        NamespacedKey patateKey = new NamespacedKey(plugin, "patate");
        ItemStack patateItem = ItemStack.of(Material.BAKED_POTATO);

        ShapelessRecipe patateRecipe = new ShapelessRecipe(patateKey, patateItem);
            patateRecipe.addIngredient( new RecipeChoice.MaterialChoice(Material.DIRT,Material.SAND,Material.RED_SAND));
        plugin.getServer().addRecipe(patateRecipe);

        Plugin firebalPlugin = Bukkit.getPluginManager().getPlugin("ThrowableFireballs");
        if (firebalPlugin!=null){
            NamespacedKey fireballKey = new NamespacedKey(plugin, "fireball");
            ItemStack fireballItem = ItemStack.of(Material.FIRE_CHARGE,2);
                ItemMeta fireballMeta = fireballItem.getItemMeta();
                fireballMeta.getPersistentDataContainer().set(new NamespacedKey(firebalPlugin,"throwable_fireballs"), PersistentDataType.STRING, "fireballxyz");
                fireballItem.setItemMeta(fireballMeta);
                fireballItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Fire Ball").color(TextColor.color(255,165,0)));

            ShapelessRecipe fireballRecipe = new ShapelessRecipe(fireballKey, fireballItem);
                fireballRecipe.addIngredient(Material.COBBLESTONE);
                fireballRecipe.addIngredient(Material.GUNPOWDER);
            plugin.getServer().addRecipe(fireballRecipe);
        }


        NamespacedKey blocksKey = new NamespacedKey(plugin, "blocs_en_plus");
        ItemStack blocksItem = ItemStack.of(Material.GRAY_WOOL,8);

        ShapelessRecipe blocksRecipe = new ShapelessRecipe(blocksKey, blocksItem);
            blocksRecipe.addIngredient(Material.COBBLESTONE);
        plugin.getServer().addRecipe(blocksRecipe);


        NamespacedKey ciseauxKey = new NamespacedKey(plugin, "ciseaux");
        ItemStack ciseauxItem = ItemStack.of(Material.SHEARS);
            ciseauxItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Ciseaux"));
            ciseauxItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.EFFICIENCY,3));

        ShapedRecipe ciseauxRecipe = new ShapedRecipe(ciseauxKey, ciseauxItem);
            ciseauxRecipe.shape(" A","A ");
            ciseauxRecipe.setIngredient('A', Material.IRON_INGOT);
        plugin.getServer().addRecipe(ciseauxRecipe);


        NamespacedKey enderPearlKey = new NamespacedKey(plugin, "enderPearl");
        ItemStack enderPearlItem = ItemStack.of(Material.ENDER_PEARL);

        ShapedRecipe enderPearlRecipe = new ShapedRecipe(enderPearlKey, enderPearlItem);
            enderPearlRecipe.shape("BA","AB");
            enderPearlRecipe.setIngredient('A', Material.IRON_INGOT);
            enderPearlRecipe.setIngredient('B', new RecipeChoice.MaterialChoice(ressourceMaterials));
        plugin.getServer().addRecipe(enderPearlRecipe);


        NamespacedKey popupTowerlKey = new NamespacedKey(plugin, "popupTower");
        ItemStack popupTowerItem = PopupTower.givePopupTower();

        ShapedRecipe popupTowerRecipe = new ShapedRecipe(popupTowerlKey, popupTowerItem);
            popupTowerRecipe.shape("ABA","BCB","ABA");
            popupTowerRecipe.setIngredient('A', new RecipeChoice.MaterialChoice(woolMaterials));
            popupTowerRecipe.setIngredient('B', Material.IRON_INGOT);
            popupTowerRecipe.setIngredient('C', Material.CHEST);
        plugin.getServer().addRecipe(popupTowerRecipe);


        NamespacedKey teamInvlKey = new NamespacedKey(plugin, "teamInv");
        ItemStack teamInvItem = TeamInventoryManager.getInstance().getTeamInventoryItem();

        ShapedRecipe teamInvRecipe = new ShapedRecipe(teamInvlKey, teamInvItem);
        teamInvRecipe.shape("ABA","BCB","ABA");
        teamInvRecipe.setIngredient('A', Material.DIAMOND);
        teamInvRecipe.setIngredient('B', Material.IRON_INGOT);
        teamInvRecipe.setIngredient('C', Material.CHEST);
        plugin.getServer().addRecipe(teamInvRecipe);

    }




}
