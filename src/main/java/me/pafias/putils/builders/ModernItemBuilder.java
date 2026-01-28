package me.pafias.putils.builders;

import me.pafias.putils.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class ModernItemBuilder {

    private Material material;
    private int amount = 1;

    private MaterialData materialData;
    private int modelData = -1;

    private Component name;

    private List<Component> lore;

    private Map<Enchantment, Integer> enchantments;

    private Set<ItemFlag> itemflags;

    public static ModernItemBuilder clone(ItemStack itemStack) {
        final ModernItemBuilder builder = new ModernItemBuilder(itemStack.getType());
        builder.setAmount(itemStack.getAmount());
        builder.setMaterialData(itemStack.getData());
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasCustomModelData())
            builder.setModelData(meta.getCustomModelData());
        if (meta.hasDisplayName())
            builder.setName(meta.displayName());
        if (meta.hasLore())
            builder.setLore(meta.lore());
        if (meta.hasEnchants()) {
            for (final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
                builder.addEnchant(entry.getKey(), entry.getValue());
        }
        builder.setFlags(meta.getItemFlags());
        return builder;
    }

    private ModernItemBuilder() {
        setMaterial(Material.AIR);
    }

    public ModernItemBuilder(Material material) {
        setMaterial(material);
    }

    public ModernItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ModernItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    @Deprecated
    public ModernItemBuilder setMaterialData(MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }

    public ModernItemBuilder setModelData(int modelData) {
        this.modelData = modelData;
        return this;
    }

    public ModernItemBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public ModernItemBuilder setLore(Collection<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public ModernItemBuilder setLore(Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ModernItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public ModernItemBuilder setFlags(Collection<ItemFlag> itemFlags) {
        this.itemflags = new HashSet<>(itemFlags);
        return this;
    }

    public ModernItemBuilder setFlags(ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public ModernItemBuilder minimal() {
        setFlags(ItemFlag.values());
        setName(CC.EMPTY);
        setLore(CC.EMPTY);
        return this;
    }

    public ItemStack build() {
        final ItemStack is = new ItemStack(material, amount);
        if (materialData != null)
            is.setData(materialData);
        final ItemMeta meta = is.getItemMeta();
        if (modelData != -1)
            meta.setCustomModelData(modelData);
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
        is.setItemMeta(meta);
        return is;
    }

}
