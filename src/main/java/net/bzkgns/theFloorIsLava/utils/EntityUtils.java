package net.bzkgns.theFloorIsLava.utils;

import org.bukkit.entity.Entity;

public class EntityUtils {
    public static void recursivelyRemovePassengers(Entity entity) {
        for (Entity passenger : entity.getPassengers()) {
            recursivelyRemovePassengers(passenger);
            passenger.remove();
        }
    }
}
