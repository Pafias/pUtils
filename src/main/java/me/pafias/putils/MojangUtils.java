package me.pafias.putils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.OfflinePlayer;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class MojangUtils {

    private static final Map<UUID, MojangPlayer> cacheByUuid = new WeakHashMap<>();
    private static final Map<String, MojangPlayer> cacheByName = new WeakHashMap<>();

    public static MojangPlayer getMojangPlayerByInput(String input) {
        try {
            UUID uuid = UUID.fromString(input);
            return getMojangPlayer(uuid);
        } catch (IllegalArgumentException ex) {
            return getMojangPlayer(input);
        }
    }

    public static MojangPlayer getMojangPlayer(UUID uuid) {
        if (cacheByUuid.containsKey(uuid))
            return cacheByUuid.get(uuid);
        try {
            MojangPlayer p = getMojangPlayer(uuid, null);
            if (p.isMojangApiSuccess()) {
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
        if (cacheByName.containsKey(name))
            return cacheByName.get(name);
        try {
            MojangPlayer p = getMojangPlayer(null, name);
            if (p.isMojangApiSuccess()) {
                cacheByUuid.put(p.getUniqueId(), p);
                cacheByName.put(p.getName(), p);
            }
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static MojangPlayer getMojangPlayer(UUID uuid, String name) {
        try { // Try Mojang API
            URL url;
            if (uuid != null)
                url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            else
                url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject();
            String uuidWithoutDashes = json.get("id").getAsString();
            String formattedUuid = String.format(
                    "%8s-%4s-%4s-%4s-%12s",
                    uuidWithoutDashes.substring(0, 8),
                    uuidWithoutDashes.substring(8, 12),
                    uuidWithoutDashes.substring(12, 16),
                    uuidWithoutDashes.substring(16, 20),
                    uuidWithoutDashes.substring(20)
            );
            final UUID finalUuid = UUID.fromString(formattedUuid);
            final String finalName = json.get("name").getAsString();

            return new MojangPlayer(finalUuid, finalName, true);
        } catch (Exception e) {
            try { // Try alternative API
                URL url = new URL(String.format("https://playerdb.co/api/player/minecraft/%s", uuid != null ? uuid.toString() : name));
                JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject();
                final UUID finalUuid = UUID.fromString(json.get("id").getAsString());
                final String finalName = json.get("username").getAsString();

                return new MojangPlayer(finalUuid, finalName, false);
            } catch (Exception ex) {
                // If neither works, just get it locally
                OfflinePlayer player;
                if (uuid != null)
                    player = BukkitPlayerManager.getOfflinePlayerByUUID(uuid);
                else
                    player = BukkitPlayerManager.getOfflinePlayerByName(name);

                return new MojangPlayer(player.getUniqueId(), player.getName(), false);
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
