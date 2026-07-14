package net.bzkgns.theFloorIsLavaManager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * /tfl config list                -> affiche tous les paramètres et leur valeur
 * /tfl config get <cle>           -> affiche un paramètre
 * /tfl config set <cle> <valeur>  -> modifie un paramètre (refusé si une partie est en cours)
 * /tfl config gui                 -> ouvre l'éditeur graphique (ConfigGUI)
 *
 * Ajouter un paramètre = l'ajouter dans DangerConfigKey, rien à changer ici.
 */
public class ConfigCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> registerConfigNode(DangerManager dangerManager) {
        LiteralArgumentBuilder<CommandSourceStack> configNode = Commands.literal("config")
                .requires(sender -> sender.getSender().isOp());

        configNode.then(Commands.literal("list")
                .executes(ctx -> listConfig(ctx, dangerManager)));

        configNode.then(Commands.literal("get")
                .then(Commands.argument("cle", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (DangerConfigKey k : DangerConfigKey.values()) builder.suggest(k.getKey());
                            return builder.buildFuture();
                        })
                        .executes(ctx -> getValue(ctx, dangerManager))));

        configNode.then(Commands.literal("set")
                .then(Commands.argument("cle", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (DangerConfigKey k : DangerConfigKey.values()) builder.suggest(k.getKey());
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("valeur", StringArgumentType.word())
                                .executes(ctx -> setValue(ctx, dangerManager)))));

        configNode.then(Commands.literal("gui")
                .executes(ctx -> openGui(ctx, dangerManager)));

        return configNode;
    }

    private static int listConfig(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager) {
        DangerConfig config = dangerManager.getConfig();
        ctx.getSource().getSender().sendMessage(Component.text("§6--- Configuration TFL ---"));
        for (DangerConfigKey k : DangerConfigKey.values()) {
            ctx.getSource().getSender().sendMessage(Component.text(
                    "§e" + k.getKey() + "§7 = §f" + k.get(config) + " §8(" + k.getDescription() + ")"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int getValue(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager) {
        String rawKey = StringArgumentType.getString(ctx, "cle");
        DangerConfigKey key = DangerConfigKey.fromKey(rawKey);
        if (key == null) {
            ctx.getSource().getSender().sendMessage(Component.text("§cParamètre inconnu : " + rawKey));
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().getSender().sendMessage(Component.text(
                "§e" + key.getKey() + " §7= §f" + key.get(dangerManager.getConfig())));
        return Command.SINGLE_SUCCESS;
    }

    private static int setValue(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager) {
        if (!dangerManager.canEditConfig()) {
            ctx.getSource().getSender().sendMessage(Component.text(
                    "Impossible de modifier la configuration pendant une partie en cours.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        String rawKey = StringArgumentType.getString(ctx, "cle");
        String rawValue = StringArgumentType.getString(ctx, "valeur");
        DangerConfigKey key = DangerConfigKey.fromKey(rawKey);

        if (key == null) {
            ctx.getSource().getSender().sendMessage(Component.text("§cParamètre inconnu : " + rawKey));
            return Command.SINGLE_SUCCESS;
        }

        try {
            key.set(dangerManager.getConfig(), rawValue);
        } catch (NumberFormatException e) {
            ctx.getSource().getSender().sendMessage(Component.text("§cValeur invalide pour " + rawKey + " : " + rawValue));
            return Command.SINGLE_SUCCESS;
        }

        ctx.getSource().getSender().sendMessage(Component.text("§a" + key.getKey() + " défini à " + rawValue));
        return Command.SINGLE_SUCCESS;
    }

    private static int openGui(CommandContext<CommandSourceStack> ctx, DangerManager dangerManager) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(Component.text("§cCommande réservée aux joueurs."));
            return Command.SINGLE_SUCCESS;
        }
        if (!dangerManager.canEditConfig()) {
            player.sendMessage(Component.text("Impossible d'éditer la configuration pendant une partie en cours.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        ConfigGUI.open(player, dangerManager);
        return Command.SINGLE_SUCCESS;
    }
}
