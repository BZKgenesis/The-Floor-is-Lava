package net.bzkgns.theFloorIsLavaManager.teams;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.abilities.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TeamData {


    private final String id;
    private String name;
    private NamedTextColor color;
    private final Team vanillaTeam;
    private final List<UUID> members = new ArrayList<>();
    private final UUID owner;


    public TeamData(String id, NamedTextColor color, Team team, UUID owner) {
        this.id = id;
        this.name = id;
        this.color = color;
        this.vanillaTeam = team;
        this.owner = owner;
    }


    public String getNameText() { return name; }
    public TextComponent getName() { return Component.text(name, color); }
    public String getId() { return id; }
    public NamedTextColor getColor() { return color; }
    public List<UUID> getMembers() { return members; }


    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void acceptRequest(UUID uuid){
        addMember(uuid);
    }
    public void rename(String newName) {
        this.name = newName;
    }

    public void changeColor(NamedTextColor newColor) {
        this.color = newColor;
        this.vanillaTeam.color(newColor);
    }

    public boolean isEliminated() {
        if (TheFloorIsLavaManager.getInstance().getGameManager().getDangerManager().getState() != DangerManager.DangerState.RISING)
            return false;
        if (TeamRespawnManager.getInstance().hasRespawnPoint(this.getId())) {
            return false;
        }
        int nbPlayerAlive = 0;
        for (UUID member : members) {
            Player player = org.bukkit.Bukkit.getPlayer(member);
            if (player != null && player.isOnline() && !player.isDead() && player.getGameMode() != GameMode.SPECTATOR) {
                nbPlayerAlive++;
            }
        }
        return nbPlayerAlive == 0;
    }

    public Player getOwner() {
        return org.bukkit.Bukkit.getPlayer(owner);
    }

    public Team getVanillaTeam() {
        return vanillaTeam;
    }
}
