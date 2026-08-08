package net.bzkgns.theFloorIsLava.game.sidebar;


import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import org.bukkit.entity.Player;

public abstract class SidebarProvider {

    protected ComponentSidebarLayout layout;

    public SidebarProvider(Player player) {
        this.layout = createLayout(player);
    }

    protected abstract ComponentSidebarLayout createLayout(Player player);

    public void apply(Sidebar sidebar) {
        layout.apply(sidebar);
    }


}
