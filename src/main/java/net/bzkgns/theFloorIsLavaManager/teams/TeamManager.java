package net.bzkgns.theFloorIsLavaManager.teams;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager {
    private final static List<NamedTextColor> colorsAvailable = List.of(
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW);
    private final Map<String, TeamData> teams = new HashMap<>();
    private final InviteManager inviteManager;

    private static TeamManager instance;

    public static TeamManager getInstance() {
        if (instance == null) {
            instance = new TeamManager();
        }
        return instance;
    }


    public TeamManager() {
        this.inviteManager = new InviteManager();
    }

    public List<String> getTeams(){
        return teams.keySet().stream().toList();
    }

    public InviteManager getInviteManager(){
        return this.inviteManager;
    }



    public TeamData getTeam(String name) { return teams.get(name); }


    public TeamData getPlayerTeam(UUID uuid) {
        for (TeamData t : teams.values()) {
            if (t.getMembers().contains(uuid)) {
                return t;
            }
        }
        return null;
    }


    public NamedTextColor randomColor() {
        List<NamedTextColor> usableColor = new ArrayList<>(colorsAvailable);
        for (TeamData team : teams.values()){
            usableColor.remove(team.getColor());
        }
        if (usableColor.isEmpty()){
            usableColor = new ArrayList<>(colorsAvailable);
        }
        return NamedTextColor.nearestTo( usableColor.get(new Random().nextInt(usableColor.size())));
    }


    public void createTeamForPlayer(Player p) {
        String name = p.getName();
        NamedTextColor color = randomColor();

        Team team = createVanillaTeam(name, color);

        TeamData data = new TeamData(name, color, team, p.getUniqueId());
        data.addMember(p.getUniqueId());
        addPlayerToVanillaTeam(p, name);
        teams.put(name, data);
    }


    public Team createVanillaTeam(String teamName, NamedTextColor color) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = sb.getTeam(teamName);
        if (team == null) team = sb.registerNewTeam(teamName);
        team.color(color);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        return team;
    }

    public void removePlayerFromTeam(Player p){
        TeamData team = getPlayerTeam(p.getUniqueId());
        team.removeMember(p.getUniqueId());
        removePlayerFromVanillaTeam(p, team.getId());
        if (p.getName().equals(team.getId())){
            teams.remove(team.getId());
            deleteVanillaTeam(team.getId());
            inviteManager.deleteAllInviteForTeam(team.getId());
        }
    }


    public void addPlayerToVanillaTeam(Player p, String teamName) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = sb.getTeam(teamName);
        if (t != null) t.addEntry(p.getName());
    }
    public void removePlayerFromVanillaTeam(Player p, String teamName) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = sb.getTeam(teamName);
        if (t != null) t.removeEntry(p.getName());
    }
    public void deleteVanillaTeam(String teamName) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = sb.getTeam(teamName);

        if (t != null) t.unregister();
    }

    public void clearTeams() {
        teams.clear();
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : sb.getTeams()) {
            team.unregister();
        }
    }

    public List<TeamData> getTeamAlive() {
        List<TeamData> aliveTeams = new ArrayList<>();
        for (TeamData team : teams.values()) {
            if (!team.isEliminated()) {
                aliveTeams.add(team);
            }
        }
        return aliveTeams;
    }

    public List<NamedTextColor> getAvailableColors() {
        List<NamedTextColor> usableColors = new ArrayList<>(colorsAvailable);
        for (TeamData team : teams.values()){
            usableColors.remove(team.getColor());
        }
        if (usableColors.isEmpty()){
            usableColors = new ArrayList<>(colorsAvailable);
        }
        return usableColors;
    }
}
