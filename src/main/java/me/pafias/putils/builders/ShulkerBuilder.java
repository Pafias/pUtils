package me.pafias.putils.builders;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class ShulkerBuilder {

    private Material material;
    private int amount = 1;
    private String name;
    private List<String> lore;
    private ItemFlag[] itemFlags;
    private Consumer<Inventory> inventoryConsumer;

    public ShulkerBuilder() {
        this(Material.SHULKER_BOX);
    }

    public ShulkerBuilder(Material material) {
        if (!material.name().contains("SHULKER")) throw new IllegalArgumentException("Material must be a shulker box");
        this.material = material;
    }

    public static ShulkerBuilder fromExisting(ItemStack item) {
        if (!item.getType().name().contains("SHULKER"))
            throw new IllegalArgumentException("Material must be a shulker box");

        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();

        ShulkerBuilder builder = new ShulkerBuilder(item.getType());
        builder.amount = item.getAmount();
        builder.name = item.getItemMeta().getDisplayName();
        builder.lore = item.getItemMeta().getLore();
        builder.itemFlags = item.getItemFlags().toArray(new ItemFlag[0]);

        Inventory originalInventory = shulkerBox.getInventory();
        builder.inventoryConsumer = inventory -> {
            inventory.setContents(originalInventory.getContents());
        };

        return builder;
    }

    public ShulkerBuilder setType(Material type) {
        if (!type.name().contains("SHULKER")) throw new IllegalArgumentException("Material must be a shulker box");
        this.material = type;
        return this;
    }

    public ShulkerBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getName() {
        return name;
    }

    public ShulkerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ShulkerBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ShulkerBuilder setFlags(ItemFlag... flags) {
        this.itemFlags = flags;
        return this;
    }

    public ShulkerBuilder minimal() {
        setFlags(ItemFlag.values());
        setName("");
        setLore("");
        return this;
    }

    public ShulkerBuilder apply(Consumer<Inventory> consumer) {
        this.inventoryConsumer = consumer;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();

        if (name != null)
            blockStateMeta.setDisplayName(name);

        if (lore != null)
            blockStateMeta.setLore(lore);

        if (itemFlags != null)
            blockStateMeta.addItemFlags(itemFlags);

        if (inventoryConsumer != null)
            inventoryConsumer.accept(shulkerBox.getInventory());

        blockStateMeta.setBlockState(shulkerBox);
        itemStack.setItemMeta(blockStateMeta);

        return itemStack;
    }

}
