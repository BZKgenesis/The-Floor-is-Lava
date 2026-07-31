package net.bzkgns.theFloorIsLava.utils;

import net.bzkgns.theFloorIsLava.lang.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;

public class TextUtils {
    public enum TimeFormat {
        SHORTEST_CLOCK_LIKE,
        SHORTEST,
        SECONDS,
        SECONDS_PRECISE,
        MINUTES_SECONDS,
        MINUTES_SECONDS_MILLISECONDS
    }
    public static String formatTime(int ticks, TimeFormat format){
        return formatTime(Bukkit.getServer(), ticks, format);
    }
    public static String formatTime(Audience audience, int ticks, TimeFormat format) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        int milliseconds = (ticks % 20) * 50;

        switch (format) {
            case SHORTEST_CLOCK_LIKE:
                if (minutes > 0) {
                    return String.format("%d:%02d", minutes, seconds);
                } else {
                    return String.format("%d", seconds);
                }
            case SHORTEST:
                if (minutes > 0) {
                    if (minutes > 1) {
                        return Messages.string(audience, "time.minutes", Placeholder.unparsed("time", String.valueOf(minutes)));
                    }
                    return Messages.string(audience, "time.minute");
                } else {
                    if (seconds > 1) {
                        return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", String.valueOf(seconds)));
                    }
                    return Messages.string(audience, "time.second");
                }
            case SECONDS:
                if (totalSeconds > 1) {
                    return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", String.valueOf(totalSeconds)));
                }
                return Messages.string(audience, "time.second");
            case SECONDS_PRECISE:
                String formattedTime = String.format("%d.%03d", totalSeconds, milliseconds);
                return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", formattedTime));
            case MINUTES_SECONDS:
                if (minutes > 0) {
                    if (minutes > 1) {
                        return Messages.string(audience, "time.minutes_and_seconds", Placeholder.unparsed("minutes", String.valueOf(minutes)), Placeholder.unparsed("seconds", String.valueOf(seconds)));
                    }
                    if (seconds > 1) {
                        return Messages.string(audience, "time.minute_and_seconds", Placeholder.unparsed("seconds", String.valueOf(seconds)));
                    }
                    return Messages.string(audience, "time.minute_and_second");
                } else {
                    if (seconds > 1) {
                        return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", String.valueOf(seconds)));
                    }
                    return Messages.string(audience, "time.second");
                }
            case MINUTES_SECONDS_MILLISECONDS:
                String formattedTime2 = String.format("%d.%03d", seconds, milliseconds);
                if (minutes > 0) {
                    if (minutes > 1) {
                        return Messages.string(audience, "time.minutes_and_seconds", Placeholder.unparsed("minutes", String.valueOf(minutes)), Placeholder.unparsed("seconds",formattedTime2));
                    }
                    if (seconds > 1) {
                        return Messages.string(audience, "time.minute_and_seconds", Placeholder.unparsed("seconds", formattedTime2));
                    }
                    return Messages.string(audience, "time.minute_and_second");
                } else {
                    if (seconds > 1) {
                        return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", formattedTime2));
                    }
                    return Messages.string(audience, "time.seconds", Placeholder.unparsed("time", formattedTime2));
                }
            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }

    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String autoClean(Number text) {
        return switch (text) {
            case null -> "";
            case Integer _ -> String.valueOf(text.intValue());
            case Double v -> String.valueOf(autoCleanDouble(v));
            case Float v -> String.valueOf(autoCleanFloat(v));
            case Long l -> String.valueOf(autoCleanDouble(l));
            default -> text.toString();
        };
    }

    private static float autoCleanFloat(float valeur) {
        return (float) autoCleanDouble(valeur);
    }

    private static double autoCleanDouble(double valeur) {
        if (Double.isNaN(valeur) || Double.isInfinite(valeur) || valeur == 0) {
            return valeur;
        }

        // 1. Convertir en texte
        String text = String.valueOf(valeur);

        // 2. Si on détecte une longue suite de '0' (ex: 0.20000007851 -> 0.2)
        text = text.replaceAll("(0{4,})[0-9]*$", "");

        // 3. Si on détecte une longue suite de '9' (ex: 0.01249999994 -> 0.0125)
        // On remplace la séquence de 9 par un arrondi propre
        if (text.contains("9999")) {
            // On utilise un formatage court pour résorber les .9999
            text = String.format(java.util.Locale.US, "%.8f", valeur)
                    .replaceAll("0+$", "")
                    .replaceAll("\\.$", "");
        }

        // Supprime un éventuel point décimal résiduel à la fin
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }

        return Double.parseDouble(text);
    }

}
