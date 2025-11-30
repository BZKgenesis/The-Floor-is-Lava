package io.github.pikayorld.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

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
        dangerManager.pause();
        ctx.getSource().getSender().sendMessage("Pause du système.");
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

    public static LiteralCommandNode<CommandSourceStack> registerTflCommands(DangerManager dangerManager){
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("tfl");
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
        root.then(Commands.literal("resetWorld")
                .requires(sender -> sender.getSender().isOp())
                .executes( ctx -> {
                    World world = Bukkit.getWorld("world");
                    if (world != null){
                        world.setTime(0);
                        Bukkit.unloadWorld(world, false); // false = ne sauvegarde pas
                        File worldDataFolder = new File(Bukkit.getWorldContainer(), "world/data");
                        File worldEntitesFolder = new File(Bukkit.getWorldContainer(), "world/entities");
                        File worldLevel = new File(Bukkit.getWorldContainer(), "world/level.dat");
                        File worldLevelOld = new File(Bukkit.getWorldContainer(), "world/level.dat_old");
                        File worldPlayerDataFolder = new File(Bukkit.getWorldContainer(), "world/playerdata");
                        File worldPoi = new File(Bukkit.getWorldContainer(), "world/poi");
                        File worldRegion = new File(Bukkit.getWorldContainer(), "world/region");
                        File worldSessionLock = new File(Bukkit.getWorldContainer(), "world/session.lock");
                        File worldUidDat = new File(Bukkit.getWorldContainer(), "world/uid.dat");
                        boolean error = !deleteRecursively(worldDataFolder);
                        if (!deleteRecursively(worldEntitesFolder))
                            error = true;
                        if (!deleteRecursively(worldLevel))
                            error = true;
                        if (!deleteRecursively(worldLevelOld))
                            error = true;
                        if (!deleteRecursively(worldPlayerDataFolder))
                            error = true;
                        if (!deleteRecursively(worldPoi))
                            error = true;
                        if (!deleteRecursively(worldRegion))
                            error = true;
                        if (!deleteRecursively(worldSessionLock))
                            error = true;
                        if (!deleteRecursively(worldUidDat))
                            error = true;
                        if (error){
                            Bukkit.getLogger().warning("Tous les fichiers du monde n'ont pas pu etre reset");
                        }
                        Bukkit.getServer().restart();
                    }
                    return  Command.SINGLE_SUCCESS;
                }));
        return root.build();
    }

    public static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) deleteRecursively(f);
            }
        }
        return file.delete();
    }
}
