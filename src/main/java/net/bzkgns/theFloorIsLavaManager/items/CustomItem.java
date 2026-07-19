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
    protected final String display_name_translation_key;
    protected final List<String> description_translation_keys;
    protected final Rarity rarity;
    protected final Material material;
    protected final boolean glint;
    protected final TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

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

    protected CustomItem(String key, String display_name_translation_key, List<String> description_translation_keys, Rarity rarity, Material material, boolean glint) {
        this.key = new NamespacedKey(plugin,key);
        this.display_name_translation_key = display_name_translation_key;
        this.description_translation_keys = description_translation_keys;
        this.rarity = rarity;
        this.material = material;
        this.glint = glint;
    }

    protected ItemStack createBaseItemStack(Audience audience){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        TextColor colorName = switch (rarity) {
            case COMMON -> TextColor.fromHexString("#FFFFFF");
            case UNCOMMON -> TextColor.fromHexString("#1EFF00");
            case RARE -> TextColor.fromHexString("#0070DD");
            case EPIC -> TextColor.fromHexString("#A335EE");
            case LEGENDARY -> TextColor.fromHexString("#FF8000");
        };
        meta.displayName(Messages.component(audience, display_name_translation_key).color(colorName));
        List<Component> lore_text = description_translation_keys.stream().map(line -> Messages.component(audience,line).color(TextColor.fromHexString("#AAAAAA"))).toList();
        meta.lore(lore_text);
        meta.getPersistentDataContainer().set(new NamespacedKey(TheFloorIsLavaManager.getInstance(), "key"), PersistentDataType.STRING, key.getKey());
        itemStack.setItemMeta(meta);
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return itemStack;
    }

    public ItemStack giveItem(){
        return giveItem(Bukkit.getServer());
    }

    public ItemStack giveItem(Audience audience){
        return createBaseItemStack(audience);
    }
    public boolean isItem(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "key"), PersistentDataType.STRING)) {
            return false;
        }
        String itemKey = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "key"), PersistentDataType.STRING);
        return key.getKey().equals(itemKey) && stack.getType() == material;
    }
    public abstract CraftingRecipe getRecipe(Audience audience);


    public String getKey(){
        return key.getKey();
    }
}
