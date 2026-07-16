package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.managers.ConfigRegistry;
import net.bzkgns.theFloorIsLavaManager.managers.GameManager;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.bzkgns.theFloorIsLavaManager.items.ItemManager;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.teams.TeamGUI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

import static net.bzkgns.theFloorIsLavaManager.config.ConfigCommands.registerConfigNode;

@SuppressWarnings("SameReturnValue")
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
    private static int start(CommandContext<CommandSourceStack> ctx, GameManager gameManager){
        if (gameManager.startGame()){
            ctx.getSource().getSender().sendMessage("Démarrage du système.");
        }else{
            ctx.getSource().getSender().sendMessage("Erreur dans le démarrage du système (état actuel : " + gameManager.getState() + ").");
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int stop(CommandContext<CommandSourceStack> ctx){
        TheFloorIsLavaManager.getInstance().getGameManager().stopGame();
        ctx.getSource().getSender().sendMessage("Arrêt du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int pause(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        if (!dangerManager.pause()){
            ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Impossible de mettre en pause (état actuel : " + dangerManager.getState() + ")."));
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().getSender().sendMessage("Pause du système.");
        return Command.SINGLE_SUCCESS;
    }
    private static int resume(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager){
        if (!dangerManager.resume()){
            ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Aucune pause en cours à reprendre."));
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

    public static LiteralCommandNode<CommandSourceStack> registerTflCommands(GameManager gameManager, TheFloorIsLavaManager plugin){
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("tfl");
        root.then(Commands.literal("config")
            .requires(sender -> sender.getSender().isOp())
                .then(registerConfigNode(ConfigRegistry.getConfigManager("danger")))
                .then(registerConfigNode(ConfigRegistry.getConfigManager("game")))
                .then(registerConfigNode(ConfigRegistry.getConfigManager("map"))));
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
        root.then(Commands.literal("give")
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
                        player.give(ItemManager.getItemByKey(itemKey).giveItem());
                    } else {
                        ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Item inconnu : " + itemKey));
                    }
                    return Command.SINGLE_SUCCESS;
                })));
        root.then(Commands.literal("resetWorld")
                .requires(sender -> sender.getSender().isOp())
                    .then(Commands.literal("random")
                        .executes(ctx -> resetWorldRandomCommande(ctx,0,plugin))
            .then(Commands.argument("seed", LongArgumentType.longArg()).executes(
                ctx -> resetWorldRandomCommande(ctx,LongArgumentType.getLong(ctx,"seed"),plugin)))
                ).then(Commands.literal("map")
                        .then(Commands.argument("map_name", StringArgumentType.greedyString()).suggests((_, suggestionsBuilder) ->{
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
                    if (plugin.getGameManager().getState() == GameState.RUNNING){
                        ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Vous ne pouvez pas gérer votre équipe pendant une partie."));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (ctx.getSource().getExecutor() instanceof Player player){
                        TeamGUI.openMainMenu(player);
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        root.then(Commands.literal("debug")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.literal("respawnTeam")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                player.sendMessage("respawn locations:");
                                for (Map.Entry<String, Location> entry : TeamRespawnManager.getInstance().getRespawnPoints().entrySet()){
                                    player.sendMessage("- respawn point for team " + entry.getKey() + " is at " + entry.getValue().toString());
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("team")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                player.sendMessage("teams");
                                for (String teamName : TeamManager.getInstance().getTeams()){
                                    player.sendMessage("- team " + teamName + " has members: " + TeamManager.getInstance().getTeam(teamName).getMembers().toString());
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))

                );


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
