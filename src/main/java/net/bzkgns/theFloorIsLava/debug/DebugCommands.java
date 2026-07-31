package net.bzkgns.theFloorIsLava.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.items.abilities.gambling.GamblingEngine;
import net.bzkgns.theFloorIsLava.items.abilities.TeamRespawnManager;
import net.bzkgns.theFloorIsLava.kits.KitData;
import net.bzkgns.theFloorIsLava.kits.KitManager;
import net.bzkgns.theFloorIsLava.lang.Messages;
import net.bzkgns.theFloorIsLava.statistics.PlayerStatistics;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

public class DebugCommands {
    private static final TheFloorIsLava plugin = TheFloorIsLava.getInstance();
    public static ArgumentBuilder <CommandSourceStack, ?> register() {
        return Commands.literal("debug")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.literal("respawnTeam")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.respawn_locations_header");
                                for (Map.Entry<String, Location> entry : TeamRespawnManager.getInstance().getRespawnPoints().entrySet()){
                                    Messages.send(player, "debug.respawn_location_line",
                                            Placeholder.unparsed("team_name", entry.getKey()),
                                            Placeholder.unparsed("location", entry.getValue().toString()));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("gameState")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.game_state", Placeholder.unparsed("state", plugin.getGameManager().getState().toString()));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("team")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.teams_header");
                                for (String teamName : TeamManager.getInstance().getTeams()){
                                    TeamData team = TeamManager.getInstance().getTeam(teamName);
                                    Messages.send(player, "debug.team_line",
                                            Placeholder.component("team_name", Component.text(teamName, team.getColor())),
                                            Placeholder.unparsed("members", team.getMembers().toString()));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("dangerState")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.danger_state", Placeholder.unparsed("state", plugin.getGameManager().getDangerManager().getState().toString()));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("kit")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.kits_header");
                                for (String kitName : KitManager.getInstance().getAllKits().keySet()){
                                    Messages.send(player, "debug.kit_line", Placeholder.unparsed("kit_name", kitName));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("kit_name", StringArgumentType.word()).suggests(
                                (_, suggestionsBuilder) -> {
                                    for (String kitName : KitManager.getInstance().getAllKits().keySet()) {
                                        if (kitName.startsWith(suggestionsBuilder.getRemaining()))
                                            suggestionsBuilder.suggest(kitName);
                                    }
                                    return suggestionsBuilder.buildFuture();
                                }
                        ).executes(ctx -> {
                            String kitName = StringArgumentType.getString(ctx, "kit_name");
                            KitData kit = KitManager.getInstance().getAllKits().get(kitName);
                            if (ctx.getSource().getExecutor() instanceof Player player && kit != null) {

                                player.sendMessage(kit.toString());
                            } else {
                                Messages.send(ctx.getSource().getSender(), "error.unknown_kit", Placeholder.unparsed("kit_name", kitName));
                            }
                            return Command.SINGLE_SUCCESS;
                        })))
                .then(Commands.literal("playerKits")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.player_kits_header");
                                for (Map.Entry<UUID, KitData> entry : KitManager.getInstance().getPlayerKits().entrySet()){
                                    Player currentPlayer = plugin.getServer().getPlayer(entry.getKey());
                                    String playerName = currentPlayer != null ? currentPlayer.getName() : entry.getKey().toString();
                                    Messages.send(player, "debug.player_kit_line",
                                            Placeholder.unparsed("player_name", playerName),
                                            Placeholder.unparsed("kit_name", entry.getValue().getName()));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("playerStats")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player){
                                Messages.send(player, "debug.player_stats_header");
                                for (Map.Entry<UUID, PlayerStatistics> entry : plugin.getStatisticsManager().getCache().entrySet()){
                                    Player currentPlayer = plugin.getServer().getPlayer(entry.getKey());
                                    String playerName = currentPlayer != null ? currentPlayer.getName() : entry.getKey().toString();
                                    for (Map.Entry<String, Integer> statEntry : entry.getValue().getAll().entrySet()) {
                                        Messages.send(player, "debug.player_stats_line",
                                                Placeholder.unparsed("player_name", playerName),
                                                Placeholder.unparsed("stats", statEntry.getKey()),
                                                Placeholder.unparsed("value", statEntry.getValue().toString())
                                        );

                                    }
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                ).then(Commands.literal("oreCount")
                        .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                                    if (ctx.getSource().getExecutor() instanceof Player player){
                                        int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                        countOres(player, player.getWorld(), radius);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                )).then(Commands.literal("gamble")
                        .then(Commands.literal("computeRtp")
                                .executes(context -> {
                                    if (context.getSource().getExecutor() instanceof Player player) {
                                        double rtp = GamblingEngine.computeRTP();
                                        player.sendMessage(Component.text(rtp));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))
                        );
    }

    public static void countOres(CommandSender sender, World world, int radiusChunks) {

        Map<OreType, Integer> oreCount = new EnumMap<>(OreType.class);
        Map<OreType, Integer> chunksContainingOre = new EnumMap<>(OreType.class);

        int totalChunks = 0;

        Chunk center = world.getSpawnLocation().getChunk();

        for (int cx = center.getX() - radiusChunks; cx <= center.getX() + radiusChunks; cx++) {
            for (int cz = center.getZ() - radiusChunks; cz <= center.getZ() + radiusChunks; cz++) {

                Chunk chunk = world.getChunkAt(cx, cz);

                if (!chunk.isLoaded()) {
                    chunk.load(false);
                }

                totalChunks++;

                EnumSet<OreType> oresFoundInChunk = EnumSet.noneOf(OreType.class);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {

                            OreType ore = OreType.fromMaterial(
                                    chunk.getBlock(x, y, z).getType()
                            );

                            if (ore == null)
                                continue;

                            oreCount.merge(ore, 1, Integer::sum);
                            oresFoundInChunk.add(ore);
                        }
                    }
                }

                for (OreType ore : oresFoundInChunk) {
                    chunksContainingOre.merge(ore, 1, Integer::sum);
                }
            }
        }

        int totalOres = oreCount.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        sender.sendMessage(Component.text("§6========== Ore Statistics =========="));
        sender.sendMessage(Component.text("Chunks analysés : §e" + totalChunks));
        sender.sendMessage(Component.text("Minerais trouvés : §e" + totalOres));
        sender.sendMessage(Component.empty());

        int finalTotalChunks = totalChunks;
        oreCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {

                    OreType ore = entry.getKey();
                    int amount = entry.getValue();

                    double average = (double) amount / finalTotalChunks;
                    double percent = amount * 100D / totalOres;
                    int chunkWithOre = chunksContainingOre.getOrDefault(ore, 0);
                    plugin.getLogger().info(String.format(
                            "%-12s | %7d | %6.2f/chunk | %6.2f%% | %5d chunks%n",
                            ore.getDisplayName(),
                            amount,
                            average,
                            percent,
                            chunkWithOre
                    ));
                    sender.sendMessage(Component.text(String.format(
                            "%-12s | %7d | %6.2f/chunk | %6.2f%% | %5d chunks",
                            ore.getDisplayName(),
                            amount,
                            average,
                            percent,
                            chunkWithOre
                    )));
                });
    }

