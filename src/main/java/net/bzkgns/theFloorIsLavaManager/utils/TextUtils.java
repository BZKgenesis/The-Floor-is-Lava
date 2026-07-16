package net.bzkgns.theFloorIsLavaManager.utils;

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
                System.out.println("minutes: " + minutes + ", seconds: " + seconds);
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

    public static Component prefix(){
        return Component.text("[").color(TextColor.color(255,255,255))
                .append(Component.text("TFL").color(TextColor.color(255,0,0)))
                .append(Component.text("] ").color(TextColor.color(255,255,255)));
    }
    public static void broadcastMessage(Component message){
        Bukkit.getServer().sendMessage(message);
    }

    public static void broadcastMessageOp(Component message){
        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            if (player.isOp()){
                player.sendMessage(message);
            }
        }
    }

    public static void sendActionBar(Component component){
        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendActionBar(component);
        }
    }

    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @SuppressWarnings("unused")
    public static Component warningMessage(String message){
        return prefix().append(Component.text(message).color(TextColor.color(255,255,0)));
    }

    public static Component errorMessage(String message){
        return errorMessage(message, true);
    }
    public static Component errorMessage(String message, boolean withPrefix){
        if (!withPrefix) return Component.text(message).color(TextColor.color(255,0,0));
        return prefix().append(Component.text(message).color(TextColor.color(255,0,0)));
    }
    public static Component infoMessage(String message){
        return prefix().append(Component.text(message).color(TextColor.color(255,255,255)));
    }

    public static Component validationMessage(String message){
        return validationMessage(message, true);
    }
    @SuppressWarnings("unused")
    public static Component validationMessage(String message, boolean withPrefix){
        return prefix().append(Component.text(message).color(TextColor.color(0,255,0)));
    }
}
