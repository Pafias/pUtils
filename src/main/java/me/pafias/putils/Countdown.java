package me.pafias.putils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Countdown implements BukkitTask {

    private static final JavaPlugin plugin = pUtils.getPlugin();

    public int seconds;
    public int secondsLeft;
    private final BukkitRunnable runnable;
    private Runnable whenFinished;
    private Consumer<Countdown> everySecond;


    private boolean sync;
    private boolean finished;

    public Countdown(int seconds) {
        this.seconds = seconds;
        this.secondsLeft = seconds;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    cancel();
                    whenFinished.run();
                    finished = true;
                }
                everySecond.accept(get());
                secondsLeft--;
            }
        };
    }

    public Countdown get() {
        return this;
    }

    public Countdown start() {
        sync = true;
        runnable.runTaskTimer(plugin, 2, 20);
        return this;
    }

    public Countdown startAsync() {
        sync = false;
        runnable.runTaskTimerAsynchronously(plugin, 2, 20);
        return this;
    }

    public Countdown whenFinished(Runnable runnable) {
        this.whenFinished = runnable;
        return this;
    }

    public Countdown everySecond(Consumer<Countdown> consumer) {
        this.everySecond = consumer;
        return this;
    }

    @Override
    public int getTaskId() {
        return runnable.getTaskId();
    }

    @Override
    public @NotNull Plugin getOwner() {
        return plugin;
    }

    @Override
    public boolean isSync() {
        return sync;
    }

    @Override
    public boolean isCancelled() {
        return runnable.isCancelled() || finished;
    }

    @Override
    public void cancel() {
        runnable.cancel();
    }
}