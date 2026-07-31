package net.bzkgns.theFloorIsLava.statistics;

public enum StatisticType {

    KILLS("kills"),
    DEATHS("deaths"),

    POPUP_TOWERS("popup_towers"),

    GAMES_PLAYED("games_played"),
    GAMES_WON("games_won"),

    PLAY_TIME("play_time");

    private final String columnName;

    StatisticType(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

}
