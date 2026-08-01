package net.bzkgns.theFloorIsLava.items.abilities.gambling;

import java.util.List;
import java.util.UUID;

public class GamblingManager {
    private static GamblingManager instance;

    private final List<UUID> playersInGambling = new java.util.ArrayList<>();

    public static GamblingManager getInstance() {
        if (instance == null) {
            instance = new GamblingManager();
        }
        return instance;
    }

    public boolean isPlayerInGambling(UUID playerUUID) {
        return playersInGambling.contains(playerUUID);
    }

    public void addPlayerToGambling(UUID playerUUID) {
        if (!isPlayerInGambling(playerUUID)) {
            playersInGambling.add(playerUUID);
        }
    }

    public void removePlayerFromGambling(UUID playerUUID) {
        playersInGambling.remove(playerUUID);
    }
}
