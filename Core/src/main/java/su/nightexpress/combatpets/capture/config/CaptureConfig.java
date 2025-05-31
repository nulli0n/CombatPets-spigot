package su.nightexpress.combatpets.capture.config;

import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.bukkit.NightItem;

public class CaptureConfig {

    public static final ConfigValue<NightItem> CAPTURE_ITEM = ConfigValue.create("Settings.Item",
        PetCreator.getCaptureItem(),
        "Sets capture item format."
    );

    public static final ConfigValue<Boolean> CAPTURE_CONSUME_ITEM = ConfigValue.create("Settings.Consume_Item",
        true,
        "Sets whether or not to consume capture item on use."
    );

    public static final ConfigValue<Boolean> CAPTURE_ESCAPE_ALLOWED = ConfigValue.create("Settings.Escape_Allowed",
        true,
        "Sets whether or not mobs can escape from capturing making them immune to any next captures."
    );

    public static final ConfigValue<Double> CAPTURE_HEALTH_PERCENT = ConfigValue.create("Settings.Health_Percent",
        40D,
        "Sets min. amount (in percent of max.) of mob health to allow capturing them.",
        "If mob health % is greater than this value, it can not be captured."
    );

    public static final ConfigValue<Boolean> CAPTURE_SAVE_PROGRESS = ConfigValue.create("Settings.Save_Progress",
        true,
        "When 'true' saves success progress to the mob if catching failed.",
        "On next catch attempt, it will have that saved success value."
    );

    public static final ConfigValue<Integer> CAPTURE_MAX_DISTANCE = ConfigValue.create("Settings.Max_Distance",
        5,
        "Sets max. distance between a player and a mob while capturing.",
        "If distance is greater than this value, capture task will be cancelled."
    );
}
