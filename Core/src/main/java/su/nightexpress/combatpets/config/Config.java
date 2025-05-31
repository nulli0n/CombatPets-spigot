package su.nightexpress.combatpets.config;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;

import java.util.Map;
import java.util.Set;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Config {

    public static final String DIR_MENU  = "/menu/";
    public static final String DIR_TIERS = "/pets/tiers/";
    public static final String DIR_PETS  = "/pets/configs/";

    @Deprecated
    public static final ConfigValue<Boolean> GENERAL_PLACEHOLDER_API_GUI = ConfigValue.create("General.PlaceholderAPI_In_GUI",
        false,
        "When enabled, applies PlaceholderAPI placeholders to GUI title and items."
    );

    public static final ConfigValue<Boolean> GENERAL_COLLECTION_DEFAULT_COMMAND = ConfigValue.create("General.Collection_As_Default_Command",
        false,
        "Sets whether Pets Collection GUI should open if no command argument specified.");

    public static final ConfigValue<Boolean> FEATURES_LEVELING_ENABLED = ConfigValue.create("Features.Leveling",
        true,
        "Sets whether or not Leveling feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_CAPTURE = ConfigValue.create("Features.Capture",
        true,
        "Sets whether or not Capture feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_WARDROBE = ConfigValue.create("Features.Accessories",
        true,
        "Sets whether or not Accessories feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_SHOP = ConfigValue.create("Features.Shop",
        true,
        "Sets whether or not Shop feature is enabled."
    );



    public static final ConfigValue<ItemStack> ITEM_MYSTERY_EGG = ConfigValue.create("Items.Mystery_Egg",
        PetCreator.getDefaultMysteryEgg(),
        "Mystery egg item."
    );



    public static final ConfigValue<ItemStack> PET_EGG_ITEM = ConfigValue.create("Pets.Egg_Item",
        PetCreator.getDefaultEgg(),
        "Egg item.",
        "[*] You should use '" + BukkitThing.toString(Material.PLAYER_HEAD) + "' as item Material to apply pet's egg texture."
    );

    public static final ConfigValue<Set<String>> PET_DISABLED_WORLDS = ConfigValue.create("Pets.Disabled_Worlds",
        Lists.newSet("custom_world"),
        "List of worlds, where pets are disabled completely."
    );

    public static final ConfigValue<Boolean> PET_RELEASE_ALLOWED = ConfigValue.create("Pets.Release.Allowed",
        true,
        "Sets whether or not players can release their pets."
    );

    public static final ConfigValue<Boolean> PET_RELEASE_NATURAL = ConfigValue.create("Pets.Release.Natural",
        true,
        "When enabled, spawns similar to pet entity when releasing."
    );

    public static final ConfigValue<Set<String>> PET_RELEASE_DISABLED_WORLDS = ConfigValue.create("Pets.Release.BadWorlds",
        Lists.newSet("spawn", "other_world"),
        "List of worlds, where players can not release their pets."
    );

    public static final ConfigValue<Boolean> PET_PVP_ALLOWED = ConfigValue.create("Pets.PVP_Allowed",
        true,
        "Sets whether or not player's pets can attack each other."
    );

    public static final ConfigValue<Boolean> PET_SNEAK_TO_OPEN_MENU = ConfigValue.create("Pets.Sneak_To_Open_Menu",
        false,
        "Sets whether or not player should sneak to open pet menu on right click."
    );

    public static final ConfigValue<Boolean> PET_ATTACK_DAMAGE_FOR_PROJECTILES = ConfigValue.create("Pets.Attack_Damage_For_Projectiles",
        true,
        "Sets whether or not pet's 'Attack Damage' attribute will override damage for ranged (non-melee) attacks.",
        "By default, game does not uses 'Attack Damage' attribute when you (or mob) shoot arrows, fireballs, tridents, etc.",
        "This setting will change that."
    );

    public static final ConfigValue<Double> PET_ORIGINAL_DAMAGE_FROM_PROJECTILES = ConfigValue.create("Pets.Original_Damage_From_Projectiles",
        0.25D,
        "[ONLY WHEN 'Attack_Damage_For_Projectiles' IS ENABLED]",
        "Sets multiplier for original damage caused by pet's ranged attacks to be added to 'Attack Damage' attribute.",
        "This will help in balancing ranged attacks damage especially for enchanted items, so they inflicts more damage."
    );

    public static final ConfigValue<Integer> PET_SATURATION_PERCENT_TO_REGEN = ConfigValue.create("Pets.Saturation_Percent_To_Regen",
        70,
        "Sets for how many percents a pet should be satiated to be able for natural health regen.");

    public static final ConfigValue<Double> PET_DAMAGE_REGEN_COOLDOWN = ConfigValue.create("Pets.Damage_Regen_Cooldown",
        5D,
        "Sets natural regeneration cooldown (in seconds) when a pet was damaged.");

    public static final ConfigValue<Boolean> PET_PERMANENT_DEATH = ConfigValue.create("Pets.Permanent_Deaths",
        false,
        "Sets whether or not pet will be removed from player's collection when pet dies.");

    public static final ConfigValue<Boolean> PET_DROP_INVENTORY = ConfigValue.create("Pets.Drop_Inventory",
        true,
        "Sets whether or not pet will drop items from their inventories on death.");

    public static final ConfigValue<Boolean> PET_DROP_EQUIPMENT = ConfigValue.create("Pets.Drop_Equipment",
        true,
        "Sets whether or not pet will drop their armors on death.");

    public static final ConfigValue<Boolean> PET_REALLOCATE_ASPECTS = ConfigValue.create("Pets.Allow_Reallocate_Aspect_Points",
        false,
        "Sets whether or not players can reallocate pet's aspects by using the Reallocate Points button in the GUI.");

    //public static final ConfigValue<Boolean> PET_DAMAGE_EQUIPMENT = ConfigValue.create("Pets.Damage_Equipment", true,
    //    "Sets whether or not pet equipment can be damaged.");

    public static final ConfigValue<Boolean> PET_AUTO_FOOD_ENABLED = ConfigValue.create("Pets.AutoFoodUsage.Enabled",
        false,
        "Sets whether or not pets will auto use food from their inventories.");

    public static final ConfigValue<Double> PET_AUTO_FOOD_PERCENT = ConfigValue.create("Pets.AutoFoodUsage.At_Saturation",
        75D,
        "Sets saturation percent for a pet to start use food.",
        "Default is 75, so pet will auto-eat when saturation drops to 75% or lower.");

    public static final ConfigValue<Boolean> PET_POP_DEFAULT_EQUIPMENT = ConfigValue.create("Pets.Populate_Default_Equipment",
        true,
        "Sets whether or not captured pets will get vanilla's mob equipment instead of being empty.",
        "This option is useful to make skeleton pets with bows by default, piglins with swords, etc."
    );



    public static final ConfigValue<Integer> PET_NAME_LENGTH_MAX = ConfigValue.create("Pets.Name.Max_Length",
        16,
        "Sets the characters limit for pet names.");

    public static final ConfigValue<Integer> PET_NAME_LENGTH_MIN = ConfigValue.create("Pets.Name.Min_Length",
        3,
        "Sets min characters amount for pet names.");

    public static final ConfigValue<Set<String>> PET_NAME_BLOCKED_WORDS = ConfigValue.create("Pets.Name.ForbiddenWords",
        Lists.newSet("asshole", "dick", "penis", "ass", "bitch"),
        "A list of words that are forbidden to be in pet names.");

    public static final ConfigValue<Boolean> PET_NAME_RENAME_ALLOW_NAMETAGS = ConfigValue.create("Pets.Name.Rename.Allow_NameTags",
        true,
        "Sets whether or not Name tag items can be used on pets to rename them.");

    public static final ConfigValue<Boolean> PET_NAME_RENAME_MENU_REQUIRES_NAMETAG = ConfigValue.create("Pets.Name.Rename.Menu_Requires_NameTag",
        true,
        "Sets whether or not Rename button in Pet Menu will require Name tag item in player's inventory.");



    public static final ConfigValue<Boolean>  PET_HEALTHBAR_ENABLED = ConfigValue.create("Pets.Healthbar.Enabled",
        true,
        "Sets whether or not to use boss bar indicating pet health.");

    public static final ConfigValue<String> PET_HEALTHBAR_TITLE = ConfigValue.create("Pets.Healthbar.Title",
        LIGHT_YELLOW.wrap(PET_NAME) + "   " + GRAY.wrap("Lv. ") + WHITE.wrap(PET_LEVEL) + "   " + LIGHT_RED.wrap(PET_HEALTH) + GRAY.wrap("/") + LIGHT_RED.wrap(PET_MAX_HEALTH + " ❤"),
        "Sets healthbar title.",
        "You can use 'Pet' placeholders: " + WIKI_PLACEHOLDERS_URL
    );

    public static final ConfigValue<BarStyle> PET_HEALTHBAR_STYLE = ConfigValue.create("Pets.Healthbar.Style",
        BarStyle.class, BarStyle.SOLID,
        "Sets healthbar style.",
        "Available values: " + StringUtil.inlineEnum(BarStyle.class, ", ")
    );

    public static final ConfigValue<BarColor> PET_HEALTHBAR_COLOR = ConfigValue.create("Pets.Healthbar.Color",
        BarColor.class, BarColor.GREEN,
        "Sets healthbar color.",
        "Available values: " + StringUtil.inlineEnum(BarColor.class, ", ")
    );

    public static final ConfigValue<RankMap<Integer>> PET_AMOUNT_PER_RANK = ConfigValue.create("Pets.Amount_Per_Rank",
        (cfg, path, def) -> RankMap.readInt(cfg, path, -1),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(
            RankMap.Mode.RANK,
            "pets.amount.",
            -1,
            Map.of(Placeholders.DEFAULT, 10, "vip", 20, "admin", -1)
        ),
        "Sets how many pets (per each tier) players can have depends on their permission group.",
        "Use '" + DEFAULT + "' for any groups not listed here.",
        "Use '-1' for unlimited amount."
    );

    @Deprecated
    public static boolean isGUIPlaceholdersEnabled() {
        return GENERAL_PLACEHOLDER_API_GUI.get();
    }

    public static boolean isLevelingEnabled() {
        return FEATURES_LEVELING_ENABLED.get();
    }

    public static boolean isCapturingEnabled() {
        return FEATURES_CAPTURE.get();
    }

    public static boolean isShopEnabled() {
        return FEATURES_SHOP.get();
    }

    public static boolean isWardrobeEnabled() {
        return FEATURES_WARDROBE.get();
    }
}
