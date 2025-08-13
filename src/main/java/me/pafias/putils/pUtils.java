package me.pafias.putils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public final class pUtils {

    @Getter
    private static JavaPlugin plugin;

    /**
     * You are supposed to call this method in your plugin's onEnable method, with your plugin as the parameter, if you are including pUtils as a dependency.
     *
     * @param plugin the instance of your plugin
     */
    public static void setPlugin(JavaPlugin plugin) {
        pUtils.plugin = plugin;
    }

    public Double getVersion() {
        try {
            Method getMinecraftVersion = plugin.getServer().getClass().getMethod("getMinecraftVersion");
            if (getMinecraftVersion.getReturnType() == String.class) {
                String versionString = (String) getMinecraftVersion.invoke(plugin.getServer());
                return Double.parseDouble(versionString.substring(2));
            }
        } catch (Exception legacy) {
            String bukkitVersion = plugin.getServer().getBukkitVersion();
            String versionString = bukkitVersion.split("-")[0];
            return Double.parseDouble(versionString.substring(2));
        }
        return null;
    }

}
