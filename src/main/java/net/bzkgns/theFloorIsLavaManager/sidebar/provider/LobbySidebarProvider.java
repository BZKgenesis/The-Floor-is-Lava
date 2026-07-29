package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.currency.PlayerBalance;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.kits.KitData;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class LobbySidebarProvider extends SidebarProvider {


    public LobbySidebarProvider(Player player) {
        super(player);
    }

    private Integer teamOffset = 0;
    private Integer delay = 0;
    private final int MAX_DELAY = 10;
    private SidebarAnimation<Component> titleAnimation;

    @Override
    protected ComponentSidebarLayout createLayout(Player player) {

        titleAnimation = createGradientAnimation(Messages.component(player, "sidebar.lobby_title"));

        PlayerBalance playerBalance = TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().getBalance(player.getUniqueId());

        SidebarComponent lines = SidebarComponent.builder()
                .addStaticLine(Messages.component(player, "sidebar.team"))
                .addDynamicLine(() -> {
                    TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
                    if (teamData == null) {
                        return Component.text("   ").append(Messages.component(player, "sidebar.no_team"));
                    } else {
                        return Component.text("   ").append(teamData.getName());
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
                .addStaticLine(Messages.component(player, "sidebar.kit"))
                .addDynamicLine(()-> {
                    KitData kitData = KitManager.getInstance().getPlayerKit(player);
                    return kitData == null ? Component.text("   ")
                            .append(Messages.component(player, "sidebar.no_kit")) :
                            Component.text("   ").append(kitData.getDisplayName());
                })
                .addStaticLine(playerBalance.prefixDisplayMaterial(player))
                .addDynamicLine(() -> Component.text("    ").append(playerBalance.displayMaterial(player, false)))
                .addStaticLine(playerBalance.prefixDisplayResource(player))
                .addDynamicLine(() -> Component.text("    ").append(playerBalance.displayResource(player,false)))
                .build();
        return new ComponentSidebarLayout(
                SidebarComponent.animatedLine(this.titleAnimation),
                lines
        );
    }

    private TextComponent getTeamMemberTextOfPlayer(Player player, int NMember) {
        TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        if (teamData != null) {
            List<UUID> members = teamData.getMembers();
            if (members.size() > NMember){
                UUID memberUUID = members.get(NMember);
                Player memberPlayer = Bukkit.getPlayer(memberUUID);
                String memberNameStr = memberPlayer==null?"":TextUtils.plainText(memberPlayer.displayName());
                if (teamData.getOwner().equals(memberPlayer)){
                    return Component.text("\uD83D\uDC51")
                            .append(MiniMessage.miniMessage().deserialize(String.format("<head:%s:false>", memberNameStr)))
                            .append(memberPlayer.displayName());
                }else{
                    if (memberPlayer != null)
                        return Component.text("-")
                                .append(MiniMessage.miniMessage().deserialize(String.format("<head:%s:false>", memberNameStr)))
                                .append(memberPlayer.displayName());
                }
            }
        }
        return Component.text("");
    }

    @Override
    public void apply(Sidebar sidebar) {
        titleAnimation.nextFrame();
        super.apply(sidebar);
    }

    private @NotNull SidebarAnimation<Component> createGradientAnimation(@NotNull Component text) {
        float step = 1f / 8f;

        TagResolver.Single textPlaceholder = Placeholder.component("text", text);
        List<Component> frames = new ArrayList<>((int) (2f / step));

        float phase = -1f;
        while (phase < 1) {
            frames.add(MiniMessage.miniMessage().deserialize("<gradient:red:gold:" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
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
