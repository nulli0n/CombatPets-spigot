package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.*;

public interface FoodCategory {

    void write(@NotNull FileConfig config, @NotNull String path);

    @Nullable
    default FoodItem getItem(@NotNull ItemStack itemStack) {
        return this.getItems().stream().filter(foodItem -> foodItem.isItem(itemStack)).findFirst().orElse(null);
    }

    default boolean isItem(@NotNull ItemStack itemStack) {
        return this.getItem(itemStack) != null;
    }

    @NotNull
    default Set<FoodItem> getItems() {
        return new HashSet<>(this.getItemMap().values());
    }

    @NotNull
    default List<String> getItemNames() {
        return new ArrayList<>(this.getItemMap().keySet());
    }

    @Nullable
    default FoodItem getItem(@NotNull String id) {
        return this.getItemMap().get(id.toLowerCase());
    }

    @NotNull String getId();

    @NotNull String getName();

    @NotNull Map<String, FoodItem> getItemMap();
}
