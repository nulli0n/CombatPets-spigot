package su.nightexpress.combatpets.config;

import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class Perms {

    private static final String PREFIX         = "combatpets.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final  String PREFIX_CAPTURE = PREFIX + "capture.";

    public static final UniPermission PLUGIN  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission CAPTURE = new UniPermission(PREFIX_CAPTURE + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_ADD                   = new UniPermission(PREFIX_COMMAND + "add");
    public static final UniPermission COMMAND_ADD_ALL               = new UniPermission(PREFIX_COMMAND + "addall");
    public static final UniPermission COMMAND_REMOVE                = new UniPermission(PREFIX_COMMAND + "remove");
    public static final UniPermission COMMAND_REMOVE_ALL            = new UniPermission(PREFIX_COMMAND + "removeall");
    public static final UniPermission COMMAND_FOOD                  = new UniPermission(PREFIX_COMMAND + "food");
    public static final UniPermission COMMAND_EGG                   = new UniPermission(PREFIX_COMMAND + "egg");
    public static final UniPermission COMMAND_MYSTERY_EGG           = new UniPermission(PREFIX_COMMAND + "mysteryegg");
    public static final UniPermission COMMAND_SHOP                  = new UniPermission(PREFIX_COMMAND + "shop");
    public static final UniPermission COMMAND_CAPTURE_ITEM          = new UniPermission(PREFIX_COMMAND + "captureitem");
    public static final UniPermission COMMAND_COLLECTION            = new UniPermission(PREFIX_COMMAND + "collection");
    public static final UniPermission COMMAND_ACCESSORY             = new UniPermission(PREFIX_COMMAND + "accessory");
    public static final UniPermission COMMAND_MENU                  = new UniPermission(PREFIX_COMMAND + "menu");
    public static final UniPermission COMMAND_RENAME                = new UniPermission(PREFIX_COMMAND + "rename");
    public static final UniPermission COMMAND_REVIVE                = new UniPermission(PREFIX_COMMAND + "revive");
    public static final UniPermission COMMAND_CLEAR_INVENTORY       = new UniPermission(PREFIX_COMMAND + "clearinventory");
    public static final UniPermission COMMAND_RELOAD                = new UniPermission(PREFIX_COMMAND + "reload");
    public static final UniPermission COMMAND_RESET_PROGRESS        = new UniPermission(PREFIX_COMMAND + "resetprogress");
    public static final UniPermission COMMAND_XP                    = new UniPermission(PREFIX_COMMAND + "xp");
    public static final UniPermission COMMAND_XP_ADD                = new UniPermission(PREFIX_COMMAND + "xp.add");
    public static final UniPermission COMMAND_XP_SET                = new UniPermission(PREFIX_COMMAND + "xp.set");
    public static final UniPermission COMMAND_XP_REMOVE             = new UniPermission(PREFIX_COMMAND + "xp.remove");
    public static final UniPermission COMMAND_XP_REWARD             = new UniPermission(PREFIX_COMMAND + "xp.reward");
    public static final UniPermission COMMAND_XP_PENALTY            = new UniPermission(PREFIX_COMMAND + "xp.penalty");
    public static final UniPermission COMMAND_ASPECT_POINTS         = new UniPermission(PREFIX_COMMAND + "aspectpoints");
    public static final UniPermission COMMAND_ASPECT_POINTS_ADD     = new UniPermission(PREFIX_COMMAND + "aspectpoints.add");
    public static final UniPermission COMMAND_ASPECT_POINTS_SET     = new UniPermission(PREFIX_COMMAND + "aspectpoints.set");
    public static final UniPermission COMMAND_ASPECT_POINTS_REMOVE  = new UniPermission(PREFIX_COMMAND + "aspectpoints.remove");
    public static final UniPermission COMMAND_ASPECT_POINTS_REWARD  = new UniPermission(PREFIX_COMMAND + "aspectpoints.reward");
    public static final UniPermission COMMAND_ASPECT_POINTS_PENALTY = new UniPermission(PREFIX_COMMAND + "aspectpoints.penalty");

    public static final UniPermission BYPASS_RELEASE_DISABLED = new UniPermission(PREFIX_BYPASS + "release.disallow");
    public static final UniPermission BYPASS_NAME_LENGTH      = new UniPermission(PREFIX_BYPASS + "name.length");
    public static final UniPermission BYPASS_NAME_WORDS       = new UniPermission(PREFIX_BYPASS + "name.words");

    static {
        PLUGIN.addChildren(COMMAND, BYPASS, CAPTURE);

        COMMAND.addChildren(
            COMMAND_ADD,
            COMMAND_ADD_ALL,
            COMMAND_REMOVE,
            COMMAND_REMOVE_ALL,
            COMMAND_FOOD,
            COMMAND_EGG,
            COMMAND_MYSTERY_EGG,
            COMMAND_SHOP,
            COMMAND_CAPTURE_ITEM,
            COMMAND_COLLECTION,
            COMMAND_ACCESSORY,
            COMMAND_MENU,
            COMMAND_RENAME,
            COMMAND_REVIVE,
            COMMAND_CLEAR_INVENTORY,
            COMMAND_RELOAD,
            COMMAND_RESET_PROGRESS,
            COMMAND_XP,
            COMMAND_XP_ADD,
            COMMAND_XP_SET,
            COMMAND_XP_REMOVE,
            COMMAND_XP_REWARD,
            COMMAND_XP_PENALTY,
            COMMAND_ASPECT_POINTS,
            COMMAND_ASPECT_POINTS_ADD,
            COMMAND_ASPECT_POINTS_SET,
            COMMAND_ASPECT_POINTS_REMOVE,
            COMMAND_ASPECT_POINTS_REWARD,
            COMMAND_ASPECT_POINTS_PENALTY
        );

        BYPASS.addChildren(
            BYPASS_RELEASE_DISABLED,
            BYPASS_NAME_LENGTH,
            BYPASS_NAME_WORDS
        );
    }
}
