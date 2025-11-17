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
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class LevelingCommands {

    private static final String XP             = "xp";
    private static final String RESET_PROGRESS = "resetprogress";

    public enum Mode {
        ADD, SET, REMOVE
    }

    public static void load(@NotNull PetsPlugin plugin, @NotNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.hub(XP)
            .description(Lang.COMMAND_XP_DESC)
            .permission(Perms.COMMAND_XP)
            .branch(Commands.literal("add", builder -> builderXPChange(plugin, builder, Mode.ADD)))
            .branch(Commands.literal("set", builder -> builderXPChange(plugin, builder, Mode.SET)))
            .branch(Commands.literal("remove", builder -> builderXPChange(plugin, builder, Mode.REMOVE)))
            .branch(Commands.literal("reward", builder -> builderXPReward(plugin, builder, Mode.ADD)))
            .branch(Commands.literal("penalty", builder -> builderXPReward(plugin, builder, Mode.REMOVE)))
        );

        rootNode.branch(Commands.literal(RESET_PROGRESS)
            .description(Lang.COMMAND_RESET_PROGRESS_DESC)
            .permission(Perms.COMMAND_RESET_PROGRESS)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> resetLeveling(plugin, context, arguments))
        );
    }

    private static LiteralNodeBuilder builderXPChange(@NotNull PetsPlugin plugin, @NotNull LiteralNodeBuilder builder, @NotNull Mode mode) {
        TextLocale description = null;
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
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1", "10", "100")),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> changeXP(plugin, context, arguments, mode));
    }

    private static LiteralNodeBuilder builderXPReward(@NotNull PetsPlugin plugin, @NotNull LiteralNodeBuilder builder, @NotNull Mode mode) {
        TextLocale description = mode == Mode.ADD ? Lang.COMMAND_XP_REWARD_DESC : Lang.COMMAND_XP_PENALTY_DESC;
        UniPermission permission = mode == Mode.ADD ? Perms.COMMAND_XP_REWARD : Perms.COMMAND_XP_PENALTY;

        return builder
            .description(description)
            .permission(permission)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1", "10", "100")),
                Arguments.player(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> rewardXP(plugin, context, arguments, mode));
    }

    public static boolean changeXP(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
        plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            int amount = arguments.getInt(CommandArguments.AMOUNT);

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);

            PetData data = user.getPet(template, tier);
            if (data == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
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

            MessageLocale message = null;

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

            message.message().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            );
        });


        return true;
    }

    public static boolean rewardXP(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
        if (mode == Mode.SET) return false;

        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        int amount = arguments.getInt(CommandArguments.AMOUNT);
        if (amount == 0) return false;

        PetUser user = plugin.getUserManager().getOrFetch(player);
        ActivePet activePet = plugin.getPetManager().getPlayerPet(player);
        if (activePet == null) {
            Lang.PET_USER_ERROR_NOT_SUMMONED.message().send(context.getSender());
            return false;
        }

        MessageLocale message;
        if (mode == Mode.ADD) {
            message = Lang.COMMAND_XP_REWARD_DONE;
            activePet.addXP(amount);
        }
        else {
            message = Lang.COMMAND_XP_PENALTY_DONE;
            activePet.removeXP(amount);
        }

        plugin.getUserManager().save(user);

        message.message().send(context.getSender(), replacer -> replacer
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.PET_NAME, activePet.getName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
        );

        return true;
    }

    public static boolean resetLeveling(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);

            PetData data = user.getPet(template, tier);
            if (data == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
                return;
            }

            Player player = user.getPlayer();
            ActivePet activePet = player == null ? null : plugin.getPetManager().getPlayerPet(player);
            if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
                activePet.resetLeveling();
            }
            else data.resetXP();

            plugin.getUserManager().save(user);

            Lang.COMMAND_RESET_PROGRESS_DONE.message().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
            );
        });

        return true;
    }
}
