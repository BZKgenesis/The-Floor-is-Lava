package net.bzkgns.theFloorIsLavaManager.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
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
    public static Component warningMessage(String message, boolean withPrefix){
        if (!withPrefix) return Component.text(message).color(TextColor.color(255,255,0));
        return prefix().append(Component.text(message).color(TextColor.color(255,255,0)));
    }
    @SuppressWarnings("unused")
    public static Component warningMessage(String message){
        return warningMessage(message, true);
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

    /**
     * black
     * @param message message formatted in black color
     * @return text component with black color
     */
    public static Component text0(String message){
        return Component.text(message).color(TextColor.color(0,0,0));
    }

    /**
     * dark_blue
     * @param message message formatted in dark blue color
     * @return text component with dark blue color
     */
    public static Component text1(String message){
        return Component.text(message).color(TextColor.fromHexString("#0000AA"));
    }

    /**
     * dark_green
     * @param message message formatted in dark green color
     * @return text component with dark green color
     */
    public static Component text2(String message){
        return Component.text(message).color(TextColor.fromHexString("#00AA00"));
    }

    /**
     * dark_aqua
     * @param message message formatted in dark aqua color
     * @return text component with dark aqua color
     */
    public static Component text3(String message){
        return Component.text(message).color(TextColor.fromHexString("#00AAAA"));
    }

    /**
     * dark_red
     * @param message message formatted in dark red color
     * @return text component with dark red color
     */
    public static Component text4(String message){
        return Component.text(message).color(TextColor.fromHexString("#AA0000"));
    }

    /**
     * dark_purple
     * @param message message formatted in dark purple color
     * @return text component with dark purple color
     */
    public static Component text5(String message){
        return Component.text(message).color(TextColor.fromHexString("#AA00AA"));
    }

    /**
     * gold
     * @param message message formatted in gold color
     * @return text component with gold color
     */
    public static Component text6(String message){
        return Component.text(message).color(TextColor.fromHexString("#FFAA00"));
    }

    /**
     * gray
     * @param message message formatted in gray color
     * @return text component with gray color
     */
    public static Component text7(String message){
        return Component.text(message).color(TextColor.fromHexString("#AAAAAA"));
    }

    /**
     * dark_gray
     * @param message message formatted in dark gray color
     * @return text component with dark gray color
     */
    public static Component text8(String message){
        return Component.text(message).color(TextColor.fromHexString("#555555"));
    }

    /**
     * blue
     * @param message message formatted in blue color
     * @return text component with blue color
     */
    public static Component text9(String message){
        return Component.text(message).color(TextColor.fromHexString("#5555FF"));
    }

    /**
     * green
     * @param message message formatted in green color
     * @return text component with green color
     */
    public static Component textA(String message){
        return Component.text(message).color(TextColor.fromHexString("#55FF55"));
    }

    /**
     * aqua
     * @param message message formatted in aqua color
     * @return text component with aqua color
     */
    public static Component textB(String message){
        return Component.text(message).color(TextColor.fromHexString("#55FFFF"));
    }

    /**
     * red
     * @param message message formatted in red color
     * @return text component with red color
     */
    public static Component textC(String message){
        return Component.text(message).color(TextColor.fromHexString("#FF5555"));
    }

    /**
     * light_purple
     * @param message message formatted in light purple color
     * @return text component with light purple color
     */
    public static Component textD(String message){
        return Component.text(message).color(TextColor.fromHexString("#FF55FF"));
    }

    /**
     * yellow
     * @param message message formatted in yellow color
     * @return text component with yellow color
     */
    public static Component textE(String message){
        return Component.text(message).color(TextColor.fromHexString("#FFFF55"));
    }

    /**
     * white
     * @param message message formatted in white color
     * @return text component with white color
     */
    public static Component textF(String message){
        return Component.text(message).color(TextColor.fromHexString("#FFFFFF"));
    }
}
