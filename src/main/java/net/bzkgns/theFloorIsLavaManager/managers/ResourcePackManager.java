package net.bzkgns.theFloorIsLavaManager.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.logging.Level;

public class ResourcePackManager {
    private final TheFloorIsLavaManager plugin;

    public ResourcePackManager(TheFloorIsLavaManager _plugin){
        plugin = _plugin;
    }

    private static final String API_URL =
            "https://api.github.com/repos/BZKgenesis/TheFloorIsLava-ResourcePack/releases/latest";

    private String downloadUrl;
    private String sha1;

    public void load() {
        try(HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {


            // 1 Récupération des informations de la release
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

            // Premier fichier attaché à la release
            JsonObject asset = assets.get(0).getAsJsonObject();

            downloadUrl =
                    asset.get("browser_download_url")
                            .getAsString();

            plugin.getLogger().info(
                    "Resource pack URL: " + downloadUrl
            );


            // 2 Téléchargement du zip
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

            // 3 Calcul SHA1
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

    public String getUrl() {
        return downloadUrl;
    }

    public String getSha1() {
        return sha1;
    }
}
