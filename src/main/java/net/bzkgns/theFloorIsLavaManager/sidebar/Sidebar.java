package net.bzkgns.theFloorIsLavaManager.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Sidebar {

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final Map<Integer, Team> teams = new HashMap<>();

    public Sidebar(Component title) {

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard(); // TODO: faire un systeme de packet ou jsp pour faire fonctionner les sidebars

        Objective obj = scoreboard.getObjective("tfl");

        objective = Objects.requireNonNullElseGet(obj, () -> scoreboard.registerNewObjective(
                "tfl",
                Criteria.DUMMY,
                title
        ));


        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setTitle(Component title) {
        objective.displayName(title);
    }

    public void setLine(int score, Component text) {

        Team team = teams.computeIfAbsent(score, s -> {

            Team t = scoreboard.registerNewTeam("~line_" + s);

            // Une entrée unique par ligne
            String entry = ChatColor.values()[s].toString();

            t.addEntry(entry);

            objective.getScore(entry).setScore(s);

            return t;
        });

        team.prefix(text);
    }

    public void setLines(List<SidebarLine> lines) {

        // Supprime les anciennes lignes
        for (Integer score : teams.keySet().toArray(new Integer[0])) {

            boolean stillExists = lines.stream()
                    .anyMatch(line -> line.score() == score);

            if (!stillExists) {

                Team team = teams.remove(score);

                if (team != null) {

                    for (String entry : team.getEntries()) {
                        scoreboard.resetScores(entry);
                    }

                    team.unregister();
                }
            }
        }

        // Met à jour les lignes existantes
        for (SidebarLine line : lines) {
            setLine(line.score(), line.text());
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}