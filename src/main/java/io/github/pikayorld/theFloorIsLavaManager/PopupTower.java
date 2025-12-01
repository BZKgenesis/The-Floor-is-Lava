package io.github.pikayorld.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.pikayorld.theFloorIsLavaManager.BlockColorUtils.getWoolBlockByPlayer;

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

    private static final TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);
    public static ItemStack givePopupTower(){
        ItemStack popupTowerStack = new ItemStack(Material.CHEST);
        popupTowerStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        popupTowerStack.setData(DataComponentTypes.ITEM_NAME, Component.text("Popup Tower"));
        ItemMeta popupTowerMeta = popupTowerStack.getItemMeta();
        popupTowerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "popup"), PersistentDataType.STRING, "popupTower");
        popupTowerStack.setItemMeta(popupTowerMeta);

        return popupTowerStack;
    }

    public static boolean isPopupTower(ItemStack stack){
        if (stack.getType() == Material.CHEST){
            if(stack.getPersistentDataContainer().has(new NamespacedKey(plugin, "popup"))){
                return Objects.equals(stack.getPersistentDataContainer().get(new NamespacedKey(plugin, "popup"), PersistentDataType.STRING), "popupTower");
            }
        }
        return false;
    }

    public static void placePopupTower(Player p, Location pos, Rotation rotation){

        Map<Integer, Material> blocks_layout = new HashMap<Integer, Material>();

        blocks_layout.put(0,Material.AIR);
        blocks_layout.put(1,getWoolBlockByPlayer(p));
        blocks_layout.put(2,Material.LADDER);
        int SIZE_X = LAYOUT[0][0].length;
        int SIZE_Z = LAYOUT[0].length;
        for (int y = 0; y<LAYOUT.length;y++){
            for (int z = 0; z<LAYOUT[y].length;z++){
                for (int x = 0; x<LAYOUT[y][z].length; x++){
                    setBlockRelative(pos, x-SIZE_X/2,y,z-SIZE_Z/2,blocks_layout.get(LAYOUT[y][z][x]),rotation);
                }
            }
        }

    }

    private static void setBlockRelative(Location origin, int x,int y,int z, Material mat, Rotation rotation){
        int Z = z;
        int X = x;
        int Y = y;
        BlockFace facing = BlockFace.NORTH;
        switch (rotation){
            case Rotation.NONE:
                Z = z;
                X = x;
                facing = BlockFace.NORTH;
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
        block.setType(mat);
        if (block.getBlockData() instanceof Directional directional){
            directional.setFacing(facing.getOppositeFace());
            block.setBlockData(directional);
        }
    }
}
