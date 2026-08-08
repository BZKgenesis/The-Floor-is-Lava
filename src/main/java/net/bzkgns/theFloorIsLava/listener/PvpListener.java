package net.bzkgns.theFloorIsLava.listener;

import net.bzkgns.theFloorIsLava.game.PvpManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PvpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player && e.getDamageSource().getCausingEntity()!=null){
            if (e.getDamageSource().getCausingEntity() instanceof Player){
                if (!PvpManager.isPvpEnabled()){
                    e.setCancelled(true);
                }
            }
        }
    }
}
