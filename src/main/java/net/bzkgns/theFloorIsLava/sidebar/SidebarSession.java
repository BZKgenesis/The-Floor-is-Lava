package net.bzkgns.theFloorIsLava.sidebar;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;

public class SidebarSession {

    private final Sidebar sidebar;
    private final SidebarProvider provider;

    public SidebarSession(Sidebar sidebar, SidebarProvider provider) {
        this.sidebar = sidebar;
        this.provider = provider;
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public SidebarProvider getProvider() {
        return provider;
    }

}
