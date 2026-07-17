package net.bzkgns.theFloorIsLavaManager.kits;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

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
                        "Default Kit"
                )
        );
        kits.put("miner",
                new KitData(
                        "miner",
                        "Miner Kit",
                        List.of(new ItemStack(Material.IRON_HELMET),new ItemStack(Material.IRON_CHESTPLATE)),
                        List.of( new ItemStack(Material.STONE, 64), new ItemStack(Material.BREAD,64)),
                        List.of(),
                        List.of("tfl_kit_miner"),
                        List.of(
                                new PotionEffect(PotionEffectType.HASTE, -1, 1, false, false, false)
                        )
                )
        );
        kits.put("sac_a_pv",
                new KitData(
                        "sac_a_pv",
                        "Sac à PV Kit",
                        List.of(new ItemStack(Material.IRON_HELMET),new ItemStack(Material.IRON_CHESTPLATE)),
                        List.of( new ItemStack(Material.STONE, 64), new ItemStack(Material.BREAD,64)),
                        List.of(
                                new AttributeModifier(
                                        Objects.requireNonNull(Registry.ATTRIBUTE.getKey(Attribute.MAX_HEALTH)),
                                        10.0,
                                        AttributeModifier.Operation.ADD_NUMBER
                                )
                        )
                )
        );
    }

    public void applyKitToPlayer(Player player) {
        TheFloorIsLavaManager.getInstance().getLogger().info("Applying kit to player " + player.getUniqueId());
        resetPlayerAttributes(player);
        KitData kit = playerKits.get(player.getUniqueId());
        if (kit != null) {
            kit.applyToPlayerNoClear(player);
        }else{
            TheFloorIsLavaManager.getInstance().getLogger().warning("No kit assigned to player " + player.getUniqueId() + ". Applying default kit.");
            KitData defaultKit = kits.get(DEFAULT_KIT_NAME);
            if (defaultKit != null) {
                defaultKit.applyToPlayerNoClear(player);
            } else {
                TheFloorIsLavaManager.getInstance().getLogger().severe("Default kit not found. Player " + player.getUniqueId() + " will not receive any kit.");
            }
        }
    }

    public void applyKitToPlayerAttributeOnly(Player player) {
        resetPlayerAttributes(player);
        KitData kit = playerKits.get(player.getUniqueId());
        if (kit != null) {
            kit.applyToPlayerNoClear(player);
        }
    }

    public void assignKitToPlayer(UUID playerUUID, String kitName) {
        TheFloorIsLavaManager.getInstance().getLogger().info("Assigning kit " + kitName + " to player " + playerUUID);
        KitData kit = kits.get(kitName);
        if (kit != null) {
            playerKits.put(playerUUID, kit);
        }else{
            TheFloorIsLavaManager.getInstance().getLogger().warning("Kit " + kitName + " not found. Assigning default kit.");
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

    public List<String> getKitNames() {
        return new ArrayList<>(kits.keySet());
    }

    public KitData getKit(String kitName) {
        return kits.get(kitName);
    }
}
