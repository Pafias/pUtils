package me.pafias.putils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class BukkitPlayerManager {

    private static final Map<UUID, OfflinePlayer> cacheByUuid = new WeakHashMap<>();
    private static final Map<String, OfflinePlayer> cacheByName = new WeakHashMap<>();

    /**
     * Gets an OfflinePlayer by their name
     *
     * @param name The player's Minecraft username.
     * @return The corresponding OfflinePlayer object, or null if the player could not be found anywhere.
     */
    public static @Nullable OfflinePlayer getOfflinePlayerByName(String name) {
        if (name == null || name.isEmpty()) return null;

        // 1. Check for online player (fastest)
        Player onlinePlayer = pUtils.getPlugin().getServer().getPlayer(name);
        if (onlinePlayer != null) return onlinePlayer;

        // 2. Check our name cache
        if (cacheByName.containsKey(name)) return cacheByName.get(name);

        // 3. Player not found locally. Fetch APIs
        MojangPlayer mojangPlayer = MojangUtils.getMojangPlayer(name);
        if (mojangPlayer != null && mojangPlayer.getUniqueId() != null)
            // API success.
            // This also ensures the player is cached correctly for future lookups.
            return getOfflinePlayerByUUID(mojangPlayer.getUniqueId());

        // 4. All lookups failed.
        return null;
    }

    /**
     * Gets an OfflinePlayer by their UUID
     *
     * @param uuid The player's UUID.
     * @return The corresponding OfflinePlayer object, or null if the UUID could not be resolved by APIs.
     */
    public @Nullable
    static OfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
        if (uuid == null) return null;

        // 1. Check for online player
        Player onlinePlayer = pUtils.getPlugin().getServer().getPlayer(uuid);
        if (onlinePlayer != null)
            return onlinePlayer;

        // 2. Check our UUID cache
        OfflinePlayer cachedPlayer = cacheByUuid.get(uuid);
        if (cachedPlayer != null && cachedPlayer.getName() != null)
            return cachedPlayer;

        // 3. Check the local user cache
        OfflinePlayer offlinePlayer = pUtils.getPlugin().getServer().getOfflinePlayer(uuid);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() != null) {
            // Player is known to this server.
            cache(offlinePlayer);
            return offlinePlayer;
        }

        // 4. All lookups failed.
        return null;
    }

    /**
     * Gets an OfflinePlayer from a string input which could be a name or a UUID.
     *
     * @param input The player's name or UUID as a string.
     * @return An OfflinePlayer object if found, otherwise null.
     */
    public static @Nullable OfflinePlayer getOfflinePlayerByInput(String input) {
        if (input == null || input.isEmpty()) return null;
        try {
            UUID uuid = UUID.fromString(input);
            return getOfflinePlayerByUUID(uuid);
        } catch (IllegalArgumentException e) {
            return getOfflinePlayerByName(input);
        }
    }

    private static void cache(OfflinePlayer player) {
        if (player == null) return;
        cacheByUuid.put(player.getUniqueId(), player);
        if (player.getName() != null)
            cacheByName.put(player.getName().toLowerCase(), player);
    }

}
