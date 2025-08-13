package me.pafias.putils.builders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ArrowBuilder {

    private Material material;
    private int amount = 1;
    private PotionType effect;
    private boolean extended;
    private boolean upgraded;

    public ArrowBuilder() {
        material = Material.ARROW;
    }

    public ArrowBuilder setEffect(PotionType effect, boolean extended, boolean upgraded) {
        material = Material.TIPPED_ARROW;
        this.effect = effect;
        this.extended = extended;
        this.upgraded = upgraded;
        return this;
    }

    public ArrowBuilder setSpectral() {
        material = Material.SPECTRAL_ARROW;
        return this;
    }

    public ArrowBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        if (effect != null && !material.equals(Material.SPECTRAL_ARROW)) {
            PotionMeta meta = (PotionMeta) is.getItemMeta();
            meta.setBasePotionData(new PotionData(effect, extended, upgraded));
            is.setItemMeta(meta);
        }
        return is;
    }

}
