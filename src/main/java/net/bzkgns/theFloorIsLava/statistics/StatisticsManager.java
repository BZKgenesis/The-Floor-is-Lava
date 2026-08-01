package net.bzkgns.theFloorIsLava.statistics;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

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

    public LinkedHashMap<UUID, Integer> getTop(StatisticType type, int limit) {
        LinkedHashMap<UUID, Integer> top = new LinkedHashMap<>();
        cache.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().get(type), e1.getValue().get(type))).limit(limit)
                .forEachOrdered(entry -> top.put(entry.getKey(), entry.getValue().get(type)));

        return top;
    }
}