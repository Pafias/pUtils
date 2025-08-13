package me.pafias.putils.builders;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedBookBuilder {

    private int amount = 1;
    private Component name;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;

    public EnchantedBookBuilder() {
    }

    public EnchantedBookBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public EnchantedBookBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public EnchantedBookBuilder setName(String name) {
        this.name = Component.text(name);
        return this;
    }

    public EnchantedBookBuilder setLore(Component... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public EnchantedBookBuilder setLore(String... lore) {
        this.lore = Arrays.asList(Arrays.stream(lore)
                .map(Component::text)
                .toArray(Component[]::new));
        return this;
    }

    public EnchantedBookBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public EnchantedBookBuilder minimal() {
        setName(" ");
        setLore(" ");
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(Material.ENCHANTED_BOOK, amount);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addStoredEnchant(enchantment, level, true));
        is.setItemMeta(meta);
        return is;
    }

}
