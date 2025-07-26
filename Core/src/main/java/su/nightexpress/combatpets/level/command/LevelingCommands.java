package su.nightexpress.combatpets.level.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class LevelingCommands {

    private static final String XP             = "xp";
    private static final String RESET_PROGRESS = "resetprogress";

    public enum Mode {
        ADD, SET, REMOVE
    }

    public static void load(@NotNull PetsPlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(ChainedNode.builder(plugin, XP)
            .description(Lang.COMMAND_XP_DESC)
            .permission(Perms.COMMAND_XP)
            .addDirect("add", builder -> builderXPChange(plugin, builder, Mode.ADD))
            .addDirect("set", builder -> builderXPChange(plugin, builder, Mode.SET))
            .addDirect("remove", builder -> builderXPChange(plugin, builder, Mode.REMOVE))
            .addDirect("reward", builder -> builderXPReward(plugin, builder, Mode.ADD))
            .addDirect("penalty", builder -> builderXPReward(plugin, builder, Mode.REMOVE))
        );

        rootNode.addChildren(DirectNode.builder(plugin, RESET_PROGRESS)
            .description(Lang.COMMAND_RESET_PROGRESS_DESC)
            .permission(Perms.COMMAND_RESET_PROGRESS)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> resetLeveling(plugin, context, arguments))
        );
    }

    public static void unload(@NotNull PetsPlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.removeChildren(XP);
        rootNode.removeChildren(RESET_PROGRESS);
    }

    private static DirectNodeBuilder builderXPChange(@NotNull PetsPlugin plugin, @NotNull DirectNodeBuilder builder, @NotNull Mode mode) {
        LangString description = null;
        UniPermission permission = null;
        switch (mode) {
            case ADD -> {
                description = Lang.COMMAND_XP_ADD_DESC;
                permission = Perms.COMMAND_XP_ADD;
            }
            case SET -> {
                description = Lang.COMMAND_XP_SET_DESC;
                permission = Perms.COMMAND_XP_SET;
            }
            case REMOVE -> {
                description = Lang.COMMAND_XP_REMOVE_DESC;
                permission = Perms.COMMAND_XP_REMOVE;
            }
        }

        return builder
            .description(description)
            .permission(permission)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "10", "100"))
            )
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> changeXP(plugin, context, arguments, mode));
    }

    private static DirectNodeBuilder builderXPReward(@NotNull PetsPlugin plugin, @NotNull DirectNodeBuilder builder, @NotNull Mode mode) {
        LangString description = mode == Mode.ADD ? Lang.COMMAND_XP_REWARD_DESC : Lang.COMMAND_XP_PENALTY_DESC;
        UniPermission permission = mode == Mode.ADD ? Perms.COMMAND_XP_REWARD : Perms.COMMAND_XP_PENALTY;

        return builder
            .description(description)
            .permission(permission)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "10", "100"))
            )
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> rewardXP(plugin, context, arguments, mode));
    }

    public static boolean changeXP(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            int amount = arguments.getIntArgument(CommandArguments.AMOUNT);

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);

            PetData data = user.getPet(template, tier);
            if (data == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.getMessage().send(context.getSender());
                return;
            }

            Player player = user.getPlayer();
            ActivePet activePet = player == null ? null : plugin.getPetManager().getPlayerPet(player);
            if (activePet != null && (activePet.getTier() != tier || activePet.getTemplate() != template)) {
                activePet = null;
            }
//            if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
//                activePet.addXP(amount);
//            }
//            else {
//                data.addXP(amount);
//            }

            LangText message = null;

            switch (mode) {
                case ADD -> {
                    message = Lang.COMMAND_XP_ADD_DONE;
                    if (activePet != null) activePet.addXP(amount); else data.addXP(amount);
                }
                case SET -> {
                    message = Lang.COMMAND_XP_SET_DONE;
                    if (activePet != null) activePet.setXP(amount); else data.setXP(amount);
                }
                case REMOVE -> {
                    message = Lang.COMMAND_XP_REMOVE_DONE;
                    if (activePet != null) activePet.removeXP(amount); else data.removeXP(amount);
                }
            }

            plugin.getUserManager().save(user);

            message.getMessage()
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .send(context.getSender());
        });


        return true;
    }

    public static boolean rewardXP(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
        if (mode == Mode.SET) return false;

        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        if (amount == 0) return false;

        PetUser user = plugin.getUserManager().getOrFetch(player);
        ActivePet activePet = plugin.getPetManager().getPlayerPet(player);
        if (activePet == null) {
            Lang.PET_USER_ERROR_NOT_SUMMONED.getMessage().send(context.getSender());
            return false;
        }

        LangText message;
        if (mode == Mode.ADD) {
            message = Lang.COMMAND_XP_REWARD_DONE;
            activePet.addXP(amount);
        }
        else {
            message = Lang.COMMAND_XP_PENALTY_DONE;
            activePet.removeXP(amount);
        }

        plugin.getUserManager().save(user);

        message.getMessage()
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.PET_NAME, activePet.getName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .send(context.getSender());

        return true;
    }

    public static boolean resetLeveling(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);

            PetData data = user.getPet(template, tier);
            if (data == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.getMessage().send(context.getSender());
                return;
            }

            Player player = user.getPlayer();
            ActivePet activePet = player == null ? null : plugin.getPetManager().getPlayerPet(player);
            if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
                activePet.resetLeveling();
            }
            else data.resetXP();

            plugin.getUserManager().save(user);

            Lang.COMMAND_RESET_PROGRESS_DONE.getMessage()
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
                .send(context.getSender());
        });

        return true;
    }
}
