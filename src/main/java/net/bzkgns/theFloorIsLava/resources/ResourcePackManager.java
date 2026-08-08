package net.bzkgns.theFloorIsLava.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.config.game.GameConfigKeys;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.logging.Level;

public class ResourcePackManager {
    private final TheFloorIsLava plugin;

    public ResourcePackManager(TheFloorIsLava _plugin){
        plugin = _plugin;
    }

    private static final String API_URL =
            "https://api.github.com/repos/BZKgenesis/TheFloorIsLava-ResourcePack/releases/latest";

    private String downloadUrl;
    private String sha1;

    public void load() {
        Boolean useCustomResourcePack = ConfigRegistry.getConfigManager("game")
                .getBoolean(GameConfigKeys.USE_RESOURCE_PACK_OVERRIDE.getKey());
        String resourcePackUrl = ConfigRegistry.getConfigManager("game")
                .getString(GameConfigKeys.RESOURCE_PACK_URL_OVERRIDE.getKey());
        String resourcePackSha1 = ConfigRegistry.getConfigManager("game")
                .getString(GameConfigKeys.RESOURCE_PACK_SHA1_OVERRIDE.getKey());
        if (useCustomResourcePack){
            if (resourcePackUrl != null && !resourcePackUrl.isEmpty() &&
                    resourcePackSha1 != null && !resourcePackSha1.isEmpty()) {
                downloadUrl = resourcePackUrl;
                sha1 = resourcePackSha1;
                plugin.getLogger().info("Using overridden resource pack URL and SHA1 from config.");
                return;
            }else{
                plugin.getLogger().warning("Resource pack override is enabled, but URL or SHA1 is missing. Falling back to GitHub release.");
            }

        }
        try(HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {

            HttpRequest releaseRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept", "application/vnd.github+json")
                    .build();

            HttpResponse<String> releaseResponse =
                    client.send(
                            releaseRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if(releaseResponse.statusCode() != 200){
                throw new RuntimeException(
                        "GitHub API error: " + releaseResponse.statusCode()
                );
            }

            JsonObject release =
                    JsonParser.parseString(releaseResponse.body())
                            .getAsJsonObject();

            JsonArray assets =
                    release.getAsJsonArray("assets");

            if(assets.isEmpty()){
                throw new RuntimeException(
                        "No asset found in latest release"
                );
            }

            JsonObject asset = assets.get(0).getAsJsonObject();

            downloadUrl =
                    asset.get("browser_download_url")
                            .getAsString();

            plugin.getLogger().info(
                    "Resource pack URL: " + downloadUrl
            );


            // zip file download
            HttpRequest downloadRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(downloadUrl))
                            .build();

            HttpResponse<byte[]> downloadResponse =
                    client.send(
                            downloadRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            if(downloadResponse.statusCode() != 200){
                throw new RuntimeException(
                        "Download failed: "
                                + downloadResponse.statusCode()
                );
            }

            byte[] pack = downloadResponse.body();

            // SHA1 hash calculation
            MessageDigest md =
                    MessageDigest.getInstance("SHA-1");

            byte[] hash = md.digest(pack);
            sha1 = HexFormat.of()
                    .formatHex(hash);
            plugin.getLogger().info(
                    "Resource pack SHA1: " + sha1
            );

        } catch(Exception e){
            plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to load resource pack",
                    e
            );
        }
    }

    @Nullable
    public String getUrl() {
        return downloadUrl;
    }

    @Nullable
    public String getSha1() {
        return sha1;
    }
}
