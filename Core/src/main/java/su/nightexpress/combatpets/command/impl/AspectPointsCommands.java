package su.nightexpress.combatpets.command.impl;

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

public class AspectPointsCommands {

    private static final String ASPECT_POINTS = "aspectpoints";

    public enum Mode {
        ADD, SET, REMOVE
    }

    public static void load(@NotNull PetsPlugin plugin, @NotNull HubNodeBuilder rootNode) {

        rootNode.branch(Commands.hub(ASPECT_POINTS)
            .description(Lang.COMMAND_APOINTS_DESC)
            .permission(Perms.COMMAND_ASPECT_POINTS)
            .branch(Commands.literal("add", builder -> builderChange(plugin, builder, Mode.ADD)))
            .branch(Commands.literal("set", builder -> builderChange(plugin, builder, Mode.SET)))
            .branch(Commands.literal("remove", builder -> builderChange(plugin, builder, Mode.REMOVE)))
            .branch(Commands.literal("reward", builder -> builderReward(plugin, builder, Mode.ADD)))
            .branch(Commands.literal("penalty", builder -> builderReward(plugin, builder, Mode.REMOVE)))
        );
    }

//    public static void unload(@NotNull PetsPlugin plugin) {
//        ChainedNode rootNode = plugin.getRootNode();
//
//        rootNode.removeChildren(ASPECT_POINTS);
//    }

    private static LiteralNodeBuilder builderChange(@NotNull PetsPlugin plugin, @NotNull LiteralNodeBuilder builder, @NotNull Mode mode) {
        TextLocale description = null;
        UniPermission permission = null;
        switch (mode) {
            case ADD -> {
                description = Lang.COMMAND_ASPECT_POINTS_ADD_DESC;
                permission = Perms.COMMAND_ASPECT_POINTS_ADD;
            }
            case SET -> {
                description = Lang.COMMAND_ASPECT_POINTS_SET_DESC;
                permission = Perms.COMMAND_ASPECT_POINTS_SET;
            }
            case REMOVE -> {
                description = Lang.COMMAND_ASPECT_POINTS_REMOVE_DESC;
                permission = Perms.COMMAND_ASPECT_POINTS_REMOVE;
            }
        }

        return builder
            .description(description)
            .permission(permission)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT, 1)
                .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .suggestions((reader, context) -> Lists.newList("1", "10", "100")),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> changePoints(plugin, context, arguments, mode));
    }

    private static LiteralNodeBuilder builderReward(@NotNull PetsPlugin plugin, @NotNull LiteralNodeBuilder builder, @NotNull Mode mode) {
        TextLocale description = mode == Mode.ADD ? Lang.COMMAND_ASPECT_POINTS_REWARD_DESC : Lang.COMMAND_ASPECT_POINTS_PENALTY_DESC;
        UniPermission permission = mode == Mode.ADD ? Perms.COMMAND_ASPECT_POINTS_REWARD : Perms.COMMAND_ASPECT_POINTS_PENALTY;

        return builder
            .description(description)
            .permission(permission)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1", "10", "100")),
                Arguments.player(CommandArguments.PLAYER)
            )
            .executes((context, arguments) -> rewardPoints(plugin, context, arguments, mode));
    }

    public static boolean changePoints(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
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

//            Player player = user.getPlayer();
//            ActivePet activePet = player == null ? null : plugin.getPetManager().getPlayerPet(player);
//            if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
//                activePet.addAspectPoints(amount);
//            }
//            else {
//                data.addAspectPoints(amount);
//            }

            MessageLocale message = null;

            switch (mode) {
                case ADD -> {
                    message = Lang.COMMAND_ASPECT_POINTS_ADD_DONE;
                    data.addAspectPoints(amount);
                }
                case SET -> {
                    message = Lang.COMMAND_ASPECT_POINTS_SET_DONE;
                    data.setAspectPoints(amount);
                }
                case REMOVE -> {
                    message = Lang.COMMAND_ASPECT_POINTS_REMOVE_DONE;
                    data.removeAspectPoints(amount);
                }
            }

            plugin.getUserManager().save(user);

            context.send(message, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount)));
        });


        return true;
    }

    public static boolean rewardPoints(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Mode mode) {
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
            message = Lang.COMMAND_ASPECT_POINTS_REWARD_DONE;
            activePet.addAspectPoints(amount);
        }
        else {
            message = Lang.COMMAND_ASPECT_POINTS_PENALTY_DONE;
            activePet.removeAspectPoints(amount);
        }

        plugin.getUserManager().save(user);

        context.send(message, replacer -> replacer
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.PET_NAME, activePet.getName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount)));

        return true;
    }
}
