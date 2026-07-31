package net.bzkgns.theFloorIsLava.vein_miner;

import io.papermc.paper.entity.PlayerGiveResult;
import net.bzkgns.theFloorIsLava.listener.AutoSmelt;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class VeinMinerListener implements Listener {

    private final static BlockType[] veinMinerMaterials = {
            BlockType.DIAMOND_ORE,
            BlockType.DEEPSLATE_DIAMOND_ORE,
            BlockType.IRON_ORE,
            BlockType.DEEPSLATE_IRON_ORE,
            BlockType.GOLD_ORE,
            BlockType.DEEPSLATE_GOLD_ORE,
            BlockType.REDSTONE_ORE,
            BlockType.DEEPSLATE_REDSTONE_ORE,
            BlockType.LAPIS_ORE,
            BlockType.DEEPSLATE_LAPIS_ORE,
            BlockType.COPPER_ORE,
            BlockType.DEEPSLATE_COPPER_ORE,
            BlockType.COAL_ORE,
            BlockType.DEEPSLATE_COAL_ORE,
            BlockType.EMERALD_ORE,
            BlockType.DEEPSLATE_EMERALD_ORE,
    };

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!isVeinMinerMaterial(block.getType().asBlockType())) {
            return;
        }
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return;
        }
        PlayerGiveResult giveResult = player.give(AutoSmelt.autoSmeltOre(breakVein(block, player, tool, block.getType().asBlockType(), 10)));
//        for (ItemStack stack : giveResult.leftovers()) {
//            player.getWorld().dropItemNaturally(
//                    player.getLocation(),
//                    stack
//            );
//        }
    }

    public static boolean isVeinMinerMaterial(BlockType blockType) {
        if (blockType == null) {
            return false;
        }
        for (BlockType veinMinerMaterial : veinMinerMaterials) {
            if (blockType.equals(veinMinerMaterial)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public Collection<ItemStack> breakVein(Block block, Player player, ItemStack tool, BlockType type, Integer depth) {
        if (depth <= 0) {
            return List.of();
        }
        if (!isVeinMinerMaterial(block.getType().asBlockType())) {
            return List.of();
        }
        if (!block.getType().asBlockType().equals(type)) {
            return List.of();
        }
        tool.damage(1, player);
        if (tool.getAmount() <= 0) {
            return List.of();
        }
        if (!block.isPreferredTool(tool)) return List.of();

        Collection<ItemStack> items = block.getDrops(tool, player);
        player.spawnParticle(Particle.BLOCK, block.getLocation().add(0.5, 0.5, 0.5), 30, 0.3, 0.3, 0.3, block.getBlockData());
        player.playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
        block.setType(Material.AIR);

        //direct
        items.addAll(breakVein(block.getRelative( 1, 0, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1, 0, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0, 1, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0,-1, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0, 0, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0, 0,-1), player, tool, type, depth - 1));

        //diagonal
        items.addAll(breakVein(block.getRelative( 1, 1, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( -1, 1, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 1, -1, 0), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( -1, -1, 0), player, tool, type, depth - 1));

        items.addAll(breakVein(block.getRelative(1, 0, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1, 0, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(1, 0, -1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1, 0, -1), player, tool, type, depth - 1));

        items.addAll(breakVein(block.getRelative( 0,1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0,-1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0,1, -1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 0,-1, -1), player, tool, type, depth - 1));

        //3D diagonal
        items.addAll(breakVein(block.getRelative( 1, 1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1, 1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 1,-1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 1, 1,-1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative( 1,-1,-1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1, 1,-1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1,-1, 1), player, tool, type, depth - 1));
        items.addAll(breakVein(block.getRelative(-1,-1,-1), player, tool, type, depth - 1));
        return items;

    }
}
