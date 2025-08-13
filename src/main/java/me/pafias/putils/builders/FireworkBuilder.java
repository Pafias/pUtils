package me.pafias.putils.builders;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class FireworkBuilder {

    private final Material material;
    private final List<FireworkEffect> effects = new ArrayList<>();
    private int amount = 1;
    private double power = 2;

    public FireworkBuilder() {
        material = Material.FIREWORK_ROCKET;
    }

    public FireworkBuilder addEffect(FireworkEffect effect) {
        effects.add(effect);
        return this;
    }

    public FireworkBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public FireworkBuilder setFlightTime(int seconds) {
        power = seconds/* / 0.5 */;
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        FireworkMeta meta = (FireworkMeta) is.getItemMeta();
        meta.addEffects(effects);
        meta.setPower((int) power);
        is.setItemMeta(meta);
        return is;
    }

}
