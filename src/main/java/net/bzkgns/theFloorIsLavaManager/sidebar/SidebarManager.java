package net.bzkgns.theFloorIsLavaManager.sidebar;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SidebarManager {

    private final Map<UUID, SidebarSession> sidebars = new HashMap<>();

    private static int sidebarTask = -1;

    public SidebarManager(){
        if (sidebarTask == -1){
            sidebarTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheFloorIsLavaManager.getInstance(), this::updateAll, 1, 10);
        }
    }

    public void show(Player player, SidebarProvider provider) {

        Sidebar sidebar = new Sidebar(provider.getTitle());

        sidebar.setLines(provider.getLines(player));

        player.setScoreboard(sidebar.getScoreboard());

        sidebars.put(
                player.getUniqueId(),
                new SidebarSession(sidebar, provider)
        );
    }

    public void hide(Player player) {

        sidebars.remove(player.getUniqueId());

        player.setScoreboard(
                player.getServer()
                        .getScoreboardManager()
                        .getMainScoreboard()
        );
    }

    public void update(Player player) {

        SidebarSession session = sidebars.get(player.getUniqueId());

        if (session == null) {
            return;
        }

        session.getSidebar().setTitle(
                session.getProvider().getTitle()
        );

        session.getSidebar().setLines(
                session.getProvider().getLines(player)
        );
    }

    public void updateAll() {

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    public void setProvider(Player player, SidebarProvider provider) {

        SidebarSession session = sidebars.get(player.getUniqueId());

        if (session == null) {
            show(player, provider);
            return;
        }

        session.setProvider(provider);

        update(player);
    }

    public Sidebar getSidebar(Player player) {

        SidebarSession session = sidebars.get(player.getUniqueId());

        return session == null ? null : session.getSidebar();
    }
}
