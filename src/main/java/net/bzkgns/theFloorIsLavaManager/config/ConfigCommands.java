package net.bzkgns.theFloorIsLavaManager.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

                    Messages.send(ctx.getSource().getSender(), "validation.config_saved");
                    return Command.SINGLE_SUCCESS;
                }));
        return configNode;
    }

    private static <T extends ConfigSection<T>> int listConfig(CommandContext<CommandSourceStack> ctx, ConfigManager<T> configManager) {
        T config = configManager.getConfig();
        Messages.send(ctx.getSource().getSender(), "gui.list_config_title",
                Placeholder.parsed("config_name",config.getName()));

        for (ConfigKey<T, ?> k : config.getKeys()) {
            Messages.send(ctx.getSource().getSender(),
                    "gui.list_config_element",
                    Placeholder.parsed("param", k.getKey()),
                    Placeholder.parsed("value", k.get(config).toString()),
                    Placeholder.parsed("description", k.getDescription()));
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
            Messages.send(ctx.getSource().getSender(), "error.unknown_parameter", Placeholder.unparsed("param", rawKey));
            return Command.SINGLE_SUCCESS;
        }

        Messages.send(ctx.getSource().getSender(), "gui.config_value",
                Placeholder.unparsed("param", key.getKey()),
                Placeholder.unparsed("value", key.get(configManager.getConfig()).toString()));

        return Command.SINGLE_SUCCESS;
    }

    private static <T extends ConfigSection<T>> int setValue(
            CommandContext<CommandSourceStack> ctx,
            ConfigManager<T> configManager,
            Supplier<Boolean> canEdit
    ) {
        if (!canEdit.get()) {
            Messages.send(ctx.getSource().getSender(), "error.cannot_modify_config_during_game");
            return Command.SINGLE_SUCCESS;
        }

        String rawKey = StringArgumentType.getString(ctx, "cle");
        String rawValue = StringArgumentType.getString(ctx, "valeur");

        ConfigKey<T, ?> key = configManager.getKey(rawKey);

        if (key == null) {
            Messages.send(ctx.getSource().getSender(), "error.unknown_parameter", Placeholder.unparsed("param", rawKey));
            return Command.SINGLE_SUCCESS;
        }

        try {
            configManager.set(rawKey, rawValue);
        } catch (NumberFormatException e) {
            Messages.send(ctx.getSource().getSender(), "error.invalid_value", Placeholder.unparsed("param", rawKey), Placeholder.unparsed("value", rawValue));
            return Command.SINGLE_SUCCESS;
        }

        Messages.send(ctx.getSource().getSender(), "validation.value_set", Placeholder.unparsed("param", key.getKey()), Placeholder.unparsed("value", key.get(configManager.getConfig()).toString()));
        return Command.SINGLE_SUCCESS;
    }

    private static <T extends ConfigSection<T>> int openGui(CommandContext<CommandSourceStack> ctx, ConfigManager<T> configManager) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            Messages.send(ctx.getSource().getSender(), "error.command_for_players_only");
            return Command.SINGLE_SUCCESS;
        }
        if (!TheFloorIsLavaManager.getInstance().getGameManager().canEditConfig()) {
            Messages.send(player, "error.cannot_modify_config_during_game");
            return Command.SINGLE_SUCCESS;
        }
        ConfigGUI.open(player, configManager.getConfig());
        return Command.SINGLE_SUCCESS;
    }
}
