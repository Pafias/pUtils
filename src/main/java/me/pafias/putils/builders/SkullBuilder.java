package me.pafias.putils.builders;

import me.pafias.putils.BukkitPlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkullBuilder {

    private final OfflinePlayer owner;
    private final String ownerName;
    private int amount = 1;
    private Component name;
    private List<Component> lore;

    public SkullBuilder(@NotNull OfflinePlayer player) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null");
        owner = player;
        ownerName = player.getName();
    }

    public SkullBuilder(@NotNull String playerName) {
        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("Player name cannot be null or empty");
        ownerName = playerName;
        owner = null;
    }

    public SkullBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SkullBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public SkullBuilder setName(String name) {
        this.name = Component.text(name);
        return this;
    }

    public SkullBuilder setLore(Component... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public SkullBuilder setLore(String... lore) {
        this.lore = Arrays.asList(Arrays.stream(lore)
                .map(Component::text)
                .toArray(Component[]::new));
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (owner != null)
            meta.setOwningPlayer(owner);
        else if (ownerName != null)
            meta.setOwningPlayer(BukkitPlayerManager.getOfflinePlayerByName(ownerName));
        is.setItemMeta(meta);
        return is;
    }

    public CompletableFuture<ItemStack> buildAsync() {
        return CompletableFuture.supplyAsync(this::build);
    }

}
