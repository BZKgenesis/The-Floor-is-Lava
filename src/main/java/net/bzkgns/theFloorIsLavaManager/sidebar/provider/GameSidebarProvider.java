package net.bzkgns.theFloorIsLavaManager.sidebar.provider;

import net.bzkgns.theFloorIsLavaManager.config.map.MapConfigKeys;
import net.bzkgns.theFloorIsLavaManager.currency.PlayerBalance;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
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
        boolean USE_CHAR = true;

        titleAnimation = createGradientAnimation(Component.text("---- Game ----"));

        PlayerBalance playerBalance = TheFloorIsLavaManager.getInstance().getGameManager().getMoneyManager().getBalance(player.getUniqueId());


        SidebarComponent lines = SidebarComponent.builder()
                .addStaticLine(Component.text("Centre:"))
                .addDynamicLine(() -> {

                    Integer relative_x = player.getLocation().getBlock().getX() - ConfigRegistry.getConfigManager("map").getInt(MapConfigKeys.CENTER_X.getKey());
                    Integer relative_z = player.getLocation().getBlock().getZ() - ConfigRegistry.getConfigManager("map").getInt(MapConfigKeys.CENTER_Z.getKey());

                    double angle = Math.toDegrees(Math.atan2(relative_z, relative_x)) - player.getYaw() - 90;
                    int compassIndex = (((int) Math.round((angle + 360) % 360 / 11.25))+32) % 32;

                    char[] compassChars = {'⬇','⬋','⬅','⬉','⬆','⬈','⮕','⬊',};

                    int distance = (int) Math.round(Math.sqrt(relative_x * relative_x + relative_z * relative_z));
                    if (USE_CHAR){
                        return MiniMessage.miniMessage().deserialize(String.format("  %s",compassChars[compassIndex / 4]))
                                .append(Component.text("( "+distance + " blocs )"));
                    }
                    return MiniMessage.miniMessage().deserialize(String.format("  <sprite:\"minecraft:items\":item/compass_%02d>",compassIndex))
                            .append(Component.text("( "+distance + " blocs )"));
                })
                .addBlankLine()
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
            frames.add(MiniMessage.miniMessage().deserialize("<gradient:blue:aqua:" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
    }

}
