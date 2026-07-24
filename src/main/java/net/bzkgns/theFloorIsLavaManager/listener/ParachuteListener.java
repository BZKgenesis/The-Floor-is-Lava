package net.bzkgns.theFloorIsLavaManager.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.ParachuteItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

@SuppressWarnings("UnstableApiUsage")
public class ParachuteListener implements Listener {
    private static final int PARACHUTE_COOLDOWN = 60;
    private static final int PARACHUTE_EFFECT_DURATION = 60;

    @EventHandler
    public void onParachuteUsed(PlayerInteractEvent event){
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (!new ParachuteItem().isItem(itemStack)) return;
        Player player = event.getPlayer();
        if (player.hasCooldown(itemStack)) return;

        ItemDisplay itemDisplay = player.getWorld().spawn(player.getLocation(), ItemDisplay.class);
        ItemStack itemStackDisplay = new ItemStack(Material.FEATHER);
        itemStackDisplay.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("parachute").build());
        itemDisplay.setItemStack(itemStackDisplay);
        player.addPassenger(itemDisplay);
        itemStack.setAmount(itemStack.getAmount()-1);
        int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(),
                () -> itemDisplay.setRotation(player.getYaw(), 0), 0L, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                () -> {
                    player.removePassenger(itemDisplay);
                    itemDisplay.remove();
                    player.playSound(player, Sound.ITEM_LEAD_BREAK, 1, 1);
                    Bukkit.getScheduler().cancelTask(taskID);
                }, PARACHUTE_EFFECT_DURATION);
        player.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
        player.setCooldown(itemStack, PARACHUTE_EFFECT_DURATION);
        player.setVelocity(player.getVelocity().setY(0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PARACHUTE_COOLDOWN, 1, false, false, false));
    }
}
