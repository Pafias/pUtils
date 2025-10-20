package me.pafias.putils.builders;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class LegacySkullBuilder {

    private final SkullType skullType;
    private final String owner;
    private int amount = 1;
    private String name;
    private List<String> lore;
    private Set<ItemFlag> itemflags;

    public LegacySkullBuilder(OfflinePlayer player) {
        skullType = SkullType.PLAYER;
        owner = player.getName();
    }

    public LegacySkullBuilder(String playerName) {
        skullType = SkullType.PLAYER;
        owner = playerName;
    }

    public LegacySkullBuilder(SkullType skullType) {
        if (skullType == SkullType.PLAYER)
            throw new IllegalArgumentException("For player skulls, please use the other constructors.");
        this.skullType = skullType;
        owner = null;
    }

    public LegacySkullBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public LegacySkullBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public LegacySkullBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public LegacySkullBuilder setFlags(Collection<ItemFlag> itemFlags) {
        this.itemflags = new HashSet<>(itemFlags);
        return this;
    }

    public LegacySkullBuilder setFlags(ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public LegacySkullBuilder minimal() {
        setFlags(ItemFlag.values());
        setName(" ");
        setLore(" ");
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(Material.getMaterial("SKULL_ITEM"), amount, (short) skullType.ordinal());
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        if (name != null)
            meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        if (owner != null)
            meta.setOwner(owner);
        is.setItemMeta(meta);
        return is;
    }

}
