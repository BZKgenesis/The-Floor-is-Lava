package io.github.pikayorld.theFloorIsLavaManager.Teams;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;


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
}
