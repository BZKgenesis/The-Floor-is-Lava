package net.bzkgns.theFloorIsLavaManager.teams;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TeamData {


    private final String name;
    private final NamedTextColor color;
    private final List<UUID> members = new ArrayList<>();


    public TeamData(String name, NamedTextColor color) {
        this.name = name;
        this.color = color;
    }


    public String getName() { return name; }
    public NamedTextColor getColor() { return color; }
    public List<UUID> getMembers() { return members; }


    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void acceptRequest(UUID uuid){
        addMember(uuid);
    }

    public boolean isEliminated() {
        if (TheFloorIsLavaManager.getInstance().getGameManager().getDangerManager().getState() != DangerManager.DangerState.RISING)
            return false;
        if (TeamRespawnManager.getInstance().hasRespawnPoint(this.getName())) {
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
}
