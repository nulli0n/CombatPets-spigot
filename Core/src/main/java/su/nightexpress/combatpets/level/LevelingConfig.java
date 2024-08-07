package su.nightexpress.combatpets.level;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.Set;
import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class LevelingConfig {

    public static final ConfigValue<Boolean> ALLOW_DOWNGRADE = ConfigValue.create("Settings.Downgrade_Allowed",
        true,
        "Sets whether or not level downgrade is allowed.",
        "This setting will allow XP to go negative and therefore downgrade pet levels."
    );

    public static final ConfigValue<Set<String>> DISABLED_WORLDS = ConfigValue.create("Settings.Disabled_Worlds",
        Lists.newSet("world_name", "another_world"),
        "List of worlds, where pets will not gain or loss XP."
    );

    public static final ConfigValue<Double> DEATH_XP_LOSS = ConfigValue.create("Settings.Death_XP_Loss",
        5D,
        "Sets how much XP (in % of max.) will be lost on pet's death."
    );

//    public static final ConfigValue<Boolean> XP_BASED_ON_DAMAGE_DEALT = ConfigValue.create("Settings.XP_Based_On_Damage_Dealt",
//        true,
//        "When enabled, adjusts XP amount based on damage dealt by a pet.",
//        "Example: If pet dealt 10% damage = 10% XP of total XP amount."
//    );

    public static final ConfigValue<Boolean> USE_CUSTOM_XP_TABLE = ConfigValue.create("Settings.Use_Custom_XP_Table",
        true,
        "Sets whether or not to use custom XP table - 'XPSources'.",
        "When disabled, uses 'natural' XP amount produced by mob's death."
    );

    public static final ConfigValue<Set<SpawnReason>> DISABLE_XP_BY_SPAWN_REASON = ConfigValue.forSet("Settings.Prevent_XP_From",
        id -> StringUtil.getEnum(id, SpawnReason.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        () -> Lists.newSet(
            SpawnReason.SPAWNER,
            SpawnReason.SPAWNER_EGG,
            SpawnReason.BUILD_IRONGOLEM,
            SpawnReason.BUILD_SNOWMAN,
            SpawnReason.DISPENSE_EGG,
            SpawnReason.EGG
        ),
        "Mobs spawned by one of the following reasons will produce no XP for pets.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    );
}
