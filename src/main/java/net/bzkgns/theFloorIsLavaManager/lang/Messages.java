package net.bzkgns.theFloorIsLavaManager.lang;

import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

    public static void broadcast(String key,
                                 TagResolver... placeholders) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            player.sendMessage(component(player, key, placeholders));

        }

    }

    public static void broadcastTeam(@NotNull TeamData team, @NotNull String key,
                                     TagResolver... placeholders) {

        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(component(player, key, placeholders));

        }

    }

    public static void broadcastOp(String key,
                                 TagResolver... placeholders) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp())
                player.sendMessage(component(player, key, placeholders));

        }

    }

}
