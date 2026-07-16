package net.bzkgns.theFloorIsLavaManager.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * /tfl config list                -> affiche tous les paramètres et leur valeur
 * /tfl config get <cle>           -> affiche un paramètre
 * /tfl config set <cle> <valeur>  -> modifie un paramètre (refusé si une partie est en cours)
 * /tfl config gui                 -> ouvre l'éditeur graphique (ConfigGUI)
 * <p>
 * Ajouter un paramètre = l'ajouter dans DangerConfigKey, rien à changer ici.
 */
@SuppressWarnings("SameReturnValue")
public class ConfigCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> registerConfigNode(ConfigManager<?> configManager) {
        LiteralArgumentBuilder<CommandSourceStack> configNode = Commands.literal(configManager.getConfig().getName())
                .requires(sender -> sender.getSender().isOp());

        configNode.then(Commands.literal("list")
                .executes(ctx -> listConfig(ctx, configManager)));

        configNode.then(Commands.literal("get")
                .then(Commands.argument("cle", StringArgumentType.word())
                        .suggests((_, builder) -> {
                            for (ConfigKey<?,?> k : configManager.getConfig().getKeys()) builder.suggest(k.getKey());
                            return builder.buildFuture();
                        })
                        .executes(ctx -> getValue(ctx, configManager))));

        configNode.then(Commands.literal("set")
                .then(Commands.argument("cle", StringArgumentType.word())
                        .suggests((_, builder) -> {
                            for (ConfigKey<?,?> k : configManager.getConfig().getKeys()) builder.suggest(k.getKey());
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("valeur", StringArgumentType.word())
                                .executes(ctx -> setValue(ctx, configManager, () -> TheFloorIsLavaManager.getInstance().getGameManager().canEditConfig())))));

        configNode.then(Commands.literal("gui")
                .executes(ctx -> openGui(ctx, configManager)));
        configNode.then(Commands.literal("save")
                .executes(ctx -> {
                    configManager.saveConfig();
                    ctx.getSource().getSender().sendMessage(TextUtils.validationMessage("Configuration sauvegardée."));
                    return Command.SINGLE_SUCCESS;
                }));
        return configNode;
    }

    private static <T extends ConfigSection<T>> int listConfig(CommandContext<CommandSourceStack> ctx, ConfigManager<T> configManager) {
        T config = configManager.getConfig();
        ctx.getSource().getSender().sendMessage(Component.text("--- Configuration TFL ---").color(TextColor.fromHexString("#FFAA00")));

        for (ConfigKey<T, ?> k : config.getKeys()) {
            ctx.getSource().getSender().sendMessage(
                    Component.text(k.getKey()).color(TextColor.fromHexString("#FFFF55")).append(
                    Component.text(" = ").color(TextColor.fromHexString("#AAAAAA")).append(
                    Component.text(k.get(config).toString()).color(TextColor.fromHexString("#FFFFFF")).append(
                            Component.text("(" + k.getDescription()+ ")").color(TextColor.fromHexString("#555555"))))));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static <T extends ConfigSection<T>> int getValue(
            CommandContext<CommandSourceStack> ctx,
            ConfigManager<T> configManager
    ) {
        String rawKey = StringArgumentType.getString(ctx, "cle");

        ConfigKey<T, ?> key = configManager.getKey(rawKey);

        if (key == null) {
            ctx.getSource().getSender().sendMessage(
                    TextUtils.errorMessage("Paramètre inconnu : " + rawKey));
            return Command.SINGLE_SUCCESS;
        }

        ctx.getSource().getSender().sendMessage(
                TextUtils.textE(key.getKey()).append(
                TextUtils.text7(" = ")).append(
                TextUtils.textF(key.get(configManager.getConfig()).toString())));

        return Command.SINGLE_SUCCESS;
    }

    private static <T extends ConfigSection<T>> int setValue(
            CommandContext<CommandSourceStack> ctx,
            ConfigManager<T> configManager,
            Supplier<Boolean> canEdit
    ) {
        if (!canEdit.get()) {
            ctx.getSource().getSender().sendMessage(Component.text(
                    "Impossible de modifier la configuration pendant une partie en cours.",
                    NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        String rawKey = StringArgumentType.getString(ctx, "cle");
        String rawValue = StringArgumentType.getString(ctx, "valeur");

        ConfigKey<T, ?> key = configManager.getKey(rawKey);

        if (key == null) {
            ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Paramètre inconnu : " + rawKey));
            return Command.SINGLE_SUCCESS;
        }

        try {
            configManager.set(rawKey, rawValue);
        } catch (NumberFormatException e) {
            ctx.getSource().getSender().sendMessage(TextUtils.errorMessage(
                    "Valeur invalide pour " + rawKey + " : " + rawValue));
            return Command.SINGLE_SUCCESS;
        }

        ctx.getSource().getSender().sendMessage(TextUtils.validationMessage(
                key.getKey() + " défini à " + key.get(configManager.getConfig())));

        return Command.SINGLE_SUCCESS;
    }

    private static <T extends ConfigSection<T>> int openGui(CommandContext<CommandSourceStack> ctx, ConfigManager<T> configManager) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(TextUtils.errorMessage("Commande réservée aux joueurs."));
            return Command.SINGLE_SUCCESS;
        }
        if (!TheFloorIsLavaManager.getInstance().getGameManager().canEditConfig()) {
            player.sendMessage(Component.text("Impossible d'éditer la configuration pendant une partie en cours.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        ConfigGUI.open(player, configManager.getConfig());
        return Command.SINGLE_SUCCESS;
    }
}
