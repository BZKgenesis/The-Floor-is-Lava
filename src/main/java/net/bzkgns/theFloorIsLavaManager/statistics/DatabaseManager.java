package net.bzkgns.theFloorIsLavaManager.statistics;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private Connection connection;

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    public void connect() throws SQLException {

        File folder = plugin.getDataFolder();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, "stats.db");

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + file.getAbsolutePath()
        );
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }

    public PlayerStatistics load(UUID uuid) {

        PlayerStatistics stats = new PlayerStatistics(uuid);

        String sql = "SELECT * FROM player_stats WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                for (StatisticType type : StatisticType.values()) {
                    stats.set(type, rs.getInt(type.getColumnName()));
                }
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading player statistics for UUID: " + uuid, e);
        }

        return stats;
    }

    public void initializeDatabase() throws SQLException {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS player_stats
                (
                    uuid TEXT PRIMARY KEY,
                
                    kills INTEGER NOT NULL DEFAULT 0,
                    deaths INTEGER NOT NULL DEFAULT 0,
                
                    popup_towers INTEGER NOT NULL DEFAULT 0,
                
                    games_played INTEGER NOT NULL DEFAULT 0,
                    games_won INTEGER NOT NULL DEFAULT 0,
                
                    play_time INTEGER NOT NULL DEFAULT 0
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public void save(PlayerStatistics stats) {

        String sql = """
        INSERT INTO player_stats(
            uuid,
            kills,
            deaths,
            popup_towers,
            games_played,
            games_won,
            play_time
        )
        VALUES (?, ?, ?, ?, ?, ?, ?)

        ON CONFLICT(uuid) DO UPDATE SET

            kills = excluded.kills,
            deaths = excluded.deaths,
            popup_towers = excluded.popup_towers,
            games_played = excluded.games_played,
            games_won = excluded.games_won,
            play_time = excluded.play_time;
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, stats.getUuid().toString());

            stmt.setInt(2, stats.get(StatisticType.KILLS));
            stmt.setInt(3, stats.get(StatisticType.DEATHS));
            stmt.setInt(4, stats.get(StatisticType.POPUP_TOWERS));
            stmt.setInt(5, stats.get(StatisticType.GAMES_PLAYED));
            stmt.setInt(6, stats.get(StatisticType.GAMES_WON));
            stmt.setInt(7, stats.get(StatisticType.PLAY_TIME));

            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving player statistics for UUID: " + stats.getUuid(), e);
        }
    }


}
