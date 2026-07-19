package net.bzkgns.theFloorIsLavaManager.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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

    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

}
