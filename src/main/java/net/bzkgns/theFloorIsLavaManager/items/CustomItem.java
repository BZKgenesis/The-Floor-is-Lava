package net.bzkgns.theFloorIsLavaManager.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
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
    protected CustomItem(String key, String display_name_translation_key, String description_translation_key, Rarity rarity, Material material, boolean glint){
        this(key, display_name_translation_key, List.of(description_translation_key), rarity, material, glint);
    }

    protected CustomItem(Audience audience, String key, String display_name_translation_key, String description_translation_key, Rarity rarity, Material material, boolean glint){
        if (audience == null) {
            audience = Bukkit.getServer();
        }
        this(audience, key, display_name_translation_key, List.of(description_translation_key), rarity, material, glint);
    }

    protected CustomItem(String key, String display_name_translation_key, List<String> description_translation_keys, Rarity rarity, Material material, boolean glint) {
        Audience audience = Bukkit.getServer();
        this(audience, key, display_name_translation_key, description_translation_keys, rarity, material, glint);
    }

    protected CustomItem(Audience audience, String key, String display_name_translation_key, List<String> description_translation_keys, Rarity rarity, Material material, boolean glint) {
        if (audience == null) {
            audience = Bukkit.getServer();
        }
        itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        TextColor colorName = switch (rarity) {
            case COMMON -> TextColor.fromHexString("#FFFFFF");
            case UNCOMMON -> TextColor.fromHexString("#1EFF00");
            case RARE -> TextColor.fromHexString("#0070DD");
            case EPIC -> TextColor.fromHexString("#A335EE");
            case LEGENDARY -> TextColor.fromHexString("#FF8000");
        };
        meta.displayName(Messages.component(audience, display_name_translation_key).color(colorName));
        Audience finalAudience = audience;
        List<Component> lore_text = description_translation_keys.stream().map(line -> Messages.component(finalAudience,line).color(TextColor.fromHexString("#AAAAAA"))).toList();
        meta.lore(lore_text);
        meta.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLavaManager.getInstance(), "key"), PersistentDataType.STRING, key);
        itemStack.setItemMeta(meta);
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        this.key = new NamespacedKey(plugin,key);
    }
    public abstract ItemStack giveItem();
    public boolean isItem(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "key"), PersistentDataType.STRING)) {
            return false;
        }
        String itemKey = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "key"), PersistentDataType.STRING);
        return key.getKey().equals(itemKey) && stack.getType() == itemStack.getType();
    }
    public abstract CraftingRecipe getRecipe();


    public String getKey(){
        return key.getKey();
    }
}
