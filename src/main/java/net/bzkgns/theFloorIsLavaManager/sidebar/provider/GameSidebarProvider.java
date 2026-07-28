package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.currency.PlayerBalance;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.sidebar.SidebarProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class GameSidebarProvider extends SidebarProvider {


    private SidebarAnimation<Component> titleAnimation;

    public GameSidebarProvider(Player player) {
        super(player);
    }

    @Override
    protected ComponentSidebarLayout createLayout(Player player) {

        titleAnimation = createGradientAnimation(Component.text("---- Game ----"));

        PlayerBalance playerBalance = TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().getBalance(player.getUniqueId());


        SidebarComponent lines = SidebarComponent.builder()
                .addDynamicLine(() -> playerBalance.displayMaterial(player))
                .addDynamicLine(() -> playerBalance.displayResource(player))
                .build();
        return new ComponentSidebarLayout(
                SidebarComponent.animatedLine(this.titleAnimation),
                lines
        );
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
            frames.add(MiniMessage.miniMessage().deserialize("<gradient:blue:cyan:" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
    }

}
