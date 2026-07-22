package net.bzkgns.theFloorIsLavaManager.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface SidebarProvider {

    List<SidebarLine> getLines(Player player);

    Component getTitle();

}
