package su.nightexpress.combatpets.pet.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class PetFoodCategory implements FoodCategory {

    private final String id;
    private final String name;
    private final Map<String, FoodItem> itemMap;

    public PetFoodCategory(@NotNull String id, @NotNull String name, @NotNull Map<String, FoodItem> itemMap) {
        this.id = id;
        this.name = name;
        this.itemMap = itemMap;
    }

    @NotNull
    public static PetFoodCategory read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(id)).read(config);
        Map<String, FoodItem> itemMap = new HashMap<>();

        PetFoodCategory category = new PetFoodCategory(id, name, itemMap);

        config.getSection(path + ".Items").forEach(itemId -> {
            FoodItem foodItem = PetFoodItem.read(config, path + ".Items." + itemId, itemId, category);
            itemMap.put(foodItem.getId(), foodItem);
        });

        return category;
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.getName());
        this.itemMap.values().forEach(foodItem -> {
            foodItem.write(config, path + ".Items." + foodItem.getId());
        });
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public Map<String, FoodItem> getItemMap() {
        return itemMap;
    }
}
