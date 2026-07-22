package net.bzkgns.theFloorIsLavaManager.listener;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.items.ParachuteItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public class ParachuteListener implements Listener {
    private static final int PARACHUTE_COOLDOWN = 60;
    private static final int PARACHUTE_EFFECT_DURATION = 60;
    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    @EventHandler
    public void onParachuteUsed(PlayerInteractEvent event){
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (!new ParachuteItem().isItem(itemStack)) return;
        Player player = event.getPlayer();
        if (player.hasCooldown(itemStack)) return;


        player.setCooldown(itemStack, PARACHUTE_EFFECT_DURATION);
        player.setVelocity(player.getVelocity().setY(0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PARACHUTE_COOLDOWN, 1, false, false, false));
    }
}
