package net.bzkgns.theFloorIsLavaManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.HexFormat;

public class ResourcePackManager {
    private final TheFloorIsLavaManager plugin;

    public ResourcePackManager(TheFloorIsLavaManager _plugin){
        plugin = _plugin;
    }

    private static final String URL =
            "https://github.com/BZKgenesis/TheFloorIsLava-ResourcePack/releases/download/v0.0.1/resourcepack.zip";

    private String sha1;

    public void load() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();

        HttpResponse<byte[]> response =
                client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        plugin.getLogger().info(String.valueOf(response.statusCode()));
        plugin.getLogger().info(String.valueOf(response.headers().firstValue("content-type")));

        byte[] pack = response.body();
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] hash = md.digest(pack);
        sha1 = HexFormat.of().formatHex(hash);
        // téléchargement
        // calcul SHA1
        // stockage dans sha1
    }

    public String getUrl() {
        return URL;
    }

    public String getSha1() {
        return sha1;
    }
}