    public enum OreType {

        STONE("Stone/deepslate"),
        COAL("Coal"),
        IRON("Iron"),
        COPPER("Copper"),
        GOLD("Gold"),
        REDSTONE("Redstone"),
        LAPIS("Lapis"),
        DIAMOND("Diamond"),
        EMERALD("Emerald"),
        QUARTZ("Quartz"),
        NETHER_GOLD("Nether Gold"),
        ANCIENT_DEBRIS("Ancient Debris");

        private final String displayName;

        OreType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static OreType fromMaterial(Material material) {
            return switch (material) {
                case COAL_ORE,
                     DEEPSLATE_COAL_ORE -> COAL;

                case IRON_ORE,
                     DEEPSLATE_IRON_ORE -> IRON;

                case COPPER_ORE,
                     DEEPSLATE_COPPER_ORE -> COPPER;

                case GOLD_ORE,
                     DEEPSLATE_GOLD_ORE -> GOLD;

                case REDSTONE_ORE,
                     DEEPSLATE_REDSTONE_ORE -> REDSTONE;

                case LAPIS_ORE,
                     DEEPSLATE_LAPIS_ORE -> LAPIS;

                case DIAMOND_ORE,
                     DEEPSLATE_DIAMOND_ORE -> DIAMOND;

                case EMERALD_ORE,
                     DEEPSLATE_EMERALD_ORE -> EMERALD;

                case NETHER_QUARTZ_ORE -> QUARTZ;

                case NETHER_GOLD_ORE -> NETHER_GOLD;

                case ANCIENT_DEBRIS -> ANCIENT_DEBRIS;

                case STONE,DEEPSLATE -> STONE;

                default -> null;
            };
        }
    }
}
/*
Coal 112.58/Chunk
Iron 101.06/Chunk
Copper 93.69/Chunk
Gold 71.07/Chunk
Redstone 35.19/Chunk
Lapis 24.24/Chunk
Diamond 23.99/Chunk


 */