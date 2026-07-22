package net.bzkgns.theFloorIsLavaManager.sidebar;

public class SidebarSession {

    private final Sidebar sidebar;
    private SidebarProvider provider;

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

    public void setProvider(SidebarProvider provider) {
        this.provider = provider;
    }
}
