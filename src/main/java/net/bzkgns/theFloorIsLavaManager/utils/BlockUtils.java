package net.bzkgns.theFloorIsLavaManager.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;

public class BlockUtils {

    private static final Material DEFAULT_WOOL_COLOR = Material.LIGHT_GRAY_WOOL;

    public static final List<Material> RESOURCE_MATERIALS = List.of(Material.DIAMOND,Material.GOLD_INGOT,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.AMETHYST_SHARD);

    public static final List<Material> WOOLS_MATERIALS = List.of(Material.WHITE_WOOL,Material.ORANGE_WOOL,Material.MAGENTA_WOOL,Material.LIGHT_BLUE_WOOL,Material.YELLOW_WOOL,Material.LIME_WOOL,Material.PINK_WOOL,Material.GRAY_WOOL,Material.LIGHT_GRAY_WOOL,Material.CYAN_WOOL,Material.PURPLE_WOOL,Material.BLUE_WOOL,Material.BROWN_WOOL,Material.GREEN_WOOL,Material.RED_WOOL,Material.BLACK_WOOL);

    private static final HashMap<TextColor, Material> blockColor = new HashMap<>();
    static {
        blockColor.put(TextColor.color(15000804), Material.WHITE_WOOL);
        blockColor.put(TextColor.color(15367733), Material.ORANGE_WOOL);
        blockColor.put(TextColor.color(12470729), Material.MAGENTA_WOOL);
        blockColor.put(TextColor.color(6522834),  Material.LIGHT_BLUE_WOOL);
        blockColor.put(TextColor.color(12760348), Material.YELLOW_WOOL);
        blockColor.put(TextColor.color(3783214),  Material.LIME_WOOL);
        blockColor.put(TextColor.color(14254489), Material.PINK_WOOL);
        blockColor.put(TextColor.color(4276545),  Material.GRAY_WOOL);
        blockColor.put(TextColor.color(10528679), Material.LIGHT_GRAY_WOOL);
        blockColor.put(TextColor.color(2519441),  Material.CYAN_WOOL);
        blockColor.put(TextColor.color(8271039),  Material.PURPLE_WOOL);
        blockColor.put(TextColor.color(2437523),  Material.BLUE_WOOL);
        blockColor.put(TextColor.color(5649180),  Material.BROWN_WOOL);
        blockColor.put(TextColor.color(3558168),  Material.GREEN_WOOL);
        blockColor.put(TextColor.color(10365735), Material.RED_WOOL);
        blockColor.put(TextColor.color(1578004),  Material.BLACK_WOOL);
    }
    public static Material getWoolBlockByPlayer(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getEntryTeam(p.getName());
        if (team != null){
            List<TextColor> keys = blockColor.keySet().stream().toList();
            return blockColor.get(TextColor.nearestColorTo(keys, team.color()));
        }
        return DEFAULT_WOOL_COLOR;
    }

    public static Material getWoolBlockByNamedTextColor(NamedTextColor color){

        List<TextColor> keys = blockColor.keySet().stream().toList();
        return blockColor.get(TextColor.nearestColorTo(keys,color));
    }

    public static boolean canPlaceBlock(BlockPos pos){
        return pos.getY() <= 278 || pos.getY() >= 297 || abs(pos.getX()) >= 15 || abs(pos.getZ()) >= 15;
    }

    public static boolean isWaterlogged(Block block) {
        return block.getBlockData() instanceof Waterlogged waterlogged
                && waterlogged.isWaterlogged();
    }

}
