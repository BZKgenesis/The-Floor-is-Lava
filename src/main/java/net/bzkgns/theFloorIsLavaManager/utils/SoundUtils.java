package net.bzkgns.theFloorIsLavaManager.utils;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {


    public static void playPing(Player player){
        player.playSound(player.getLocation(), "minecraft:entity.experience_orb.pickup", 0.5f, 1.0f);
    }
    public static void playAlert(Player player){
        player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 0.5f, 1.0f);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                () -> player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 0.5f, 1.0f), 2L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheFloorIsLavaManager.getInstance(),
                () -> player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 0.5f, 1.0f), 4L);
    }
    public static void playError(Player player){
        player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 0.5f, 1.0f);
    }
    public static void playNo(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
    }
}
