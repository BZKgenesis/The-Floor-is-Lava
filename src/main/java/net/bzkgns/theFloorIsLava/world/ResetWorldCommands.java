package net.bzkgns.theFloorIsLava.world;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.exception.WorldGenerationException;
import net.bzkgns.theFloorIsLava.config.lang.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.logging.Level;

public class ResetWorldCommands {
    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reset")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.literal("random")
                        .executes(_ -> resetWorldRandomCommande(0))
                        .then(Commands.argument("seed", LongArgumentType.longArg()).executes(
                                ctx -> resetWorldRandomCommande(LongArgumentType.getLong(ctx,"seed"))))
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
                                .executes(ctx -> resetWorldMapCommande(
                                        StringArgumentType.getString(ctx,"map_name").replace("\"","")))));
    }
    private static int resetWorldRandomCommande(long seed){
        if (seed == 0){
            seed = System.currentTimeMillis();
        }
        Messages.broadcastOp("info.world_reset_random", Placeholder.unparsed("seed", String.valueOf(seed)));

        Messages.broadcast("info.world_generating");
        Messages.broadcast("info.world_generating_warning");
        try{
            plugin.getWorldManager().resetRandomWorld(seed);
            Messages.broadcastOpPing( "validation.world_reset_success");
        } catch (WorldGenerationException e){
            Messages.broadcastOpError("error.world_reset_failed");
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la réinitialisation du monde.", e);
        }


        return Command.SINGLE_SUCCESS;
    }

    private static int resetWorldMapCommande(String map_name){

        Messages.broadcastOp( "info.world_reset_map", Placeholder.unparsed("map_name", map_name));

        Messages.broadcast( "info.world_generating");
        Messages.broadcast( "info.world_generating_warning");

        try {
            plugin.getWorldManager().loadMap(map_name);
            Messages.broadcastOpPing( "validation.world_reset_map_success", Placeholder.unparsed("map_name", map_name));
        }catch (WorldGenerationException e){
            Messages.broadcastOpError( "error.world_reset_map_failed", Placeholder.unparsed("map_name", map_name));
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la réinitialisation du monde avec la map " + map_name + ".", e);
        }


        return Command.SINGLE_SUCCESS;
    }
}
