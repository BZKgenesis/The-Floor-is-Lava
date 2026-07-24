package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.kits.KitData;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public class LobbySidebarProvider extends SidebarProvider {


    public LobbySidebarProvider(Player player) {
        super(player);
    }

    private Integer teamOffset = 0;
    private Integer delay = 0;
    private final int MAX_DELAY = 10;

    @Override
    protected ComponentSidebarLayout createLayout(Player player) {

        SidebarComponent lines = SidebarComponent.builder()
                .addDynamicLine(() -> {
                    TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
                    if (teamData == null) {
                        return Component.text("Équipe: ").append(Component.text("Aucune équipe"));
                    } else {
                        return Component.text("Équipe: ").append(teamData.getName());
                    }
                })
                .addDynamicLine(() -> {
                    TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
                    if (teamData == null) {
                        return Component.text("");
                    }
                    delay++;
                    if (delay > MAX_DELAY) {
                        delay = 0;
                        if (teamData.getMembers().size() > 4) {
                            teamOffset++;
                            if (teamOffset > teamData.getMembers().size() - 4) {
                                teamOffset = 0;
                            }
                        }else{
                            teamOffset = 0;
                        }
                    }
                    return getTeamMemberTextOfPlayer(player, teamOffset);
                })
                .addDynamicLine(() -> getTeamMemberTextOfPlayer(player, teamOffset+1))
                .addDynamicLine(() -> getTeamMemberTextOfPlayer(player, teamOffset+2))
                .addDynamicLine(() -> getTeamMemberTextOfPlayer(player, teamOffset+3))
                .addStaticLine(Component.text("Kit :"))
                .addDynamicLine(()-> {
                    KitData kitData = KitManager.getInstance().getPlayerKit(player);
                    return kitData == null ? Component.text("Aucun") : kitData.getDisplayName();
                })
                .build();
        return new ComponentSidebarLayout(
                SidebarComponent.staticLine(Component.text("----- Lobby -----")),
                lines
        );
    }

    private TextComponent getTeamMemberTextOfPlayer(Player player, int NMember) {
        TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        StringBuilder membersText = new StringBuilder();
        if (teamData != null) {
            List<UUID> members = teamData.getMembers();
            if (members.size() > NMember){
                UUID memberUUID = members.get(NMember);
                Player memberPlayer = Bukkit.getPlayer(memberUUID);
                if (teamData.getOwner().equals(memberPlayer)){
                    membersText.append("- ").append("\uD83D\uDC51").append(TextUtils.plainText(memberPlayer.displayName()));
                }else{
                    if (memberPlayer != null)
                        membersText.append("- ").append(TextUtils.plainText(memberPlayer.displayName()));
                }
            }
        }
        return Component.text(membersText.toString());
    }
}

//  public void apply(Sidebar sidebar) {
//        layout.apply(sidebar);
//        List<SidebarLine> currentLines = new ArrayList<>();
//        TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
//        int i = 0;
//        currentLines.add(new SidebarLine(
//                i,
//                Component.text("Équipe:")
//        ));
//        i++;
//        if (teamData == null){
//            currentLines.add(new SidebarLine(
//                    i,
//                    Component.text("Aucune équipe")
//            ));
//        }else{
//            currentLines.add(new SidebarLine(
//                    i,
//                    teamData.getName()
//            ));
//            List<UUID> members = teamData.getMembers();
//            for (UUID uuid : members){
//                Player player1 = Bukkit.getPlayer(uuid);
//                if (player1 == null) continue;
//                if (teamData.getOwner().equals(player1)){
//                    currentLines.add(new SidebarLine(
//                            i,
//                            Component.text("- \uD83D\uDC51 " + TextUtils.plainText(player1.displayName()), NamedTextColor.GOLD)
//                    ));
//                }else{
//                    currentLines.add(new SidebarLine(
//                            i,
//                            Component.text("- ").append(player1.displayName())
//                    ));
//                }
//                i++;
//            }
//        }
//
//        currentLines.add(new SidebarLine(
//                i,
//                Component.text(""))
//        );
//
//        i++;
//
//        currentLines.add(new SidebarLine(
//                i,
//                Component.text("Kit Sélectionné: "))
//        );
//
//        i++;
//
//        KitData kitData = KitManager.getInstance().getPlayerKit(player);
//        TextComponent kitName = kitData==null?Component.text("Aucun"):kitData.getDisplayName();
//        currentLines.add(new SidebarLine(
//                i,Component.text("  ").append(kitName)
//                )
//        );
//    }
