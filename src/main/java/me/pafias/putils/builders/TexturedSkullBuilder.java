package me.pafias.putils.builders;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.pafias.putils.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TexturedSkullBuilder {

    private final String base64;

    /**
     * Creates a new TexturedSkullBuilder with the given base64 texture string.
     *
     * @param base64 The base64 texture string. This value usually starts with <b>ey</b> and ends with <b>=</b>
     */
    public TexturedSkullBuilder(@NotNull String base64) {
        if (base64 == null || base64.isEmpty())
            throw new IllegalArgumentException("Texture cannot be null or empty");
        this.base64 = base64;
    }

    /**
     * Creates a new TexturedSkullBuilder with the given texture URL.
     *
     * @param textureUrl The URL of the texture to use. This value is usually <b>https://textures.minecraft.net/texture/...</b>
     */
    public TexturedSkullBuilder(@NotNull URL textureUrl) {
        if (textureUrl == null)
            throw new IllegalArgumentException("Texture cannot be null or empty");
        String format = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", textureUrl.toString());
        this.base64 = Base64.getEncoder().encodeToString(format.getBytes());
    }

    public static TexturedSkullBuilder clone(ItemStack itemStack) {
        final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        final PlayerProfile profile = meta.getPlayerProfile();
        String base64 = "";
        for (ProfileProperty property : profile.getProperties()) {
            if (property.getName().equals("textures"))
                base64 = property.getValue();
        }
        TexturedSkullBuilder builder = new TexturedSkullBuilder(base64);
        builder.setAmount(itemStack.getAmount());
        if (meta.hasDisplayName())
            builder.setName(meta.displayName());
        if (meta.hasLore())
            builder.setLore(meta.lore());
        if (meta.hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet()) {
                builder.addEnchant(enchantment.getKey(), enchantment.getValue());
            }
        }
        builder.setFlags(meta.getItemFlags());
        return builder;
    }

    private int amount = 1;
    private Component name;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> itemflags;

    public TexturedSkullBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public TexturedSkullBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public TexturedSkullBuilder setLore(Collection<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public TexturedSkullBuilder setLore(Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    public TexturedSkullBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public TexturedSkullBuilder setFlags(Collection<ItemFlag> itemFlags) {
        this.itemflags = new HashSet<>(itemFlags);
        return this;
    }

    public TexturedSkullBuilder setFlags(ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public TexturedSkullBuilder minimal() {
        setFlags(ItemFlag.values());
        setName(CC.EMPTY);
        setLore(CC.EMPTY);
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (enchantments != null) {
            for (final Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
            }
        }
        if (itemflags != null)
            meta.addItemFlags(itemflags.toArray(new ItemFlag[0]));
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", base64));
        meta.setPlayerProfile(profile);
        is.setItemMeta(meta);
        return is;
    }

    public CompletableFuture<ItemStack> buildAsync() {
        return CompletableFuture.supplyAsync(this::build);
    }

}
