package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.kits.KitData;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarLine;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbySidebarProvider implements SidebarProvider {

    private final int START_LINE = 20;

    @Override
    public List<SidebarLine> getLines(Player player) {
        List<SidebarLine> currentLines = new ArrayList<>();
        TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        int i = START_LINE;
        currentLines.add(new SidebarLine(
                i,
                Component.text("Équipe:")
        ));
        i--;
        if (teamData == null){
            currentLines.add(new SidebarLine(
                    i,
                    Component.text("Aucune équipe")
            ));
        }else{
            currentLines.add(new SidebarLine(
                    i,
                    teamData.getName()
            ));
            List<UUID> members = teamData.getMembers();
            for (UUID uuid : members){
                Player player1 = Bukkit.getPlayer(uuid);
                if (player1 == null) continue;
                if (teamData.getOwner().equals(player1)){
                    currentLines.add(new SidebarLine(
                            i,
                            Component.text("- \uD83D\uDC51 " + TextUtils.plainText(player1.displayName()), NamedTextColor.GOLD)
                    ));
                }else{
                    currentLines.add(new SidebarLine(
                            i,
                            Component.text("- ").append(player1.displayName())
                    ));
                }
                i--;
            }
        }

        currentLines.add(new SidebarLine(
                i,
                Component.text(""))
        );

        i--;

        currentLines.add(new SidebarLine(
                i,
                Component.text("Kit Sélectionné: "))
        );

        i--;

        KitData kitData = KitManager.getInstance().getPlayerKit(player);
        TextComponent kitName = kitData==null?Component.text("Aucun"):kitData.getDisplayName();
        currentLines.add(new SidebarLine(
                i,Component.text("  ").append(kitName)
                )
        );
        return currentLines;

    }

    @Override
    public Component getTitle() {
        return Component.text("Lobby");
    }
}
