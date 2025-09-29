package me.pafias.putils.builders;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.pafias.putils.pUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerProfileBuilder {

    private UUID uuid;
    private boolean generateUuidFromName = true;
    private String name;
    private boolean fetchProperties;
    private Set<ProfileProperty> properties;
    private String skinUrl;

    public PlayerProfileBuilder() {
    }

    public PlayerProfileBuilder setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public PlayerProfileBuilder randomUuid() {
        this.uuid = UUID.randomUUID();
        return this;
    }

    public PlayerProfileBuilder setGenerateUuidFromName(boolean generateUuidFromName) {
        this.generateUuidFromName = generateUuidFromName;
        return this;
    }

    public PlayerProfileBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlayerProfileBuilder setFetchProperties(boolean fetchProperties) {
        this.fetchProperties = fetchProperties;
        return this;
    }

    public PlayerProfileBuilder setProperties(Set<ProfileProperty> properties) {
        this.properties = properties;
        return this;
    }

    public PlayerProfileBuilder setSkinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
        return this;
    }

    public PlayerProfile build() {
        if (uuid == null && name != null && generateUuidFromName)
            uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        PlayerProfile profile = pUtils.getPlugin().getServer().createProfile(uuid, name);
        if (uuid != null)
            profile.setId(uuid);
        if (name != null)
            profile.setName(name);
        if (properties != null)
            profile.setProperties(properties);
        if (fetchProperties)
            profile.complete(skinUrl == null);
        if (skinUrl != null) {
            HttpURLConnection connection = null;
            try {
                URL apiUrl = new URL("https://api.mineskin.org/generate/url");
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStream outStream = connection.getOutputStream();
                DataOutputStream out = new DataOutputStream(outStream);
                out.writeBytes("{\"url\":\"" + skinUrl + "\"}");
                out.flush();
                out.close();
                outStream.close();

                try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
                    if (obj.has("error")) return null;
                    if (!obj.has("data")) return null;
                    JsonObject texture = obj.get("data").getAsJsonObject().get("texture").getAsJsonObject();
                    profile.setProperty(new ProfileProperty("textures", texture.get("value").getAsString(), texture.get("signature").getAsString()));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }
        return profile;
    }

    public CompletableFuture<PlayerProfile> buildAsync() {
        return CompletableFuture.supplyAsync(this::build);
    }

}
