package net.bzkgns.theFloorIsLava;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLava.config.map.MapConfigKeys;
import net.bzkgns.theFloorIsLava.debug.DebugCommands;
import net.bzkgns.theFloorIsLava.items.ItemManager;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.game.RisingManager;
import net.bzkgns.theFloorIsLava.game.GameManager;
import net.bzkgns.theFloorIsLava.teams.TeamCommands;
import net.bzkgns.theFloorIsLava.world.ResetWorldCommands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import static net.bzkgns.theFloorIsLava.config.ConfigCommands.registerConfigNode;

@SuppressWarnings("SameReturnValue")
public class TheFloorIsLavaCommands {
    private static int getLevel(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        double level = risingManager.getDangerLevel();
        Messages.send(ctx.getSource().getSender(), "command.danger_level", Placeholder.unparsed("level", String.valueOf(level)));
        return Command.SINGLE_SUCCESS;
    }
    private static int setLevel(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        int level = IntegerArgumentType.getInteger(ctx, "couche");
        risingManager.setDangerLevel(level);
        Messages.send(ctx.getSource().getSender(), "command.danger_level_set", Placeholder.unparsed("level", String.valueOf(level)));
        return Command.SINGLE_SUCCESS;
    }
    private static int start(CommandContext<CommandSourceStack> ctx, GameManager gameManager){
        if (gameManager.startGame()){
            Messages.send(ctx.getSource().getSender(), "command.game_starting");
        }else{
            Messages.send(ctx.getSource().getSender(), "command.game_start_error", Placeholder.unparsed("state", gameManager.getState().toString()));
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int stop(CommandContext<CommandSourceStack> ctx){
        TheFloorIsLava.getInstance().getGameManager().stopGame();
        Messages.send(ctx.getSource().getSender(), "command.game_stopping");
        return Command.SINGLE_SUCCESS;
    }
    private static int pause(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        if (!risingManager.pause()){
            Messages.send(ctx.getSource().getSender(), "error.pause_failed", Placeholder.unparsed("state", risingManager.getState().toString()));
            return Command.SINGLE_SUCCESS;
        }
        Messages.send(ctx.getSource().getSender(), "command.game_paused");
        return Command.SINGLE_SUCCESS;
    }
    private static int resume(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        if (!risingManager.resume()){
            Messages.send(ctx.getSource().getSender(), "error.no_pause_to_resume");
            return Command.SINGLE_SUCCESS;
        }
        Messages.send(ctx.getSource().getSender(), "command.game_resumed");
        return Command.SINGLE_SUCCESS;
    }
    private static int setIncreaseAmount(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        double vitesse = DoubleArgumentType.getDouble(ctx, "nbTick");
        risingManager.setIncreaseAmount(vitesse);
        Messages.send(ctx.getSource().getSender(), "command.speed_set", Placeholder.unparsed("speed", String.valueOf(vitesse)));
        return Command.SINGLE_SUCCESS;
    }
    private static int getSpeed(CommandContext<CommandSourceStack> ctx, RisingManager risingManager){
        double speed = risingManager.getIncreaseAmount();
        Messages.send(ctx.getSource().getSender(), "command.speed_current", Placeholder.unparsed("speed", String.valueOf(speed)));
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralCommandNode<CommandSourceStack> registerTflCommands(GameManager gameManager, TheFloorIsLava plugin){
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("tfl");

        LiteralArgumentBuilder<CommandSourceStack> configNode = Commands.literal("config")
                .requires(sender -> sender.getSender().isOp());
        for (String configName : ConfigRegistry.getConfigManagers().keySet()) {
            configNode.then(registerConfigNode(ConfigRegistry.getConfigManager(configName)));
        }

        root.then(configNode);
        root.then(Commands.literal("getLevel")
                .requires(sender -> sender.getSender().isOp())
                .executes( ctx -> getLevel(ctx,gameManager.getDangerManager())));
        root.then(Commands.literal("setLevel")
                .requires(sender -> sender.getSender().isOp()).then(
                        Commands.argument("couche", IntegerArgumentType.integer(-64,319))
                                .executes(ctx ->setLevel(ctx,gameManager.getDangerManager()))));
        root.then(Commands.literal("start")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> start(ctx,gameManager)));
        root.then(Commands.literal("earlyRise")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> {
                    if (gameManager.earlyStartRisingPhase()){
                        Messages.sendPing(ctx.getSource().getSender(), "command.early_rise_success");
                    }else{
                        Messages.sendError(ctx.getSource().getSender(), "command.early_rise_fail");
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        root.then(Commands.literal("stop")
                .requires(sender -> sender.getSender().isOp())
                .executes(TheFloorIsLavaCommands::stop));
        root.then(Commands.literal("pause")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> pause(ctx,gameManager.getDangerManager())));
        root.then(Commands.literal("resume")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> resume(ctx,gameManager.getDangerManager())));
        root.then(Commands.literal("setSpeed")
                .requires(sender -> sender.getSender().isOp()).then(
                        Commands.argument("nbTick", DoubleArgumentType.doubleArg(1,1000))
                                .executes(ctx ->setIncreaseAmount(ctx,gameManager.getDangerManager()))));
        root.then(Commands.literal("getSpeed")
                .requires(sender -> sender.getSender().isOp())
                .executes( ctx -> getSpeed(ctx,gameManager.getDangerManager())));
        root.then(registerGive());
        root.then(TeamCommands.register());
        root.then(DebugCommands.register());
        root.then(Commands.literal("map")
                .then(Commands.literal("preview") //tp to tfl_game
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx ->{
                    if (ctx.getSource().getExecutor() instanceof Player player){
                        player.teleport(plugin.getWorldManager().getPreGameSpawnLocation());
                        player.setGameMode(GameMode.SPECTATOR);
                    }
                    return Command.SINGLE_SUCCESS;
                }))
                .then(ResetWorldCommands.register())
                .then(Commands.literal("setCenter")
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Integer x = player.getLocation().getBlockX();
                                Integer z = player.getLocation().getBlockZ();
                                setMapCenter(x,
                                        z);
                                Messages.send(player, "command.map_center_set",
                                        Placeholder.unparsed("x", String.valueOf(x)),
                                        Placeholder.unparsed("z", String.valueOf(z)));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            if (ctx.getSource().getExecutor() instanceof Player player){
                                                Integer x = IntegerArgumentType.getInteger(ctx, "x");
                                                Integer z = IntegerArgumentType.getInteger(ctx, "z");
                                                setMapCenter(x,
                                                        z);
                                                Messages.send(player, "command.map_center_set",
                                                        Placeholder.unparsed("x", String.valueOf(x)),
                                                        Placeholder.unparsed("z", String.valueOf(z)));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )

        );

        return root.build();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerGive() {
        return Commands.literal("give")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("item_key", StringArgumentType.word()).suggests(
                        (_, suggestionsBuilder) -> {
                            for (String itemKey : ItemManager.getAllItemKeys()) {
                                if (itemKey.startsWith(suggestionsBuilder.getRemaining()))
                                    suggestionsBuilder.suggest(itemKey);
                            }
                            return suggestionsBuilder.buildFuture();
                        }
                ).executes(ctx -> {
                    String itemKey = StringArgumentType.getString(ctx, "item_key");
                    if (ctx.getSource().getExecutor() instanceof Player player && ItemManager.getAllItemKeys().contains(itemKey)) {
                        player.give(ItemManager.getItemByKey(itemKey).giveItem(player));
                    } else {
                        Messages.send(ctx.getSource().getSender(), "error.unknown_item", Placeholder.unparsed("item_key", itemKey));
                    }
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static void setMapCenter(Integer x, Integer z) {
        ConfigRegistry.getConfigManager("map").set(MapConfigKeys.CENTER_X.getKey(), x.toString());
        ConfigRegistry.getConfigManager("map").set(MapConfigKeys.CENTER_Z.getKey(), z.toString());
    }
}