package net.bzkgns.theFloorIsLava.game.kits;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.bzkgns.theFloorIsLava.game.GameState;
import org.bukkit.entity.Player;

public class KitCommands {
    public static LiteralCommandNode<CommandSourceStack> registerCommands(){
        KitManager kitManager = KitManager.getInstance();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("kit")
                .executes(
                        ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) {
                                KitChoiceGUI.openKitChoiceGUI(player);
                            }
                            return Command.SINGLE_SUCCESS;
                        }
                )
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) {
                                for (String kitName : kitManager.getKitNames()){
                                    player.sendMessage(kitName);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("choose")
                        .then(Commands.argument("item_key", StringArgumentType.word())
                                .suggests(
                                        (_, builder) -> {
                                            String input = builder.getRemaining().toLowerCase();
                                            for (String kitName : kitManager.getKitNames()) {
                                                if (kitName.toLowerCase().startsWith(input)) {
                                                    builder.suggest(kitName);
                                                }
                                            }
                                            return builder.buildFuture();
                                        }
                                )
                                .executes(context -> {
                                    if (context.getSource().getExecutor() instanceof Player player){
                                        if (!player.isOp() && TheFloorIsLava.getInstance().getGameManager().getState() == GameState.RUNNING){
                                            Messages.send(player, "kit.cannot_change_kit_in_game");
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        String kitName = StringArgumentType.getString(context, "item_key");
                                        player.getInventory().clear();
                                        kitManager.assignKitToPlayer(player.getUniqueId(), kitName);
                                        kitManager.applyKitToPlayer(player);
                                    } else {
                                        TheFloorIsLava.getInstance().getLogger().info("This command can only be executed by a player.");
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("give")
                        .executes( context -> {
                            if (context.getSource().getExecutor() instanceof Player player){
                                kitManager.applyKitToPlayer(player);
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                ;
        return root.build();
    }
}
