package me.pafias.putils.builders;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyItemBuilder {

    private final Material material;
    private int amount = 1;
    private short data = -1;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private ItemFlag[] itemflags;

    public static ItemBuilder clone(ItemStack itemStack) {
        ItemBuilder builder = new ItemBuilder(itemStack.getType());
        builder.setAmount(itemStack.getAmount());
        builder.setData(itemStack.getDurability());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasDisplayName())
            builder.setName(meta.getDisplayName());
        if (meta.hasLore())
            builder.setLore(meta.getLore().toArray(new String[0]));
        if (meta.hasEnchants())
            meta.getEnchants().forEach(builder::addEnchant);
        builder.setFlags(meta.getItemFlags().toArray(new ItemFlag[0]));
        return builder;
    }

    private LegacyItemBuilder() {
        this.material = Material.AIR;
    }

    public LegacyItemBuilder(Material material) {
        this.material = material;
    }

    public LegacyItemBuilder(int materialId) {
        try {
            Method getMaterialMethod = Material.class.getMethod("getMaterial", int.class);
            this.material = (Material) getMaterialMethod.invoke(null, materialId);
            if (this.material == null)
                throw new IllegalArgumentException("Invalid material ID: " + materialId);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("LegacyItemBuilder cannot be used in this minecraft version.", e);
        }
    }

    public LegacyItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public LegacyItemBuilder setData(short data) {
        this.data = data;
        return this;
    }

    public LegacyItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public LegacyItemBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public LegacyItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public LegacyItemBuilder setFlags(ItemFlag... flags) {
        itemflags = flags;
        return this;
    }

    public LegacyItemBuilder minimal() {
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
            meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (itemflags != null)
            meta.addItemFlags(itemflags);
        is.setItemMeta(meta);
        return is;
    }

}
