package net.bzkgns.theFloorIsLavaManager.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextUtils {
    public enum TimeFormat {
        SHORTEST,
        SECONDS,
        SECONDS_PRECISE,
        MINUTES_SECONDS,
        MINUTES_SECONDS_MILLISECONDS
    }
    public static String formatTime(int ticks, TimeFormat format) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        int milliseconds = (ticks % 20) * 50;

        switch (format) {
            case SHORTEST:
                if (minutes > 0) {
                    if (minutes > 1) {
                        return String.format("%d minutes", minutes);
                    }
                    return String.format("%d minute", minutes);
                } else {
                    if (seconds > 1) {
                        return String.format("%d secondes", seconds);
                    }
                    return String.format("%d seconde", seconds);
                }
            case SECONDS:
                return String.format("%d secondes", totalSeconds);
            case SECONDS_PRECISE:
                return String.format("%d.%03d secondes", totalSeconds, milliseconds);
            case MINUTES_SECONDS:
                return String.format("%d minutes et %d secondes", minutes, seconds);
            case MINUTES_SECONDS_MILLISECONDS:
                return String.format("%d minutes %d.%03d secondes", minutes, seconds, milliseconds);
            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }

    public static void sendMessage(String message){
        Bukkit.getServer().sendMessage(Component.text("[").color(TextColor.color(255,255,255))
                .append(Component.text("TFL").color(TextColor.color(255,0,0)))
                .append(Component.text("]").color(TextColor.color(255,255,255)))
                .append(Component.text(" " +message).color(TextColor.color(255,255,255))));
    }

    public static void sendActionBar(String message){

        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendActionBar(Component.text("[").color(TextColor.color(255,255,255))
                    .append(Component.text("TFL").color(TextColor.color(255,0,0)))
                    .append(Component.text("]").color(TextColor.color(255,255,255)))
                    .append(Component.text(" " +message).color(TextColor.color(255,255,255))));
        }
    }

    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
