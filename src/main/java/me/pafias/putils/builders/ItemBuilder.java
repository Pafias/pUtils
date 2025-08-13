package me.pafias.putils.builders;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private final Material material;
    private int amount = 1;
    private short data = -1;
    private Component name;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private ItemFlag[] itemflags;

    public static ItemBuilder clone(ItemStack itemStack) {
        ItemBuilder builder = new ItemBuilder(itemStack.getType());
        builder.setAmount(itemStack.getAmount());
        builder.setData(itemStack.getDurability());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasDisplayName())
            builder.setName(meta.displayName());
        if (meta.hasLore())
            builder.setLore(meta.lore().toArray(new Component[0]));
        if (meta.hasEnchants())
            meta.getEnchants().forEach(builder::addEnchant);
        builder.setFlags(meta.getItemFlags().toArray(new ItemFlag[0]));
        return builder;
    }

    private ItemBuilder() {
        this.material = Material.AIR;
    }

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setData(short data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = Component.text(name);
        return this;
    }

    public ItemBuilder setLore(Component... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.lore = Arrays.asList(Arrays.stream(lore)
                .map(Component::text)
                .toArray(Component[]::new));
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setFlags(ItemFlag... flags) {
        itemflags = flags;
        return this;
    }

    public ItemBuilder minimal() {
        setFlags(ItemFlag.values());
        setName(" ");
        setLore(" ");
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        if (data != -1)
            is.setDurability(data);
        ItemMeta meta = is.getItemMeta();
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (itemflags != null)
            meta.addItemFlags(itemflags);
        is.setItemMeta(meta);
        return is;
    }

}
