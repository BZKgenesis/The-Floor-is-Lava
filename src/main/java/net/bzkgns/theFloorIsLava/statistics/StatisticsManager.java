package net.bzkgns.theFloorIsLava.statistics;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsManager implements Listener {

    private final DatabaseManager database;

    private final Map<UUID, PlayerStatistics> cache = new HashMap<>();

    public StatisticsManager(DatabaseManager database) {
        this.database = database;
    }

    public void load(Player player) {
        cache.put(player.getUniqueId(),
                database.load(player.getUniqueId()));
    }

    public void unload(Player player) {

        PlayerStatistics stats = cache.remove(player.getUniqueId());

        if (stats != null) {
            database.save(stats);
        }
    }

    public void saveAll() {

        for (PlayerStatistics stats : cache.values()) {
            database.save(stats);
        }
    }

    public void increment(Player player, StatisticType type) {
        cache.get(player.getUniqueId()).increment(type);
    }

    public void add(Player player, StatisticType type, int amount) {
        cache.get(player.getUniqueId()).add(type, amount);
    }

    public int get(Player player, StatisticType type) {
        return cache.get(player.getUniqueId()).get(type);
    }

    public Map<UUID, PlayerStatistics> getCache() {
        return cache;
    }
}