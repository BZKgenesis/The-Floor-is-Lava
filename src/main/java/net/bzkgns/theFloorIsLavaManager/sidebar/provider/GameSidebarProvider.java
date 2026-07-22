package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarLine;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class GameSidebarProvider implements SidebarProvider {

    @Override
    public List<SidebarLine> getLines(Player player) {
        return List.of(
                new SidebarLine(
                        8,
                        Component.text(" Game")
                )
        );
    }

    @Override
    public Component getTitle() {
        return null;
    }
}
