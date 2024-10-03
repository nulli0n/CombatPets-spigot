package su.nightexpress.combatpets.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;

public class Keys {

    public static NamespacedKey itemType;

    public static NamespacedKey eggPetId;
    public static NamespacedKey eggTierId;

    public static NamespacedKey captureItem;
    public static NamespacedKey captureEscaped;
    public static NamespacedKey captureProgress;

    public static NamespacedKey accessoryType;
    public static NamespacedKey accessoryValue;

//    public static NamespacedKey foodCategory;
//    public static NamespacedKey foodItemId;

    public static NamespacedKey levelingNoXP;

    public static void load(@NotNull PetsPlugin plugin) {
        itemType = new NamespacedKey(plugin, "item.type");

        eggPetId = new NamespacedKey(plugin, "item.config_id");
        eggTierId = new NamespacedKey(plugin, "item.tier_id");

        captureItem = new NamespacedKey(plugin, "capture.item");
        captureEscaped = new NamespacedKey(plugin, "capture.escaped");
        captureProgress = new NamespacedKey(plugin, "capture.progress");

        accessoryType = new NamespacedKey(plugin, "customization.type");
        accessoryValue = new NamespacedKey(plugin, "customization.value");

//        foodCategory = new NamespacedKey(plugin, "food.category");
//        foodItemId = new NamespacedKey(plugin, "food.id");

        levelingNoXP = new NamespacedKey(plugin, "leveling.no_xp");
    }
}
