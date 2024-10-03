package su.nightexpress.combatpets.config;

import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.wardrobe.handler.AgeVariantHandler;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class Lang extends CoreLang {

    public static final LangEnum<AgeVariantHandler.Type> AGE_TYPE    = LangEnum.of("PetVariant.AgeType", AgeVariantHandler.Type.class);
    public static final LangEnum<DyeColor>               DYE_COLOR   = LangEnum.of("PetVariant.DyeColor", DyeColor.class);
    public static final LangEnum<Fox.Type>               FOX_TYPE    = LangEnum.of("PetVariant.FoxType", Fox.Type.class);
    public static final LangEnum<Horse.Color>            HORSE_COLOR = LangEnum.of("PetVariant.HorseColor", Horse.Color.class);
    public static final LangEnum<Horse.Style>            HORSE_STYLE = LangEnum.of("PetVariant.FoxType", Horse.Style.class);
    public static final LangEnum<Llama.Color>            LLAMA_COLOR = LangEnum.of("PetVariant.LlamaColor", Llama.Color.class);

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
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_SET_DONE = LangText.of("Command.XP.Set.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_REMOVE_DONE = LangText.of("Command.XP.Remove.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP off " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_REWARD_DONE = LangText.of("Command.XP.Reward.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_XP_PENALTY_DONE = LangText.of("Command.XP.Penalty.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP off " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ASPECT_POINTS_ADD_DONE = LangText.of("Command.AspectPoints.Add.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Aspect Points to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_SET_DONE = LangText.of("Command.AspectPoints.Set.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Aspect Points for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_REMOVE_DONE = LangText.of("Command.AspectPoints.Remove.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Aspect Points off " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_REWARD_DONE = LangText.of("Command.AspectPoints.Reward.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Aspect Points to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );

    public static final LangText COMMAND_ASPECT_POINTS_PENALTY_DONE = LangText.of("Command.AspectPoints.Penalty.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Aspect Points off " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ADD_DONE = LangText.of("Command.Add.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(TIER_NAME + " " + TEMPLATE_NAME) + " to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_ADD_ALL_DONE = LangText.of("Command.AddAll.Done",
        LIGHT_GRAY.enclose("Added all " + LIGHT_YELLOW.enclose(TIER_NAME) + " pets to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_REMOVE_DONE = LangText.of("Command.Remove.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(TIER_NAME + " " + TEMPLATE_NAME) + " from " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s collection!")
    );

    public static final LangText COMMAND_REMOVE_ALL_DONE = LangText.of("Command.RemoveAll.Done",
        LIGHT_GRAY.enclose("Removed all " + LIGHT_YELLOW.enclose(TIER_NAME) + " pets from " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s collection!")
    );


    public static final LangText COMMAND_CATCH_ITEM_DONE = LangText.of("Command.CatchItem.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "!")
    );


    public static final LangText COMMAND_FOOD_DONE = LangText.of("Command.Food.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "!")
    );


    public static final LangText COMMAND_EGG_DONE = LangText.of("Command.Egg.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose(TIER_NAME + " " + TEMPLATE_NAME) + " pet egg to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_MYSTERY_EGG_DONE = LangText.of("Command.MysteryEgg.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose(TEMPLATE_NAME) + " mystery egg to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );


    public static final LangText COMMAND_RENAME_DONE = LangText.of("Command.Rename.Done",
        LIGHT_GRAY.enclose("Renamed " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s pet to " + LIGHT_YELLOW.enclose(PET_NAME) + ".")
    );



    public static final LangText COMMAND_REVIVE_ERROR_ALIVE = LangText.of("Command.Revive.Error.Alive",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet is not dead!")
    );

    public static final LangText COMMAND_REVIVE_DONE = LangText.of("Command.Revive.Done",
        LIGHT_GRAY.enclose("Revived " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );



    public static final LangText COMMAND_CLEAR_INVENTORY_DONE = LangText.of("Command.ClearInventory.Done",
        LIGHT_GRAY.enclose("Cleared " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet inventory.")
    );


    public static final LangText COMMAND_RESET_PROGRESS_DONE = LangText.of("Command.ResetProgress.Done",
        LIGHT_GRAY.enclose("Reset leveling progress for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(PET_NAME) + " pet.")
    );


    public static final LangText COMMAND_ACCESSORY_DONE = LangText.of("Command.Accessory.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT + " x " + GENERIC_NAME) + " to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final LangText PET_DESPAWN_DEATH = LangText.of("Pet.Despawn.Death",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_GENERIC_DEATH),
        LIGHT_RED.enclose(BOLD.enclose("Your Pet Died!")),
        LIGHT_GRAY.enclose("Revive it in your collection.")
    );

    public static final LangText PET_DESPAWN_DEFAULT = LangText.of("Pet.Despawn.Default",
        LIGHT_GRAY.enclose("Pet despawned.")
    );


    public static final LangText PET_CLAIM_ERROR_ALREADY_HAVE = LangText.of("Pet.Claim.Error.AlreadyHave",
        LIGHT_RED.enclose("You already have this pet!"));

    public static final LangText PET_CLAIM_ERROR_REACHED_LIMIT = LangText.of("Pet.Claim.Error.ReachedLimit",
        LIGHT_RED.enclose("You can not claim more than " + ORANGE.enclose(GENERIC_AMOUNT) + " " + TIER_NAME + " pets!"));

    public static final LangText PET_CLAIM_SUCCESS = LangText.of("Pet.Claim.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.enclose(BOLD.enclose("Pet Claimed!")),
        LIGHT_GRAY.enclose("Check out your collection: " + LIGHT_GREEN.enclose("/pet collection"))
    );

    public static final LangText PET_MYSTERY_EGG_HATCH = LangText.of("Pet.MysteryEgg.Hatch",
        SOUND.enclose(Sound.ENTITY_EVOKER_PREPARE_ATTACK),
        LIGHT_GRAY.enclose("You hatched " + LIGHT_PURPLE.enclose("Mystery Egg") + " into " + LIGHT_PURPLE.enclose(TIER_NAME + " " + TEMPLATE_DEFAULT_NAME) + " !")
    );


    public static final LangText CAPTURE_PROGRESS = LangText.of("Pet.Catch.Process.Progress",
        OUTPUT.enclose(0, 20),
        LIGHT_YELLOW.enclose(BOLD.enclose("Capturing...")),
        LIGHT_GRAY.enclose(GREEN.enclose(BOLD.enclose("Success ")) + "→ " + GREEN.enclose(GENERIC_SUCCESS + "% ") + "| " + RED.enclose(GENERIC_FAILURE + "% ") + "← " + RED.enclose(BOLD.enclose("Failure")))
    );

    public static final LangText CAPTURE_SUCCESS = LangText.of("Pet.Catch.Process.Success",
        OUTPUT.enclose(0, 50) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.enclose(BOLD.enclose("Mob Captured!")),
        LIGHT_GRAY.enclose("You captured " + LIGHT_GREEN.enclose(TEMPLATE_DEFAULT_NAME) + " " + GRAY.enclose("(" + WHITE.enclose(TIER_NAME) + ")"))
    );

    public static final LangText CAPTURE_FAIL_ESCAPED = LangText.of("Pet.Catch.Process.Escaped",
        OUTPUT.enclose(0, 50) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        ORANGE.enclose(BOLD.enclose("Mob Escaped!")),
        LIGHT_GRAY.enclose("You have to look for another one...")
    );

    public static final LangText CAPTURE_FAIL_UNLUCK = new LangText("Pet.Catch.Process.Failure",
        OUTPUT.enclose(0, 50) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Capture Failed!")),
        LIGHT_GRAY.enclose("You were unlucky. Give it another try!")
    );

    public static final LangText CAPTURE_FAIL_DISTANCE = LangText.of("Pet.Catch.Process.TooFarAway",
        OUTPUT.enclose(0, 50) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Capture Failed!")),
        LIGHT_GRAY.enclose("Stay closer next time!")
    );

    public static final LangText CAPTURE_ERROR_NOT_CAPTURABLE = LangText.of("Pet.Catch.Error.NotCatchable",
        LIGHT_GRAY.enclose("This " + LIGHT_RED.enclose(GENERIC_NAME) + " can not be captured.")
    );

    public static final LangText CAPTURE_ERROR_PERMISSION = LangText.of("Pet.Catch.Error.NotPermitted",
        LIGHT_GRAY.enclose("You don't have permissions to capture " + LIGHT_RED.enclose(GENERIC_NAME) + ".")
    );

    public static final LangText CAPTURE_ERROR_NOT_READY = LangText.of("Pet.Catch.Error.Conditions",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_NAME) + " is not ready for capture.")
    );



    public static final LangText LEVELING_LEVEL_UP = LangText.of("Pet.Leveling.LevelUp",
        OUTPUT.enclose(20, 40) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.enclose(BOLD.enclose("Pet Level Up!")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose(PET_NAME) + " is now level " + LIGHT_GREEN.enclose(PET_LEVEL) + "!")
    );

    public static final LangText LEVELING_LEVEL_DOWN = LangText.of("Pet.Leveling.LevelDown",
        OUTPUT.enclose(20, 40) + SOUND.enclose(Sound.ENTITY_BLAZE_DEATH),
        LIGHT_RED.enclose(BOLD.enclose("Pet Level Down!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(PET_NAME) + " is now level " + LIGHT_RED.enclose(PET_LEVEL) + " :(")
    );

    public static final LangText LEVELING_XP_LOSE_DEATH = LangText.of("Pet.XP.Lose.ByDeath",
        LIGHT_RED.enclose("[!] " + "Your pet lost " + GENERIC_AMOUNT + " XP for death.")
    );

    public static final LangText LEVELING_XP_GAIN = LangText.of("Pet.XP.Gain.ByMob",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_YELLOW.enclose(PET_NAME + ": ") + LIGHT_ORANGE.enclose("+" + GENERIC_AMOUNT + " XP")
    );



    public static final LangText PET_RENAME_PROMPT = LangText.of("Pet.Rename.Prompt",
        OUTPUT.enclose(20, -1) + SOUND.enclose(Sound.BLOCK_LAVA_POP),
        LIGHT_YELLOW.enclose(BOLD.enclose("Renaming")),
        LIGHT_GRAY.enclose("Enter new pet name.")
    );

    public static final LangText PET_RENAME_ERROR_NO_NAMETAG = LangText.of("Pet.Rename.Error.NoNametag",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unable to Rename!")),
        LIGHT_GRAY.enclose("You need a " + LIGHT_RED.enclose("Name Tag") + " item.")
    );

    public static final LangText PET_RENAME_ERROR_TOO_LONG = LangText.of("Pet.Rename.Error.TooLong",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Name Too Long!")),
        LIGHT_GRAY.enclose("Max name length is " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " characters!")
    );

    public static final LangText PET_RENAME_ERROR_TOO_SHORT = LangText.of("Pet.Rename.Error.TooShort",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Name Too Short!")),
        LIGHT_GRAY.enclose("Min name length is " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " characters!")
    );

    public static final LangText PET_RENAME_ERROR_FORBIDDEN = LangText.of("Pet.Rename.Error.Forbidden",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unacceptable Name!")),
        LIGHT_GRAY.enclose("Name contains " + LIGHT_RED.enclose("forbidden") + " characters!")
    );

    public static final LangText PET_RENAME_SUCCESS = LangText.of("Pet.Rename.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_NOTE_BLOCK_BELL),
        LIGHT_GREEN.enclose(BOLD.enclose("Pet Renamed!")),
        LIGHT_GRAY.enclose("You renamed your pet to " + LIGHT_GREEN.enclose(PET_NAME))
    );

    public static final LangText PET_REVIVE_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Pet.Revive.Error.NotEnoughFunds",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_RED.enclose(BOLD.enclose("Not Enough Funds!")),
        LIGHT_GRAY.enclose("You need " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " to revive " + LIGHT_RED.enclose(PET_NAME) + ".")
    );

    public static final LangText PET_REVIVE_SUCCESS = LangText.of("Pet.Revive.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ITEM_TOTEM_USE),
        LIGHT_GREEN.enclose(BOLD.enclose("Pet Revived!")),
        LIGHT_GRAY.enclose("You revived " + LIGHT_GREEN.enclose(PET_NAME) + " for " + LIGHT_GREEN.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText PET_ERROR_NO_ACTIVE_PET = LangText.of("Pet.Error.NoActivePet",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("No Active Pet!")),
        LIGHT_GRAY.enclose("You have to summon a pet to do that.")
    );

    public static final LangText PET_ERROR_NOT_YOUR = LangText.of("Pet.Error.NotYour",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose("You're not owning this pet!")
    );

    public static final LangText PET_SPAWN_ERROR_DEAD = LangText.of("Pet.Spawn.Error.Dead",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Pet is Dead!")),
        LIGHT_GRAY.enclose("You have to revive it before summon!")
    );

    public static final LangText PET_SPAWN_ERROR_ALREADY = LangText.of("Pet.Spawn.Error.Already",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Already Summoned!")),
        LIGHT_GRAY.enclose("You can have only one summoned pet.")
    );

    public static final LangText PET_SPAWN_ERROR_BAD_PLACE = LangText.of("Pet.Spawn.Error.BadPlace",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Bad Place!")),
        LIGHT_GRAY.enclose("There is not enough space for a pet.")
    );

    public static final LangText PET_ERROR_BAD_WORLD = LangText.of("Pet.Error.BadWorld",
        LIGHT_RED.enclose("Pets are disabled in this world.")
    );

    public static final LangText PET_RELEASE_SUCCESS = LangText.of("Pet.Release.Success",
        LIGHT_GRAY.enclose("You released " + LIGHT_YELLOW.enclose(PET_NAME) + "!")
    );

    public static final LangText PET_RELEASE_ERROR_BAD_WORLD = LangText.of("Pet.Release.Error.BadWorld",
        LIGHT_RED.enclose("You can not release pets in this world!")
    );

    public static final LangText PET_RELEASE_ERROR_PROTECTED_AREA = LangText.of("Pet.Release.Error.ProtectedArea",
        LIGHT_RED.enclose("You can't release pets here!")
    );

    public static final LangText PET_USER_ERROR_ALREADY_COLLECTED = LangText.of("Pet.Error.AlreadyCollected",
        LIGHT_RED.enclose(PLAYER_NAME + " already have " + TIER_NAME + " " + TEMPLATE_NAME + "!")
    );

    public static final LangText PET_USER_ERROR_NOT_COLLECTED = LangText.of("Pet.Error.NotCollected",
        LIGHT_RED.enclose("Player does not have such pet in his collection!")
    );

    public static final LangText PET_USER_ERROR_NOT_SUMMONED = LangText.of("Pet.Error.NotSummoned",
        LIGHT_RED.enclose("Player does not have a pet summoned!")
    );

    public static final LangText SHOP_PURCHASE_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Shop.Purchase.Error.NotEnoughFunds",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Not Enough Funds!")),
        LIGHT_GRAY.enclose("You need " + LIGHT_RED.enclose(GENERIC_PRICE) + " to purchase " + LIGHT_RED.enclose(TEMPLATE_DEFAULT_NAME) + ".")
    );

    public static final LangText SHOP_PURCHASE_SUCCESS = LangText.of("Shop.Purchase.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_NOTE_BLOCK_BELL),
        LIGHT_GREEN.enclose(BOLD.enclose("Successful Purchase!")),
        LIGHT_GRAY.enclose("You bought " + LIGHT_GREEN.enclose(TEMPLATE_DEFAULT_NAME) + " egg for " + LIGHT_GREEN.enclose(GENERIC_PRICE) + ".")
    );


    public static final LangText ACCESSORY_APPLY_ERROR_NOT_YOURS = LangText.of("Customizer.Interact.Pet.Error.NotYour",
        LIGHT_RED.enclose("You can't customize other player pets!")
    );

    public static final LangText ACCESSORY_APPLY_ERROR_WRONG_TYPE = LangText.of("Customizer.Interact.Pet.Error.WrongType",
        LIGHT_RED.enclose("This accessory is not applicable to this pet!")
    );

    public static final LangText ACCESSORY_APPLY_ERROR_ALREADY_HAS = LangText.of("Customizer.Interact.Pet.Error.AlreadyHas",
        LIGHT_RED.enclose("This accessory is already applied to this pet!")
    );


    public static final LangText ERROR_COMMAND_INVALID_TIER_ARGUMENT = LangText.of("Error.Command.Argument.InvalidTier",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid tier!")
    );

    public static final LangText ERROR_COMMAND_INVALID_PET_ARGUMENT = LangText.of("Error.Command.Argument.InvalidPet",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid pet!")
    );

    public static final LangText ERROR_COMMAND_INVALID_FOOD_CATEGORY_ARGUMENT = LangText.of("Error.Command.Argument.InvalidFoodCategory",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid food category!")
    );

    public static final LangText ERROR_COMMAND_INVALID_FOOD_ITEM_ARGUMENT = LangText.of("Error.Command.Argument.InvalidFoodItem",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid food item!")
    );

    public static final LangText ERROR_COMMAND_INVALID_VARIANT_ARGUMENT = LangText.of("Error.Command.Argument.InvalidCustomization",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid customization type!")
    );
}
