package me.pafias.putils.builders;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyEnchantedBookBuilder {

    private int amount = 1;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;

    public LegacyEnchantedBookBuilder() {
    }

    public LegacyEnchantedBookBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public LegacyEnchantedBookBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public LegacyEnchantedBookBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public LegacyEnchantedBookBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public LegacyEnchantedBookBuilder minimal() {
        setName(" ");
        setLore(" ");
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(Material.ENCHANTED_BOOK, amount);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
        if (name != null)
            meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addStoredEnchant(enchantment, level, true));
        is.setItemMeta(meta);
        return is;
    }

}
