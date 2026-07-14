package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.bzkgns.theFloorIsLavaManager.Teams.TeamGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.bzkgns.theFloorIsLavaManager.ConfigCommands.registerConfigNode;


public class TheFloorIsLavaCommands {
    private static int getLevel(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        double level = dangerManager.getDangerLevel();
        ctx.getSource().getSender().sendMessage("Le niveau actuel est de " + level);
        return Command.SINGLE_SUCCESS;
    }
    private static int setLevel(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        int level = IntegerArgumentType.getInteger(ctx, "couche");
        dangerManager.setDangerLevel(level);
        ctx.getSource().getSender().sendMessage("Niveau défini à " + level);
        return Command.SINGLE_SUCCESS;
    }
    private static int start(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        dangerManager.start();
        ctx.getSource().getSender().sendMessage("Démarrage du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int stop(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        dangerManager.stop();
        ctx.getSource().getSender().sendMessage("Arrêt du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int pause(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        if (!dangerManager.pause()){
            ctx.getSource().getSender().sendMessage("§cImpossible de mettre en pause (état actuel : " + dangerManager.getState() + ").");
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().getSender().sendMessage("Pause du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int resume(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        if (!dangerManager.resume()){
            ctx.getSource().getSender().sendMessage("§cAucune pause en cours à reprendre.");
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().getSender().sendMessage("Reprise du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int setIncreaseAmount(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        double vitesse = DoubleArgumentType.getDouble(ctx, "nbTick");
        dangerManager.setIncreaseAmount(vitesse);
        ctx.getSource().getSender().sendMessage("Vitesse défini à " + vitesse);
        return Command.SINGLE_SUCCESS;
    }
    private static int getSpeed(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        double speed = dangerManager.getIncreaseAmount();
        ctx.getSource().getSender().sendMessage("La vitesse actuel est de " + speed);
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralCommandNode<CommandSourceStack> registerTflCommands(DangerManager dangerManager, TheFloorIsLavaManager plugin){
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("tfl");
        root.then(registerConfigNode(dangerManager));
        root.then(Commands.literal("getLevel")
            .requires(sender -> sender.getSender().isOp())
            .executes( ctx -> getLevel(ctx,dangerManager)));
        root.then(Commands.literal("setLevel")
            .requires(sender -> sender.getSender().isOp()).then(
                Commands.argument("couche", IntegerArgumentType.integer(-64,319))
                .executes(ctx ->setLevel(ctx,dangerManager))));
        root.then(Commands.literal("start")
            .requires(sender -> sender.getSender().isOp())
            .executes(ctx -> start(ctx,dangerManager)));
        root.then(Commands.literal("stop")
            .requires(sender -> sender.getSender().isOp())
            .executes(ctx -> stop(ctx,dangerManager)));
        root.then(Commands.literal("pause")
            .requires(sender -> sender.getSender().isOp())
            .executes(ctx -> pause(ctx,dangerManager)));
        root.then(Commands.literal("setSpeed")
            .requires(sender -> sender.getSender().isOp()).then(
                Commands.argument("nbTick", DoubleArgumentType.doubleArg(1,1000))
                .executes(ctx ->setIncreaseAmount(ctx,dangerManager))));
        root.then(Commands.literal("getSpeed")
            .requires(sender -> sender.getSender().isOp())
            .executes( ctx -> getSpeed(ctx,dangerManager)));
        root.then(Commands.literal("givePopupTower")
                .requires(sender -> sender.getSender().isOp())
                .executes( ctx -> {

                    if ( ctx.getSource().getExecutor() instanceof Player player){
                        player.give(PopupTower.givePopupTower());
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        root.then(Commands.literal("resetWorld")
                .requires(sender -> sender.getSender().isOp())
                    .then(Commands.literal("random")
                        .executes(ctx -> resetWorldRandomCommande(ctx,0,plugin))
            .then(Commands.argument("seed", LongArgumentType.longArg()).executes(
                ctx -> resetWorldRandomCommande(ctx,LongArgumentType.getLong(ctx,"seed"),plugin)))
                ).then(Commands.literal("map")
                        .then(Commands.argument("map_name", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) ->{
                            plugin.getWorldManager().getMapsNames().forEach(name -> {
                                    if (name.contains(" ")) {
                                        suggestionsBuilder.suggest("\"" + name + "\"");
                                    } else {
                                        suggestionsBuilder.suggest(name);
                                    }
                                });
                            return suggestionsBuilder.buildFuture();
                                        }
                                        )
                                .executes(ctx -> resetWorldMapCommande(ctx,StringArgumentType.getString(ctx,"map_name").replace("\"",""),plugin)))));
        root.then(Commands.literal("team")
                .executes( ctx -> {
                    if (plugin.getDangerManagerInstance().getHasStarted()){
                        ctx.getSource().getSender().sendMessage("§cVous ne pouvez pas gérer votre équipe pendant une partie !");
                        return Command.SINGLE_SUCCESS;
                    }
                    if (ctx.getSource().getExecutor() instanceof Player player){
                        TeamGUI.openMainMenu(plugin,player);
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        return root.build();
    }

    private static int resetWorldRandomCommande(CommandContext<CommandSourceStack> ctx, long seed, TheFloorIsLavaManager plugin){

        plugin.getWorldManager().resetRandomWorld(seed);

        ctx.getSource().getSender()
                .sendMessage("Reset du monde lancé");

        return Command.SINGLE_SUCCESS;
    }

    private static int resetWorldMapCommande(CommandContext<CommandSourceStack> ctx, String map_name, TheFloorIsLavaManager plugin){

        plugin.getWorldManager().loadMap(map_name);

        ctx.getSource().getSender()
                .sendMessage("Chargement du monde \""+ map_name +"\" lancé");

        return Command.SINGLE_SUCCESS;
    }

}
