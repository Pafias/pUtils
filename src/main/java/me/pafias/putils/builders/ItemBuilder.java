package me.pafias.putils.builders;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final Material material;

    private int amount = 1;

    private short legacyData = -1;
    private MaterialData materialData;
    private int modelData = -1;

    private Component name;

    private List<Component> lore;

    private Map<Enchantment, Integer> enchantments;

    private Set<ItemFlag> itemflags;

    public static ItemBuilder clone(ItemStack itemStack) {
        ItemBuilder builder = new ItemBuilder(itemStack.getType());
        builder.setAmount(itemStack.getAmount());
        builder.setLegacyData(itemStack.getDurability());
        builder.setMaterialData(itemStack.getData());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasCustomModelData())
            builder.setModelData(meta.getCustomModelData());
        if (meta.hasDisplayName())
            builder.setName(meta.displayName());
        if (meta.hasLore())
            builder.setLore(meta.lore());
        if (meta.hasEnchants())
            meta.getEnchants().forEach(builder::addEnchant);
        builder.setFlags(meta.getItemFlags());
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

    @Deprecated
    public ItemBuilder setMaterialData(MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }

    @Deprecated
    public ItemBuilder setLegacyData(short data) {
        this.legacyData = data;
        return this;
    }

    public ItemBuilder setModelData(int modelData) {
        this.modelData = modelData;
        return this;
    }

    public ItemBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    @Deprecated
    public ItemBuilder setNameLegacy(String name) {
        return setName(Component.text(name));
    }

    public ItemBuilder setLore(Collection<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public ItemBuilder setLore(Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    @Deprecated
    public ItemBuilder setLoreLegacy(Collection<String> lore) {
        this.lore = lore.stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    @Deprecated
    public ItemBuilder setLoreLegacy(String... lore) {
        return setLoreLegacy(Arrays.asList(lore));
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setFlags(Collection<ItemFlag> itemFlags) {
        this.itemflags = new HashSet<>(itemFlags);
        return this;
    }

    public ItemBuilder setFlags(ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public ItemBuilder minimal() {
        setFlags(ItemFlag.values());
        setNameLegacy(" ");
        setLoreLegacy(" ");
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        if (legacyData != -1)
            is.setDurability(legacyData);
        if (materialData != null)
            is.setData(materialData);
        ItemMeta meta = is.getItemMeta();
        if (modelData != -1)
            meta.setCustomModelData(modelData);
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (itemflags != null)
            meta.addItemFlags(itemflags.toArray(new ItemFlag[0]));
        is.setItemMeta(meta);
        return is;
    }

}
