package me.pafias.putils.builders;

import me.pafias.putils.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class LeatherArmorBuilder {

    private Material material;
    private int amount = 1;

    private Color color;

    private Component name;
    private String nameLegacy;
    private List<Component> lore;
    private List<String> loreLegacy;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> itemflags;

    public static LeatherArmorBuilder clone(ItemStack itemStack) {
        LeatherArmorBuilder builder = new LeatherArmorBuilder(itemStack.getType());
        builder.setAmount(itemStack.getAmount());
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        if (meta.getColor() != null)
            builder.setColor(meta.getColor());
        if (meta.hasDisplayName())
            builder.setName(meta.displayName());
        if (meta.hasLore())
            builder.setLore(meta.lore());
        if (meta.hasEnchants()) {
            for (final Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet()) {
                builder.addEnchant(enchantment.getKey(), enchantment.getValue());
            }
        }
        builder.setFlags(meta.getItemFlags());
        return builder;
    }

    public LeatherArmorBuilder(Material material) {
        if (!Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS).contains(material))
            throw new IllegalArgumentException("Material must be a leather armor piece!");
        setMaterial(material);
    }

    public LeatherArmorBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public LeatherArmorBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public LeatherArmorBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public LeatherArmorBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    @Deprecated
    public LeatherArmorBuilder setNameLegacy(String name) {
        this.nameLegacy = name;
        return this;
    }

    public LeatherArmorBuilder setLore(Collection<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public LeatherArmorBuilder setLore(Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    @Deprecated
    public LeatherArmorBuilder setLoreLegacy(Collection<String> lore) {
        this.loreLegacy = new ArrayList<>(lore);
        return this;
    }

    @Deprecated
    public LeatherArmorBuilder setLoreLegacy(String... lore) {
        return setLoreLegacy(Arrays.asList(lore));
    }

    public LeatherArmorBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    public LeatherArmorBuilder setFlags(Collection<ItemFlag> itemFlags) {
        this.itemflags = new HashSet<>(itemFlags);
        return this;
    }

    public LeatherArmorBuilder setFlags(ItemFlag... flags) {
        return setFlags(Arrays.asList(flags));
    }

    public LeatherArmorBuilder minimal() {
        setFlags(ItemFlag.values());
        try {
            setName(CC.EMPTY);
        } catch (Throwable t) {
            setNameLegacy(" ");
        }
        try {
            setLore(CC.EMPTY);
        } catch (Throwable t) {
            setLoreLegacy(" ");
        }
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
        if (color != null)
            meta.setColor(color);
        if (nameLegacy != null)
            meta.setDisplayName(nameLegacy);
        else if (name != null)
            meta.displayName(name);
        if (loreLegacy != null)
            meta.setLore(loreLegacy);
        else if (lore != null)
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
