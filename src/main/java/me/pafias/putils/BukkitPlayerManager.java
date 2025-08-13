package me.pafias.putils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayerManager {

    private static final Map<UUID, OfflinePlayer> cacheByUuid = new WeakHashMap<>();
    private static final Map<String, OfflinePlayer> cacheByName = new WeakHashMap<>();

    public static OfflinePlayer getOfflinePlayerByName(String name) {
        if (Bukkit.getPlayer(name) != null) return Bukkit.getPlayer(name);
        if (cacheByName.containsKey(name)) return cacheByName.get(name);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        cacheByName.put(name, offlinePlayer);
        return offlinePlayer;
    }

    /**
     * Getting an offline player by their UUID is tricky, because the Bukkit API only returns a full OfflinePlayer object if the player has logged in before.
     * If the player has never joined before, the OfflinePlayer object will be incomplete and stuff like the name will be null.
     * The player's name is pretty crucial for us, so...
     * In this case, we need to fetch the player's name from Mojang and then get the player from Bukkit by their name.
     * The getOfflinePlayer method using the name, will have the server fetch the whole player from Mojang and return a valid OfflinePlayer object which we can happily use!
     */
    public static OfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) return Bukkit.getPlayer(uuid);
        if (cacheByUuid.containsKey(uuid)) return cacheByUuid.get(uuid);

        OfflinePlayer cached = Bukkit.getOfflinePlayer(uuid);
        if (cached.getName() == null) {
            OfflinePlayer offlinePlayer = cached;
            MojangPlayer mojangPlayer = MojangUtils.getMojangPlayer(uuid);
            if (mojangPlayer != null) {
                offlinePlayer = getOfflinePlayerByName(mojangPlayer.getName());
                if (offlinePlayer.getUniqueId().equals(mojangPlayer.getUniqueId()))
                    cacheByUuid.put(uuid, offlinePlayer);
            }
            return offlinePlayer;
        } else {
            cacheByUuid.put(uuid, cached);
            return cached;
        }
    }

    public static OfflinePlayer getOfflinePlayerByInput(String input) {
        try {
            UUID uuid = UUID.fromString(input);
            return getOfflinePlayerByUUID(uuid);
        } catch (IllegalArgumentException e) {
            return getOfflinePlayerByName(input);
        }
    }

}
