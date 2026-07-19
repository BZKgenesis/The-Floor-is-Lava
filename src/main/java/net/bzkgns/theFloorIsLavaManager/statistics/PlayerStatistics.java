package net.bzkgns.theFloorIsLavaManager.statistics;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatistics {

    private final UUID uuid;
    private final EnumMap<StatisticType, Integer> values =
            new EnumMap<>(StatisticType.class);

    public PlayerStatistics(UUID uuid) {
        this.uuid = uuid;

        for (StatisticType type : StatisticType.values()) {
            values.put(type, 0);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public int get(StatisticType type) {
        return values.get(type);
    }

    public void set(StatisticType type, int value) {
        values.put(type, value);
    }

    public void add(StatisticType type, int amount) {
        values.merge(type, amount, Integer::sum);
    }

    public void increment(StatisticType type) {
        add(type, 1);
    }

    public Map<String, Integer> getAll() {
        Map<String, Integer> allStats = new HashMap<>();
        for (StatisticType type : StatisticType.values()) {
            allStats.put(type.getColumnName(), get(type));
        }
        return allStats;
    }
}