package net.bzkgns.theFloorIsLava.teams;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.bzkgns.theFloorIsLava.game.GameState;
import org.bukkit.entity.Player;

public class TeamCommands {
    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("team")
                .executes( ctx -> {
                    if (plugin.getGameManager().getState() == GameState.RUNNING){
                        Messages.send(ctx.getSource().getSender(), "error.cannot_manage_team_during_game");
                        return Command.SINGLE_SUCCESS;
                    }
                    if (ctx.getSource().getExecutor() instanceof Player player){
                        TeamGUI.openMainMenu(player);
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
