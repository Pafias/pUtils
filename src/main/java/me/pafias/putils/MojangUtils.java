package me.pafias.putils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class MojangUtils {

    private static final Map<UUID, MojangPlayer> cacheByUuid = new WeakHashMap<>();
    private static final Map<String, MojangPlayer> cacheByName = new WeakHashMap<>();

    public static MojangPlayer getMojangPlayerByInput(String input) {
        if (input == null || input.isEmpty()) return null;
        try {
            UUID uuid = UUID.fromString(input);
            return getMojangPlayer(uuid);
        } catch (IllegalArgumentException ex) {
            return getMojangPlayer(input);
        }
    }

    public static MojangPlayer getMojangPlayer(UUID uuid) {
        if (uuid == null) return null;
        if (cacheByUuid.containsKey(uuid))
            return cacheByUuid.get(uuid);
        try {
            MojangPlayer p = getMojangPlayer(uuid, null);
            if (p != null) {
                cacheByUuid.put(p.getUniqueId(), p);
                cacheByName.put(p.getName(), p);
            }
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static MojangPlayer getMojangPlayer(String name) {
        if (name == null || name.isEmpty()) return null;
        if (cacheByName.containsKey(name))
            return cacheByName.get(name);
        try {
            MojangPlayer p = getMojangPlayer(null, name);
            if (p != null) {
                cacheByUuid.put(p.getUniqueId(), p);
                cacheByName.put(p.getName(), p);
            }
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static MojangPlayer getMojangPlayer(@Nullable UUID uuid, @Nullable String name) {
        if (uuid == null && name == null) return null;
        try { // Try Mojang API
            URL url;
            if (uuid != null)
                url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            else
                url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:139.0) Gecko/20100101 Firefox/139.0");
            connection.setConnectTimeout(5000); // 5 second timeout
            connection.setReadTimeout(5000);

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String uuidWithoutDashes = json.get("id").getAsString();
                String finalName = json.get("name").getAsString();
                UUID finalUuid = UUID.fromString(uuidWithoutDashes.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5"
                ));
                return new MojangPlayer(finalUuid, finalName);
            }
        } catch (FileNotFoundException e) {
            // Player does not exist
            return null;
        } catch (Exception e) {
            try { // Try alternative API
                URL url = new URL(String.format("https://playerdb.co/api/player/minecraft/%s", uuid != null ? uuid.toString() : name));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:139.0) Gecko/20100101 Firefox/139.0");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    if (root.get("code").getAsString().equals("player.found")) {
                        JsonObject playerObject = root.getAsJsonObject("data").getAsJsonObject("player");
                        UUID finalUuid = UUID.fromString(playerObject.get("id").getAsString());
                        String finalName = playerObject.get("username").getAsString();
                        return new MojangPlayer(finalUuid, finalName);
                    }
                    return null; // API responded but didn't find the player.
                }
            } catch (Exception ex) {
                // Give up if both APIs fail
                return null;
            }
        }
    }

    public static UUID getUUID(String name) {
        MojangPlayer p = getMojangPlayer(name);
        if (p == null) return null;
        return p.getUniqueId();
    }

    public static String getName(UUID uuid) {
        MojangPlayer p = getMojangPlayer(uuid);
        if (p == null) return null;
        return p.getName();
    }

}
