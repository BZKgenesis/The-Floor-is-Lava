package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomItem {
    protected final NamespacedKey key;
    protected final TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
    protected final ItemStack itemStack;

    public enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }
    protected CustomItem(String key, String name, String description, Rarity rarity, Material material, boolean glint){
        this(key, name, List.of(description), rarity, material, glint);
    }

    protected CustomItem(String key, String name, List<String> description, Rarity rarity, Material material, boolean glint) {
        itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        TextColor colorName = switch (rarity) {
            case COMMON -> TextColor.fromHexString("#FFFFFF");
            case UNCOMMON -> TextColor.fromHexString("#1EFF00");
            case RARE -> TextColor.fromHexString("#0070DD");
            case EPIC -> TextColor.fromHexString("#A335EE");
            case LEGENDARY -> TextColor.fromHexString("#FF8000");
        };
        meta.displayName(Component.text(name).color(colorName));
        List<TextComponent> lore_text = description.stream().map(line -> Component.text(line).color(TextColor.fromHexString("#AAAAAA"))).toList();
        meta.lore(lore_text);
        meta.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLavaManager.getInstance(), "key"), PersistentDataType.STRING, key);
        itemStack.setItemMeta(meta);
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        this.key = new NamespacedKey(plugin,key);
    }
    public abstract ItemStack giveItem();
    public boolean isItem(ItemStack stack){
        String itemKey = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "key"), PersistentDataType.STRING);
        return key.getKey().equals(itemKey) && stack.getType() == itemStack.getType();
    }
    public abstract CraftingRecipe getRecipe();


    public String getKey(){
        return key.getKey();
    }
}
