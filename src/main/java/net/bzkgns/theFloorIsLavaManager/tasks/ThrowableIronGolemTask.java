package net.bzkgns.theFloorIsLavaManager.tasks;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.IronGolem;
import org.bukkit.persistence.PersistentDataType;

public class ThrowableIronGolemTask implements Runnable{
    private static final float DAMAGE_PER_TICK = 0.1f;
    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
    @Override
    public void run() {
        plugin.getServer().getWorlds().forEach(world -> world.getEntities().stream()
                .filter(entity -> entity instanceof IronGolem && entity.getPersistentDataContainer().has(
                        new org.bukkit.NamespacedKey(plugin, "ironGolemEntity")
                )).map(entity -> (IronGolem) entity)
                .forEach(e -> {
                    if (e.getHealth() <= DAMAGE_PER_TICK){
                        e.kill();
                        return;
                    }
                    e.setHealth(e.getHealth()-DAMAGE_PER_TICK);
                    String name = e.getPersistentDataContainer().get(
                            new org.bukkit.NamespacedKey(plugin, "ironGolemEntity"), PersistentDataType.STRING
                    );

                    Integer colorValue = e.getPersistentDataContainer().get(
                            new org.bukkit.NamespacedKey(plugin, "ironGolemEntityColor"), PersistentDataType.INTEGER
                    );
                    TextColor color = colorValue==null?NamedTextColor.WHITE: TextColor.color(colorValue);
                    e.customName(Component.text(name==null?"":name, color).append( Component.text(" " + (int) e.getHealth() + "❤", NamedTextColor.RED)));
                })
        );
    }
}
