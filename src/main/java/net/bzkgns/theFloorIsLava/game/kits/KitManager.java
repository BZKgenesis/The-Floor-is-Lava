package net.bzkgns.theFloorIsLava.game.kits;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class KitManager {
    private static KitManager instance;
    private final Map<String,KitData> kits = new HashMap<>();
    private final Map<UUID, KitData> playerKits = new HashMap<>();
    private static final String DEFAULT_KIT_NAME = "default";

    private KitManager() {
        loadKits();
        // Private constructor to prevent instantiation
    }

    public static KitManager getInstance() {
        if (instance == null) {
            instance = new KitManager();
        }
        return instance;
    }

    public void loadKits() {
        kits.clear();

        kits.put("default",
                new KitData(
                        "default",
                        "Default Kit",
                        List.of(
                                new ItemStack(Material.LEATHER_LEGGINGS),
                                new ItemStack(Material.LEATHER_BOOTS)),
                        List.of()
                )
        );
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        pickaxe.setData(DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments()
                        .add(Enchantment.EFFICIENCY, 3)
                        .add(Enchantment.UNBREAKING, 1));
        kits.put("miner",
                new KitData(
                        "miner",
                        "Miner Kit",
                        List.of(new ItemStack(Material.IRON_HELMET),
                                new ItemStack(Material.IRON_CHESTPLATE),
                                new ItemStack(Material.LEATHER_LEGGINGS),
                                new ItemStack(Material.LEATHER_BOOTS)),
                        List.of( pickaxe, new ItemStack(Material.TORCH,64)),
                        List.of(
                                new AttributeModifier(
                                        Objects.requireNonNull(Registry.ATTRIBUTE.getKey(Attribute.MAX_HEALTH)),
                                        -4.0,
                                        AttributeModifier.Operation.ADD_NUMBER
                                )
                        ),
                        List.of("tfl_kit_miner"),
                        List.of(
                                new PotionEffect(PotionEffectType.HASTE, -1, 0, false, false, false)
                        )
                )
        );
        kits.put("tank",
                new KitData(
                        "tank",
                        "Sac à PV Kit",
                        List.of(
                                new ItemStack(Material.LEATHER_LEGGINGS),
                                new ItemStack(Material.LEATHER_BOOTS)),
                        List.of( new ItemStack(Material.IRON_SWORD, 1), new ItemStack(Material.BREAD,64)),
                        List.of(
                                new AttributeModifier(
                                        Objects.requireNonNull(Registry.ATTRIBUTE.getKey(Attribute.MAX_HEALTH)),
                                        5.0,
                                        AttributeModifier.Operation.ADD_NUMBER
                                )
                        ),
                        List.of("tfl_kit_"),
                        List.of(new PotionEffect(PotionEffectType.SLOWNESS, -1, 0, false, false, false))

                )
        );
    }

    public void applyKitToPlayer(Player player) {
        TheFloorIsLava.getInstance().getLogger().info("Applying kit to player " + player.getUniqueId());
        resetPlayerAttributes(player);
        KitData kit = playerKits.get(player.getUniqueId());
        if (kit != null) {
            kit.applyToPlayerNoClear(player);
        }else{
            TheFloorIsLava.getInstance().getLogger().warning("No kit assigned to player " + player.getUniqueId() + ". Applying default kit.");
            KitData defaultKit = kits.get(DEFAULT_KIT_NAME);
            if (defaultKit != null) {
                defaultKit.applyToPlayerNoClear(player);
            } else {
                TheFloorIsLava.getInstance().getLogger().severe("Default kit not found. Player " + player.getUniqueId() + " will not receive any kit.");
            }
        }

        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute == null) return;

        player.setHealth(healthAttribute.getValue());

    }

    public void applyKitToPlayerAttributeOnly(Player player) {
        resetPlayerAttributes(player);
        KitData kit = playerKits.get(player.getUniqueId());
        if (kit != null) {
            kit.applyToPlayerNoClear(player);
        }
    }

    public void assignKitToPlayer(UUID playerUUID, String kitName) {
        TheFloorIsLava.getInstance().getLogger().info("Assigning kit " + kitName + " to player " + playerUUID);
        KitData kit = kits.get(kitName);
        if (kit != null) {
            playerKits.put(playerUUID, kit);
        }else{
            TheFloorIsLava.getInstance().getLogger().warning("Kit " + kitName + " not found. Assigning default kit.");
            playerKits.put(playerUUID, kits.get(DEFAULT_KIT_NAME));
        }
    }

    public List<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        for (KitData kit : kits.values()) {
            allTags.addAll(kit.getTags());
        }
        return new ArrayList<>(allTags);
    }

    public void resetPlayerAttributes(Player player) {
        for (Attribute attribute : Registry.ATTRIBUTE) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            // Copie obligatoire pour éviter ConcurrentModificationException
            for (AttributeModifier modifier : List.copyOf(instance.getModifiers())) {
                instance.removeModifier(modifier);
            }
        }

        getAllTags().forEach(player::removeScoreboardTag);
    }

    public Map<String,KitData> getAllKits() {
        return new HashMap<>(kits);
    }

    public Map<UUID, KitData> getPlayerKits() {
        return new HashMap<>(playerKits);
    }

    public Map<Player, KitData> getOnlinePlayersKits() {
        Map<Player, KitData> onlinePlayersKits = new HashMap<>();
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            KitData kit = playerKits.get(player.getUniqueId());
            if (kit != null) {
                onlinePlayersKits.put(player, kit);
            }
        }
        return onlinePlayersKits;
    }

    public void clearPlayerKit(Player player) {
        KitData kit = playerKits.remove(player.getUniqueId());
        if (kit != null) {
            kit.removeFromPlayerAttributeOnly(player);
        }
    }

    public void clearAllPlayerKits() {
        for (UUID playerUUID : new HashSet<>(playerKits.keySet())) {
            Player player = org.bukkit.Bukkit.getPlayer(playerUUID);
            if (player != null) {
                clearPlayerKit(player);
            }
        }
    }

    public KitData getPlayerKit(Player player){
        return playerKits.get(player.getUniqueId());
    }

    public List<String> getKitNames() {
        return new ArrayList<>(kits.keySet());
    }

    public KitData getKit(String kitName) {
        return kits.get(kitName);
    }
}
