package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

public interface FoodItem {

    void write(@NotNull FileConfig config, @NotNull String path);

    boolean isItem(@NotNull ItemStack itemStack);

    @NotNull String getId();

    @NotNull FoodCategory getCategory();

    @NotNull ItemStack getItem();

    double getSaturation();
}
