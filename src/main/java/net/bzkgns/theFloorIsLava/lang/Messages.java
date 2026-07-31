package net.bzkgns.theFloorIsLava.lang;

import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.bzkgns.theFloorIsLava.utils.SoundUtils.*;

public final class Messages {

    private Messages(){}

    public static Component component(Audience audience,
                                      String key,
                                      TagResolver... placeholders) {

        return LangManager.getInstance().get(audience, key, placeholders);

    }

    public static String string(Audience audience,
                                      String key,
                                      TagResolver... placeholders) {

        return TextUtils.plainText(LangManager.getInstance().get(audience, key, placeholders));

    }

    public static void send(Audience audience,
                            String key,
                            TagResolver... placeholders) {
        audience.sendMessage(component(audience, key, placeholders));
    }

    public static void sendPing(Audience audience,
                            String key,
                            TagResolver... placeholders) {
        if (audience instanceof Player player)
            playPing(player);
        send(audience, key, placeholders);
    }

    public static void sendAlert(Audience audience,
                                String key,
                                TagResolver... placeholders) {
        if (audience instanceof Player player)
            playAlert(player);
        send(audience, key, placeholders);
    }

    public static void sendError(Audience audience,
                                String key,
                                TagResolver... placeholders) {
        if (audience instanceof Player player)
            playError(player);
        send(audience, key, placeholders);
    }

    public static void actionBar(Player player,
                                 String key,
                                 TagResolver... placeholders) {

        player.sendActionBar(component(player, key, placeholders));

    }

    public static void broadcastActionBar(String key,
                                        TagResolver... placeholders) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            player.sendActionBar(component(player, key, placeholders));

        }

    }

    public static void broadcastPing(String key,
                                 TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playPing(player);
            player.sendMessage(component(player, key, placeholders));
        }
    }
    public static void broadcastError(String key,
                                     TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendError(player, key, placeholders);
        }
    }

    public static void broadcast(String key,
                                 TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, key, placeholders);
        }
    }

    public static void broadcastTeam(@NotNull TeamData team, @NotNull String key,
                                     TagResolver... placeholders) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                send(player, key, placeholders);
        }
    }

    public static void broadcastTeamPing(@NotNull TeamData team, @NotNull String key,
                                     TagResolver... placeholders) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null){
                sendPing(player, key, placeholders);
            }
        }
    }

    public static void broadcastTeamAlert(@NotNull TeamData team, @NotNull String key,
                                         TagResolver... placeholders) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null){
                sendAlert(player, key, placeholders);
            }
        }
    }

    public static void broadcastOp(String key,
                                 TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp())
                send(player, key, placeholders);
        }
    }

    public static void broadcastOpPing(String key,
                                 TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                sendPing(player, key, placeholders);
            }
        }
    }

    public static void broadcastOpError(String key,
                                        TagResolver... placeholders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                sendError(player, key, placeholders);
            }
        }
    }

}
