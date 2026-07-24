package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import org.bukkit.entity.Player;


public class GameSidebarProvider extends SidebarProvider {


    public GameSidebarProvider(Player player) {
        super(player);
    }

    @Override
    protected ComponentSidebarLayout createLayout(Player player) {
        return null;
    }

}
