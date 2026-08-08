package net.bzkgns.theFloorIsLava.utils;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.world.WorldManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private static final Material DEFAULT_CONCRETE_COLOR = Material.LIGHT_GRAY_CONCRETE;

    public static final List<Material> RESOURCE_MATERIALS = List.of(Material.DIAMOND,Material.GOLD_INGOT,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.AMETHYST_SHARD);

    public static final List<Material> WOOLS_MATERIALS = List.of(Material.WHITE_WOOL,Material.ORANGE_WOOL,Material.MAGENTA_WOOL,Material.LIGHT_BLUE_WOOL,Material.YELLOW_WOOL,Material.LIME_WOOL,Material.PINK_WOOL,Material.GRAY_WOOL,Material.LIGHT_GRAY_WOOL,Material.CYAN_WOOL,Material.PURPLE_WOOL,Material.BLUE_WOOL,Material.BROWN_WOOL,Material.GREEN_WOOL,Material.RED_WOOL,Material.BLACK_WOOL);

    private static final HashMap<TextColor, Material> woolColor = new HashMap<>();
    static {
        woolColor.put(TextColor.color(15000804), Material.WHITE_WOOL);
        woolColor.put(TextColor.color(15367733), Material.ORANGE_WOOL);
        woolColor.put(TextColor.color(12470729), Material.MAGENTA_WOOL);
        woolColor.put(TextColor.color(6522834),  Material.LIGHT_BLUE_WOOL);
        woolColor.put(TextColor.color(12760348), Material.YELLOW_WOOL);
        woolColor.put(TextColor.color(3783214),  Material.LIME_WOOL);
        woolColor.put(TextColor.color(14254489), Material.PINK_WOOL);
        woolColor.put(TextColor.color(4276545),  Material.GRAY_WOOL);
        woolColor.put(TextColor.color(10528679), Material.LIGHT_GRAY_WOOL);
        woolColor.put(TextColor.color(2519441),  Material.CYAN_WOOL);
        woolColor.put(TextColor.color(8271039),  Material.PURPLE_WOOL);
        woolColor.put(TextColor.color(2437523),  Material.BLUE_WOOL);
        woolColor.put(TextColor.color(5649180),  Material.BROWN_WOOL);
        woolColor.put(TextColor.color(3558168),  Material.GREEN_WOOL);
        woolColor.put(TextColor.color(10365735), Material.RED_WOOL);
        woolColor.put(TextColor.color(1578004),  Material.BLACK_WOOL);
    }

    private static final HashMap<TextColor, Material> concreteColor = new HashMap<>();
    static {
        concreteColor.put(TextColor.color(15000804), Material.WHITE_CONCRETE);
        concreteColor.put(TextColor.color(15367733), Material.ORANGE_CONCRETE);
        concreteColor.put(TextColor.color(12470729), Material.MAGENTA_CONCRETE);
        concreteColor.put(TextColor.color(6522834),  Material.LIGHT_BLUE_CONCRETE);
        concreteColor.put(TextColor.color(12760348), Material.YELLOW_CONCRETE);
        concreteColor.put(TextColor.color(3783214),  Material.LIME_CONCRETE);
        concreteColor.put(TextColor.color(14254489), Material.PINK_CONCRETE);
        concreteColor.put(TextColor.color(4276545),  Material.GRAY_CONCRETE);
        concreteColor.put(TextColor.color(10528679), Material.LIGHT_GRAY_CONCRETE);
        concreteColor.put(TextColor.color(2519441),  Material.CYAN_CONCRETE);
        concreteColor.put(TextColor.color(8271039),  Material.PURPLE_CONCRETE);
        concreteColor.put(TextColor.color(2437523),  Material.BLUE_CONCRETE);
        concreteColor.put(TextColor.color(5649180),  Material.BROWN_CONCRETE);
        concreteColor.put(TextColor.color(3558168),  Material.GREEN_CONCRETE);
        concreteColor.put(TextColor.color(10365735), Material.RED_CONCRETE);
        concreteColor.put(TextColor.color(1578004),  Material.BLACK_CONCRETE);
    }
    public static Material getWoolBlockByPlayer(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getEntryTeam(p.getName());
        if (team != null){
            List<TextColor> keys = woolColor.keySet().stream().toList();
            return woolColor.get(TextColor.nearestColorTo(keys, team.color()));
        }
        return DEFAULT_WOOL_COLOR;
    }
    public static Material getConcreteBlockByPlayer(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getEntryTeam(p.getName());
        if (team != null){
            List<TextColor> keys = concreteColor.keySet().stream().toList();
            return concreteColor.get(TextColor.nearestColorTo(keys, team.color()));
        }
        return DEFAULT_CONCRETE_COLOR;
    }

    public static Material getWoolBlockByNamedTextColor(NamedTextColor color){
        List<TextColor> keys = woolColor.keySet().stream().toList();
        return woolColor.get(TextColor.nearestColorTo(keys,color));
    }

    public static boolean isWaterlogged(Block block) {
        return block.getBlockData() instanceof Waterlogged waterlogged
                && waterlogged.isWaterlogged();
    }

    public static boolean canPlaceBlock(Location pos){
        int SIZE = 17;
        int HEIGHT = 19;
        WorldManager manager = TheFloorIsLava.getInstance().getWorldManager();
        if (pos.getWorld().equals(manager.getGameWorld())){
            int Y_LEVEL = 277;
            return pos.getY() <= Y_LEVEL || pos.getY() >= Y_LEVEL+HEIGHT || abs(pos.getX()) >= SIZE || abs(pos.getZ()) >= SIZE;
        } else if (pos.getWorld().equals(manager.getLobbyWorld())) {
            int Y_LEVEL = -3;
            return pos.getY() <= Y_LEVEL || pos.getY() >= Y_LEVEL+HEIGHT || abs(pos.getX()) >= SIZE || abs(pos.getZ()) >= SIZE;
        } else {
            return true;
        }
    }

    public static void filterProtectedBlocks(List<Block> blocks) {
        blocks.removeIf(block ->
                !BlockUtils.canPlaceBlock(block.getLocation())
        );
    }


}
