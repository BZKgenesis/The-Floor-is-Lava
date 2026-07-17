package net.bzkgns.theFloorIsLavaManager.kits;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class KitData {

    private final String name;
    private final String displayName;
    private final ItemStack[] armor;
    private final List<ItemStack> items;
    private final List<AttributeModifier> attributeModifiers;
    private final List<PotionEffect> effects;
    private final List<String> tags;
    private final Runnable potionEffectTaskRunnable;
    private BukkitTask potionEffectTaskInstance;

    public KitData(String name, String displayName) {
        this(name, displayName, List.of(), List.of(), List.of());
    }

    public KitData(String name, String displayName, List<ItemStack> armor, List<ItemStack> items) {
        this(name, displayName, armor, items, List.of());
    }

    public KitData(String name, String displayName, List<ItemStack> armor, List<ItemStack> items, AttributeModifier... attributeModifiers) {
        this(name, displayName, armor, items, List.of(attributeModifiers));
    }

    public KitData(String name, String displayName, List<ItemStack> armor, List<ItemStack> items, List<AttributeModifier> attributeModifiers) {
        this(name, displayName, armor, items, attributeModifiers, List.of(), List.of());
    }
    public KitData(String name, String displayName, List<ItemStack> armor, List<ItemStack> items, List<AttributeModifier> attributeModifiers, String tag){
        this(name, displayName, armor, items, attributeModifiers, List.of(tag), List.of());
    }
    @SuppressWarnings("UnstableApiUsage")
    public KitData(String name, String displayName, List<ItemStack> armor, List<ItemStack> items, List<AttributeModifier> attributeModifiers, List<String> tags, List<PotionEffect> effects) {
        ItemStack[] armorItem = new ItemStack[4];
        for (int i = 0; i < armor.size() && i < 4; i++) {
            if (armor.get(i) != null){
                Equippable equippable = armor.get(i).getData(DataComponentTypes.EQUIPPABLE);
                if (equippable == null) {
                    continue;
                }
                EquipmentSlot slot = equippable.slot();
                if (slot == org.bukkit.inventory.EquipmentSlot.HEAD){
                    armorItem[3] = armor.get(i);
                } else if (slot == org.bukkit.inventory.EquipmentSlot.CHEST){
                    armorItem[2] = armor.get(i);
                } else if (slot == org.bukkit.inventory.EquipmentSlot.LEGS){
                    armorItem[1] = armor.get(i);
                } else if (slot == org.bukkit.inventory.EquipmentSlot.FEET){
                    armorItem[0] = armor.get(i);
                }
            }
        }
        this.name = name;
        this.displayName = displayName;
        this.armor = armorItem;
        this.items = items;
        this.attributeModifiers = attributeModifiers;
        this.tags = List.copyOf(tags);
        this.effects = List.copyOf(effects);
        if (!effects.isEmpty() && !tags.isEmpty()) {
            this.potionEffectTaskRunnable = this::applyPotionEffectsToTaggedPlayers;
            startPotionEffectTask();
        } else {
            this.potionEffectTaskRunnable = null;
        }
    }

    private void applyPotionEffectsToTaggedPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboardTags().containsAll(tags)) {
                for (PotionEffect effect : effects) {
                    if (!player.hasPotionEffect(effect.getType())) {
                        player.addPotionEffect(effect);
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void applyToPlayerNoClear(Player player) {
        player.getInventory().setArmorContents(new ItemStack[4]);

        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(), () -> player.getInventory().setArmorContents(armor), 2L);

        for (ItemStack item : items) {
            player.getInventory().addItem(item);
        }
        applyToPlayerAttributeOnly(player);
    }

    public void applyToPlayerAttributeOnly(Player player) {
        removeFromPlayerAttributeOnly(player);

        for (String tag : tags){
            player.addScoreboardTag(tag);
        }
        for (AttributeModifier modifier : attributeModifiers) {
            Attribute attr = Registry.ATTRIBUTE.get(modifier.getKey());
            if (attr != null) {
                AttributeInstance attributeInstance = player.getAttribute(attr);
                if (attributeInstance != null) {
                    attributeInstance.addModifier(modifier);
                }
            }
        }
    }

    public void removeFromPlayerAttributeOnly(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (player.hasPotionEffect(effect.getType())) {
                player.removePotionEffect(effect.getType());
            }
        }
        for (String tag : tags){
            player.removeScoreboardTag(tag);
        }
        for (AttributeModifier modifier : attributeModifiers) {
            Attribute attr = Registry.ATTRIBUTE.get(modifier.getKey());
            if (attr != null) {
                AttributeInstance attributeInstance = player.getAttribute(attr);
                if (attributeInstance != null) {
                    attributeInstance.removeModifier(modifier);
                }
            }
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public void startPotionEffectTask() {
        cancelPotionEffectTask();
        if (potionEffectTaskRunnable != null) {
            this.potionEffectTaskInstance = Bukkit.getScheduler().runTaskTimer(TheFloorIsLavaManager.getInstance(), potionEffectTaskRunnable, 0L, 20L);
        }
    }

    public void cancelPotionEffectTask() {
        if (potionEffectTaskInstance != null) {
            potionEffectTaskInstance.cancel();
            potionEffectTaskInstance = null;
        }
    }

    public String toString() {
        return "KitData{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", armor=" + java.util.Arrays.toString(armor) +
                ", items=" + items +
                ", attributeModifiers=" + attributeModifiers +
                ", effects=" + effects +
                ", tags=" + tags +
                '}';
    }



}
