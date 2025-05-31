package su.nightexpress.combatpets.config;

import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Rabbit;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.wardrobe.handler.AgeVariantHandler;
import su.nightexpress.combatpets.wardrobe.handler.CreeperPowerVariantHandler;
import su.nightexpress.combatpets.wardrobe.handler.SheepShearVariantHandler;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class Lang extends CoreLang {

    public static final LangEnum<AgeVariantHandler.Type>          AGE_TYPE    = LangEnum.of("PetVariant.AgeType", AgeVariantHandler.Type.class);
    public static final LangEnum<CreeperPowerVariantHandler.Type> POWER_TYPE  = LangEnum.of("PetVariant.CreeperPower", CreeperPowerVariantHandler.Type.class);
    public static final LangEnum<DyeColor>                        DYE_COLOR   = LangEnum.of("PetVariant.DyeColor", DyeColor.class);
    public static final LangEnum<Fox.Type>                        FOX_TYPE    = LangEnum.of("PetVariant.FoxType", Fox.Type.class);
    public static final LangEnum<Horse.Color>                     HORSE_COLOR = LangEnum.of("PetVariant.HorseColor", Horse.Color.class);
    public static final LangEnum<Horse.Style>                     HORSE_STYLE = LangEnum.of("PetVariant.FoxType", Horse.Style.class);
    public static final LangEnum<Llama.Color>                     LLAMA_COLOR = LangEnum.of("PetVariant.LlamaColor", Llama.Color.class);
    public static final LangEnum<SheepShearVariantHandler.Type>   SHEEP_SHEAR = LangEnum.of("PetVariant.SheepShear", SheepShearVariantHandler.Type.class);
    public static final LangEnum<Rabbit.Type> RABBIT_TYPE = LangEnum.of("PetVariant.RabbitType", Rabbit.Type.class);

    public static final LangEnum<CombatMode> COMBAT_MODE = LangEnum.of("CombatMode", CombatMode.class);

    public static final LangString COMMAND_ARGUMENT_NAME_TIER = LangString.of("Command.Argument.Name.Tier", "tier");
    public static final LangString COMMAND_ARGUMENT_NAME_PET  = LangString.of("Command.Argument.Name.Pet", "pet");

    public static final LangString COMMAND_COLLECTION_DESC            = LangString.of("Command.Collection.Desc", "List of pets you have claimed.");
    public static final LangString COMMAND_ADD_ALL_DESC               = LangString.of("Command.AddAll.Desc", "Add all pets to collection.");
    public static final LangString COMMAND_ADD_DESC                   = LangString.of("Command.Add.Desc", "Add pet to collection.");
    public static final LangString COMMAND_REMOVE_DESC                = LangString.of("Command.Remove.Desc", "Remove pet from collection.");
    public static final LangString COMMAND_REMOVE_ALL_DESC            = LangString.of("Command.RemoveAll.Desc", "Remove all pets from collection.");
    public static final LangString COMMAND_CAPTURE_ITEM_DESC          = LangString.of("Command.CatchItem.Desc", "Give capture item.");
    public static final LangString COMMAND_FOOD_DESC                  = LangString.of("Command.Food.Desc", "Give food item.");
    public static final LangString COMMAND_EGG_DESC                   = LangString.of("Command.Egg.Desc", "Give pet egg.");
    public static final LangString COMMAND_MYSTERY_EGG_DESC           = LangString.of("Command.MysteryEgg.Desc", "Give mystery egg.");
    public static final LangString COMMAND_RENAME_DESC                = LangString.of("Command.Rename.Desc", "Rename player's pet.");
    public static final LangString COMMAND_REVIVE_DESC                = LangString.of("Command.Revive.Desc", "Revive player's pet.");
    public static final LangString COMMAND_CLEAR_INVENTORY_DESC       = LangString.of("Command.ClearInventory.Desc", "Clear player's pet inventory.");
    public static final LangString COMMAND_MENU_DESC                  = LangString.of("Command.Menu.Desc", "Open pet menu.");
    public static final LangString COMMAND_SHOP_DESC                  = LangString.of("Command.Shop.Desc", "Open shop.");
    public static final LangString COMMAND_ACCESSORY_DESC             = LangString.of("Command.Accessory.Desc", "Give accessory item.");
    public static final LangString COMMAND_RESET_PROGRESS_DESC        = LangString.of("Command.ResetProgress.Desc", "Reset leveling for player's pet.");
    public static final LangString COMMAND_XP_DESC                    = LangString.of("Command.XP.Desc", "XP commands.");
    public static final LangString COMMAND_XP_ADD_DESC                = LangString.of("Command.XP.Add.Desc", "Add XP to a pet.");
    public static final LangString COMMAND_XP_SET_DESC                = LangString.of("Command.XP.Set.Desc", "Set XP for a pet.");
    public static final LangString COMMAND_XP_REMOVE_DESC             = LangString.of("Command.XP.Remove.Desc", "Remove XP off a pet.");
    public static final LangString COMMAND_XP_REWARD_DESC             = LangString.of("Command.XP.Reward.Desc", "Add XP to a player's active pet.");
    public static final LangString COMMAND_XP_PENALTY_DESC            = LangString.of("Command.XP.Penalty.Desc", "Remove XP off a player's active pet.");
    public static final LangString COMMAND_APOINTS_DESC               = LangString.of("Command.AspectPoints.Desc", "Aspect Points commands.");
    public static final LangString COMMAND_ASPECT_POINTS_ADD_DESC     = LangString.of("Command.AspectPoints.Add.Desc", "Add Aspect Points to a pet.");
    public static final LangString COMMAND_ASPECT_POINTS_SET_DESC     = LangString.of("Command.AspectPoints.Set.Desc", "Set Aspect Points for a pet.");
    public static final LangString COMMAND_ASPECT_POINTS_REMOVE_DESC  = LangString.of("Command.AspectPoints.Remove.Desc", "Remove Aspect Points off a pet.");
    public static final LangString COMMAND_ASPECT_POINTS_REWARD_DESC  = LangString.of("Command.AspectPoints.Reward.Desc", "Add Aspect Points to a player's active pet.");
    public static final LangString COMMAND_ASPECT_POINTS_PENALTY_DESC = LangString.of("Command.AspectPoints.Penalty.Desc", "Remove Aspect Points off a player's active pet.");


    public static final LangText COMMAND_XP_ADD_DONE = LangText.of("Command.XP.Add.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_SET_DONE = LangText.of("Command.XP.Set.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_REMOVE_DONE = LangText.of("Command.XP.Remove.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP off " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_REWARD_DONE = LangText.of("Command.XP.Reward.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_PENALTY_DONE = LangText.of("Command.XP.Penalty.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP off " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ASPECT_POINTS_ADD_DONE = LangText.of("Command.AspectPoints.Add.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_SET_DONE = LangText.of("Command.AspectPoints.Set.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_REMOVE_DONE = LangText.of("Command.AspectPoints.Remove.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points off " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_REWARD_DONE = LangText.of("Command.AspectPoints.Reward.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_PENALTY_DONE = LangText.of("Command.AspectPoints.Penalty.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points off " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ADD_DONE = LangText.of("Command.Add.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_ADD_ALL_DONE = LangText.of("Command.AddAll.Done",
        LIGHT_GRAY.wrap("Added all " + LIGHT_YELLOW.wrap(TIER_NAME) + " pets to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_REMOVE_DONE = LangText.of("Command.Remove.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " from " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_REMOVE_ALL_DONE = LangText.of("Command.RemoveAll.Done",
        LIGHT_GRAY.wrap("Removed all " + LIGHT_YELLOW.wrap(TIER_NAME) + " pets from " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );


    public static final LangText COMMAND_CATCH_ITEM_DONE = LangText.of("Command.CatchItem.Done",
        LIGHT_GRAY.wrap("Given " + LIGHT_YELLOW.wrap("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "!")
    );


    public static final LangText COMMAND_FOOD_DONE = LangText.of("Command.Food.Done",
        LIGHT_GRAY.wrap("Given " + LIGHT_YELLOW.wrap("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "!")
    );


    public static final LangText COMMAND_EGG_DONE = LangText.of("Command.Egg.Done",
        LIGHT_GRAY.wrap("Given " + LIGHT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " pet egg to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_MYSTERY_EGG_DONE = LangText.of("Command.MysteryEgg.Done",
        LIGHT_GRAY.wrap("Given " + LIGHT_YELLOW.wrap(TEMPLATE_NAME) + " mystery egg to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final LangText COMMAND_RENAME_DONE = LangText.of("Command.Rename.Done",
        LIGHT_GRAY.wrap("Renamed " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s pet to " + LIGHT_YELLOW.wrap(PET_NAME) + ".")
    );



    public static final LangText COMMAND_REVIVE_ERROR_ALIVE = LangText.of("Command.Revive.Error.Alive",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet is not dead!")
    );

    public static final LangText COMMAND_REVIVE_DONE = LangText.of("Command.Revive.Done",
        LIGHT_GRAY.wrap("Revived " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );



    public static final LangText COMMAND_CLEAR_INVENTORY_DONE = LangText.of("Command.ClearInventory.Done",
        LIGHT_GRAY.wrap("Cleared " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet inventory.")
    );


    public static final LangText COMMAND_RESET_PROGRESS_DONE = LangText.of("Command.ResetProgress.Done",
        LIGHT_GRAY.wrap("Reset leveling progress for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ACCESSORY_DONE = LangText.of("Command.Accessory.Done",
        LIGHT_GRAY.wrap("Given " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT + " x " + GENERIC_NAME) + " to " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final LangText PET_DESPAWN_DEATH = LangText.of("Pet.Despawn.Death",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_GENERIC_DEATH),
        LIGHT_RED.wrap(BOLD.wrap("Your Pet Died!")),
        LIGHT_GRAY.wrap("Revive it in your collection.")
    );

    public static final LangText PET_DESPAWN_DEFAULT = LangText.of("Pet.Despawn.Default",
        LIGHT_GRAY.wrap("Pet despawned.")
    );


    public static final LangText PET_CLAIM_ERROR_ALREADY_HAVE = LangText.of("Pet.Claim.Error.AlreadyHave",
        LIGHT_RED.wrap("You already have this pet!"));

    public static final LangText PET_CLAIM_ERROR_REACHED_LIMIT = LangText.of("Pet.Claim.Error.ReachedLimit",
        LIGHT_RED.wrap("You can not claim more than " + ORANGE.wrap(GENERIC_AMOUNT) + " " + TIER_NAME + " pets!"));

    public static final LangText PET_CLAIM_SUCCESS = LangText.of("Pet.Claim.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.wrap(BOLD.wrap("Pet Claimed!")),
        LIGHT_GRAY.wrap("Check out your collection: " + LIGHT_GREEN.wrap("/pet collection"))
    );

    public static final LangText PET_MYSTERY_EGG_HATCH = LangText.of("Pet.MysteryEgg.Hatch",
        SOUND.wrap(Sound.ENTITY_EVOKER_PREPARE_ATTACK),
        LIGHT_GRAY.wrap("You hatched " + LIGHT_PURPLE.wrap("Mystery Egg") + " into " + LIGHT_PURPLE.wrap(TIER_NAME + " " + TEMPLATE_DEFAULT_NAME) + " !")
    );


    public static final LangText CAPTURE_PROGRESS = LangText.of("Pet.Catch.Process.Progress",
        OUTPUT.wrap(0, 20),
        LIGHT_YELLOW.wrap(BOLD.wrap("Capturing...")),
        LIGHT_GRAY.wrap(GREEN.wrap(BOLD.wrap("Success ")) + "→ " + GREEN.wrap(GENERIC_SUCCESS + "% ") + "| " + RED.wrap(GENERIC_FAILURE + "% ") + "← " + RED.wrap(BOLD.wrap("Failure")))
    );

    public static final LangText CAPTURE_SUCCESS = LangText.of("Pet.Catch.Process.Success",
        OUTPUT.wrap(0, 50) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.wrap(BOLD.wrap("Mob Captured!")),
        LIGHT_GRAY.wrap("You captured " + LIGHT_GREEN.wrap(TEMPLATE_DEFAULT_NAME) + " " + GRAY.wrap("(" + WHITE.wrap(TIER_NAME) + ")"))
    );

    public static final LangText CAPTURE_FAIL_ESCAPED = LangText.of("Pet.Catch.Process.Escaped",
        OUTPUT.wrap(0, 50) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        ORANGE.wrap(BOLD.wrap("Mob Escaped!")),
        LIGHT_GRAY.wrap("You have to look for another one...")
    );

    public static final LangText CAPTURE_FAIL_UNLUCK = new LangText("Pet.Catch.Process.Failure",
        OUTPUT.wrap(0, 50) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Capture Failed!")),
        LIGHT_GRAY.wrap("You were unlucky. Give it another try!")
    );

    public static final LangText CAPTURE_FAIL_DISTANCE = LangText.of("Pet.Catch.Process.TooFarAway",
        OUTPUT.wrap(0, 50) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Capture Failed!")),
        LIGHT_GRAY.wrap("Stay closer next time!")
    );

    public static final LangText CAPTURE_ERROR_NOT_CAPTURABLE = LangText.of("Pet.Catch.Error.NotCatchable",
        LIGHT_GRAY.wrap("This " + LIGHT_RED.wrap(GENERIC_NAME) + " can not be captured.")
    );

    public static final LangText CAPTURE_ERROR_PERMISSION = LangText.of("Pet.Catch.Error.NotPermitted",
        LIGHT_GRAY.wrap("You don't have permissions to capture " + LIGHT_RED.wrap(GENERIC_NAME) + ".")
    );

    public static final LangText CAPTURE_ERROR_NOT_READY = LangText.of("Pet.Catch.Error.Conditions",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_NAME) + " is not ready for capture.")
    );



    public static final LangText LEVELING_LEVEL_UP = LangText.of("Pet.Leveling.LevelUp",
        OUTPUT.wrap(20, 40) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.wrap(BOLD.wrap("Pet Level Up!")),
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(PET_NAME) + " is now level " + LIGHT_GREEN.wrap(PET_LEVEL) + "!")
    );

    public static final LangText LEVELING_LEVEL_DOWN = LangText.of("Pet.Leveling.LevelDown",
        OUTPUT.wrap(20, 40) + SOUND.wrap(Sound.ENTITY_BLAZE_DEATH),
        LIGHT_RED.wrap(BOLD.wrap("Pet Level Down!")),
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(PET_NAME) + " is now level " + LIGHT_RED.wrap(PET_LEVEL) + " :(")
    );

    public static final LangText LEVELING_XP_LOSE_DEATH = LangText.of("Pet.XP.Lose.ByDeath",
        LIGHT_RED.wrap("[!] " + "Your pet lost " + GENERIC_AMOUNT + " XP for death.")
    );

    public static final LangText LEVELING_XP_GAIN = LangText.of("Pet.XP.Gain.ByMob",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        LIGHT_YELLOW.wrap(PET_NAME + ": ") + LIGHT_ORANGE.wrap("+" + GENERIC_AMOUNT + " XP")
    );



    public static final LangText PET_RENAME_PROMPT = LangText.of("Pet.Rename.Prompt",
        OUTPUT.wrap(20, -1) + SOUND.wrap(Sound.BLOCK_LAVA_POP),
        LIGHT_YELLOW.wrap(BOLD.wrap("Renaming")),
        LIGHT_GRAY.wrap("Enter new pet name.")
    );

    public static final LangText PET_RENAME_ERROR_NO_NAMETAG = LangText.of("Pet.Rename.Error.NoNametag",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Unable to Rename!")),
        LIGHT_GRAY.wrap("You need a " + LIGHT_RED.wrap("Name Tag") + " item.")
    );

    public static final LangText PET_RENAME_ERROR_TOO_LONG = LangText.of("Pet.Rename.Error.TooLong",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Name Too Long!")),
        LIGHT_GRAY.wrap("Max name length is " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " characters!")
    );

    public static final LangText PET_RENAME_ERROR_TOO_SHORT = LangText.of("Pet.Rename.Error.TooShort",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Name Too Short!")),
        LIGHT_GRAY.wrap("Min name length is " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " characters!")
    );

    public static final LangText PET_RENAME_ERROR_FORBIDDEN = LangText.of("Pet.Rename.Error.Forbidden",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Unacceptable Name!")),
        LIGHT_GRAY.wrap("Name contains " + LIGHT_RED.wrap("forbidden") + " characters!")
    );

    public static final LangText PET_RENAME_SUCCESS = LangText.of("Pet.Rename.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BELL),
        LIGHT_GREEN.wrap(BOLD.wrap("Pet Renamed!")),
        LIGHT_GRAY.wrap("You renamed your pet to " + LIGHT_GREEN.wrap(PET_NAME))
    );

    public static final LangText PET_REVIVE_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Pet.Revive.Error.NotEnoughFunds",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_RED.wrap(BOLD.wrap("Not Enough Funds!")),
        LIGHT_GRAY.wrap("You need " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " to revive " + LIGHT_RED.wrap(PET_NAME) + ".")
    );

    public static final LangText PET_REVIVE_SUCCESS = LangText.of("Pet.Revive.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ITEM_TOTEM_USE),
        LIGHT_GREEN.wrap(BOLD.wrap("Pet Revived!")),
        LIGHT_GRAY.wrap("You revived " + LIGHT_GREEN.wrap(PET_NAME) + " for " + LIGHT_GREEN.wrap(GENERIC_AMOUNT) + ".")
    );

    public static final LangText PET_ERROR_NO_ACTIVE_PET = LangText.of("Pet.Error.NoActivePet",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("No Active Pet!")),
        LIGHT_GRAY.wrap("You have to summon a pet to do that.")
    );

    public static final LangText PET_ERROR_NOT_YOUR = LangText.of("Pet.Error.NotYour",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap("You're not owning this pet!")
    );

    public static final LangText PET_SPAWN_ERROR_DEAD = LangText.of("Pet.Spawn.Error.Dead",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Pet is Dead!")),
        LIGHT_GRAY.wrap("You have to revive it before summon!")
    );

    public static final LangText PET_SPAWN_ERROR_ALREADY = LangText.of("Pet.Spawn.Error.Already",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Already Summoned!")),
        LIGHT_GRAY.wrap("You can have only one summoned pet.")
    );

    public static final LangText PET_SPAWN_ERROR_BAD_PLACE = LangText.of("Pet.Spawn.Error.BadPlace",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Bad Place!")),
        LIGHT_GRAY.wrap("There is not enough space for a pet.")
    );

    public static final LangText PET_ERROR_BAD_WORLD = LangText.of("Pet.Error.BadWorld",
        LIGHT_RED.wrap("Pets are disabled in this world.")
    );

    public static final LangText PET_RELEASE_SUCCESS = LangText.of("Pet.Release.Success",
        LIGHT_GRAY.wrap("You released " + LIGHT_YELLOW.wrap(PET_NAME) + "!")
    );

    public static final LangText PET_RELEASE_ERROR_BAD_WORLD = LangText.of("Pet.Release.Error.BadWorld",
        LIGHT_RED.wrap("You can not release pets in this world!")
    );

    public static final LangText PET_RELEASE_ERROR_PROTECTED_AREA = LangText.of("Pet.Release.Error.ProtectedArea",
        LIGHT_RED.wrap("You can't release pets here!")
    );

    public static final LangText PET_USER_ERROR_ALREADY_COLLECTED = LangText.of("Pet.Error.AlreadyCollected",
        LIGHT_RED.wrap(PLAYER_NAME + " already have " + TIER_NAME + " " + TEMPLATE_NAME + "!")
    );

    public static final LangText PET_USER_ERROR_NOT_COLLECTED = LangText.of("Pet.Error.NotCollected",
        LIGHT_RED.wrap("Player does not have such pet in his collection!")
    );

    public static final LangText PET_USER_ERROR_NOT_SUMMONED = LangText.of("Pet.Error.NotSummoned",
        LIGHT_RED.wrap("Player does not have a pet summoned!")
    );

    public static final LangText SHOP_PURCHASE_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Shop.Purchase.Error.NotEnoughFunds",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Not Enough Funds!")),
        LIGHT_GRAY.wrap("You need " + LIGHT_RED.wrap(GENERIC_PRICE) + " to purchase " + LIGHT_RED.wrap(TEMPLATE_DEFAULT_NAME) + ".")
    );

    public static final LangText SHOP_PURCHASE_SUCCESS = LangText.of("Shop.Purchase.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BELL),
        LIGHT_GREEN.wrap(BOLD.wrap("Successful Purchase!")),
        LIGHT_GRAY.wrap("You bought " + LIGHT_GREEN.wrap(TEMPLATE_DEFAULT_NAME) + " egg for " + LIGHT_GREEN.wrap(GENERIC_PRICE) + ".")
    );


    public static final LangText ACCESSORY_APPLY_ERROR_NOT_YOURS = LangText.of("Customizer.Interact.Pet.Error.NotYour",
        LIGHT_RED.wrap("You can't customize other player pets!")
    );

    public static final LangText ACCESSORY_APPLY_ERROR_WRONG_TYPE = LangText.of("Customizer.Interact.Pet.Error.WrongType",
        LIGHT_RED.wrap("This accessory is not applicable to this pet!")
    );

    public static final LangText ACCESSORY_APPLY_ERROR_ALREADY_HAS = LangText.of("Customizer.Interact.Pet.Error.AlreadyHas",
        LIGHT_RED.wrap("This accessory is already applied to this pet!")
    );


    public static final LangText ERROR_COMMAND_INVALID_TIER_ARGUMENT = LangText.of("Error.Command.Argument.InvalidTier",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid tier!")
    );

    public static final LangText ERROR_COMMAND_INVALID_PET_ARGUMENT = LangText.of("Error.Command.Argument.InvalidPet",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid pet!")
    );

    public static final LangText ERROR_COMMAND_INVALID_FOOD_CATEGORY_ARGUMENT = LangText.of("Error.Command.Argument.InvalidFoodCategory",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid food category!")
    );

    public static final LangText ERROR_COMMAND_INVALID_FOOD_ITEM_ARGUMENT = LangText.of("Error.Command.Argument.InvalidFoodItem",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid food item!")
    );

    public static final LangText ERROR_COMMAND_INVALID_VARIANT_ARGUMENT = LangText.of("Error.Command.Argument.InvalidCustomization",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid customization type!")
    );
}
