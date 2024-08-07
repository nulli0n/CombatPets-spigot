package su.nightexpress.combatpets.pet.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.nightcore.config.FileConfig;

public class PetFoodItem implements FoodItem {

    private final String id;
    private final PetFoodCategory category;
    private final ItemStack item;
    private final double    saturation;

    public PetFoodItem(@NotNull String id, @NotNull PetFoodCategory category, @NotNull ItemStack item, double saturation) {
        this.id = id.toLowerCase();
        this.category = category;
        this.item = new ItemStack(item);
        this.saturation = saturation;
    }

    @NotNull
    public static PetFoodItem read(@NotNull FileConfig config, @NotNull String path, @NotNull String id, @NotNull PetFoodCategory category) {
        ItemStack itemStack = config.getItem(path + ".Item");
        double saturation = config.getDouble(path + ".Saturation");

        return new PetFoodItem(id, category, itemStack, saturation);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.setItem(path + ".Item", this.getItem());
        config.set(path + ".Saturation", this.getSaturation());
    }

    @Override
    public boolean isItem(@NotNull ItemStack itemStack) {
        if (this.item.hasItemMeta()) {
            return itemStack.isSimilar(this.item);
        }
        return this.item.getType() == itemStack.getType();
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public FoodCategory getCategory() {
        return category;
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return new ItemStack(this.item);
    }

    @Override
    public double getSaturation() {
        return this.saturation;
    }
}
