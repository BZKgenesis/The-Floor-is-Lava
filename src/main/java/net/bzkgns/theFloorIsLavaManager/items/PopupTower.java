package net.bzkgns.theFloorIsLavaManager.items;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Map;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.getWoolBlockByPlayer;

@SuppressWarnings("UnstableApiUsage")
public class PopupTower {

    public static final int[][][] LAYOUT = {
            { // layer 0
                    {0, 0, 0, 0, 0, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 1, 0, 0, 0, 1, 0 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {0, 1, 0, 2, 0, 1, 0 },
                    {0, 0, 1, 0, 1, 0, 0 },
                    {0, 0, 0, 0, 0, 0, 0 }
            },
            { // layer 1
                    {0, 0, 0, 0, 0, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 1, 0, 0, 0, 1, 0 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {0, 1, 0, 2, 0, 1, 0 },
                    {0, 0, 1, 0, 1, 0, 0 },
                    {0, 0, 0, 0, 0, 0, 0 }
            },
            { // layer 2
                    {0, 0, 0, 0, 0, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 1, 0, 0, 0, 1, 0 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {0, 1, 0, 2, 0, 1, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 0, 0, 0, 0, 0, 0 }
            },
            { // layer 3
                    {0, 0, 0, 0, 0, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 1, 0, 0, 0, 1, 0 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {0, 1, 0, 2, 0, 1, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 0, 0, 0, 0, 0, 0 }
            },
            { // layer 4
                    {0, 0, 0, 0, 0, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 1, 0, 0, 0, 1, 0 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {0, 1, 0, 2, 0, 1, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 0, 0, 0, 0, 0, 0 }
            },
            { // layer 5
                    {0, 0, 1, 0, 1, 0, 0 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {1, 1, 0, 0, 0, 1, 1 },
                    {0, 1, 0, 1, 0, 1, 0 },
                    {1, 1, 0, 2, 0, 1, 1 },
                    {0, 0, 1, 1, 1, 0, 0 },
                    {0, 0, 1, 0, 1, 0, 0 }
            },
            { // layer 6
                    {0, 1, 1, 1, 1, 1, 0 },
                    {1, 1, 1, 1, 1, 1, 1 },
                    {1, 1, 1, 1, 1, 1, 1 },
                    {1, 1, 1, 1, 1, 1, 1 },
                    {1, 1, 1, 2, 1, 1, 1 },
                    {1, 1, 1, 1, 1, 1, 1 },
                    {0, 1, 1, 1, 1, 1, 0 }
            },
            { // layer 7
                    {0, 1, 1, 1, 1, 1, 0 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {0, 1, 1, 1, 1, 1, 0 }
            },
            { // layer 8
                    {0, 1, 0, 1, 0, 1, 0 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {0, 0, 0, 0, 0, 0, 0 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {0, 0, 0, 0, 0, 0, 0 },
                    {1, 0, 0, 0, 0, 0, 1 },
                    {0, 1, 0, 1, 0, 1, 0 }
            }
    };

    public static void placePopupTower(Player p, Location pos, Rotation rotation){

        Map<Integer, Material> blocks_layout = new HashMap<>();

        blocks_layout.put(0,Material.AIR);
        blocks_layout.put(1,getWoolBlockByPlayer(p));
        blocks_layout.put(2,Material.LADDER);
        int SIZE_X = LAYOUT[0][0].length;
        int SIZE_Z = LAYOUT[0].length;
        for (int y = 0; y<LAYOUT.length;y++){
            for (int z = 0; z<LAYOUT[y].length;z++){
                for (int x = 0; x<LAYOUT[y][z].length; x++){

                    setBlockRelative(pos, x-SIZE_X/2,y,z-SIZE_Z/2,blocks_layout.get(LAYOUT[y][z][x]),rotation, p);
                }
            }
        }

    }

    private static void setBlockRelative(Location origin, int x, int Y, int z, Material mat, Rotation rotation, Player p){
        int Z = z;
        int X = x;
        BlockFace facing = BlockFace.NORTH;
        switch (rotation){
            case Rotation.NONE:
                break;
            case Rotation.CLOCKWISE :
                Z = x;
                X = -z;
                facing = BlockFace.EAST;
                break;
            case Rotation.FLIPPED:
                Z = -z;
                X = -x;
                facing = BlockFace.SOUTH;
                break;
            case Rotation.COUNTER_CLOCKWISE:
                Z = -x;
                X = z;
                facing = BlockFace.WEST;
                break;
        }
        Block block = origin.getBlock().getRelative(X,Y,Z);
        BlockState blockState = block.getState();
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(block, blockState, block, new ItemStack(mat), p, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) return;
        if (block.getType() != Material.AIR && mat == Material.AIR) return;
        block.setType(mat);
        if (block.getBlockData() instanceof Directional directional){
            directional.setFacing(facing.getOppositeFace());
            block.setBlockData(directional);
        }
    }
}
