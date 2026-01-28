package me.pafias.putils.builders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class PotionBuilder {

    private final Material material;
    private PotionType effect;
    private boolean extended;
    private boolean upgraded;

    public PotionBuilder() {
        this.material = Material.POTION;
    }

    public PotionBuilder(Material potionMaterial) {
        if (!Arrays.asList(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION).contains(potionMaterial))
            throw new IllegalArgumentException("Material must be a potion type.");
        this.material = potionMaterial;
    }

    public PotionBuilder setEffect(PotionType effect, boolean extended, boolean upgraded) {
        this.effect = effect;
        this.extended = extended;
        this.upgraded = upgraded;
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material);
        PotionMeta meta = (PotionMeta) is.getItemMeta();
        if (effect != null)
            meta.setBasePotionData(new PotionData(effect, extended, upgraded));
        is.setItemMeta(meta);
        return is;
    }

}
