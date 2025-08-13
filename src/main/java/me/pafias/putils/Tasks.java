package me.pafias.putils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Tasks {

    private static final JavaPlugin plugin = pUtils.getPlugin();

    public static BukkitTask runSync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public static BukkitTask runAsync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static BukkitTask runLaterSync(long delay, Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static BukkitTask runLaterAsync(long delay, Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static BukkitTask runRepeatingSync(long delay, long period, Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    public static BukkitTask runRepeatingAsync(long delay, long period, Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

}
