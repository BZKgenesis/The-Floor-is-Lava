package net.bzkgns.theFloorIsLava.sidebar;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SidebarManager {

    private final Map<UUID, SidebarSession> sidebars = new HashMap<>();

    private final static ScoreboardLibrary scoreboardLibrary = TheFloorIsLava.getInstance().getScoreboardLibrary();

    private static int sidebarTask = -1;

    public SidebarManager(){
        if (sidebarTask == -1){
            sidebarTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLava.getInstance(), this::updateAll, 1, 1);
        }
    }

    public void show(Player player, SidebarProvider provider) {

        Sidebar sidebar = scoreboardLibrary.createSidebar();

        provider.apply(sidebar);

        sidebar.addPlayer(player);

        sidebars.put(
                player.getUniqueId(),
                new SidebarSession(sidebar, provider)
        );
    }

    public void hide(Player player) {

        SidebarSession session = sidebars.remove(player.getUniqueId());

        session.getSidebar().removePlayer(player);
    }

    public void update(Player player) {

        SidebarSession session = sidebars.get(player.getUniqueId());

        if (session == null) {
            return;
        }

        session.getProvider().apply(session.getSidebar());
    }

    public void updateAll() {

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

}
