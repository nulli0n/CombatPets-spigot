package su.nightexpress.combatpets.command.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.*;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;

import java.util.Collections;

public class BaseCommands {

    public static void load(@NotNull PetsPlugin plugin, @NotNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal("reload")
            .description(CoreLang.COMMAND_RELOAD_DESC)
            .permission(Perms.COMMAND_RELOAD)
            .executes((context, arguments) -> {
                plugin.doReload(context.getSender());
                return true;
            })
        );

        rootNode.branch(Commands.literal("add")
            .description(Lang.COMMAND_ADD_DESC)
            .permission(Perms.COMMAND_ADD)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addOrRemovePet(plugin, context, arguments, true))
        );

        rootNode.branch(Commands.literal("remove")
            .description(Lang.COMMAND_REMOVE_DESC)
            .permission(Perms.COMMAND_REMOVE)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addOrRemovePet(plugin, context, arguments, false))
        );

        rootNode.branch(Commands.literal("addall")
            .description(Lang.COMMAND_ADD_ALL_DESC)
            .permission(Perms.COMMAND_ADD_ALL)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addOrRemoveAll(plugin, context, arguments, true))
        );

        rootNode.branch(Commands.literal("removeall")
            .description(Lang.COMMAND_REMOVE_ALL_DESC)
            .permission(Perms.COMMAND_REMOVE_ALL)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addOrRemoveAll(plugin, context, arguments, false))
        );

        rootNode.branch(Commands.literal("egg")
            .description(Lang.COMMAND_EGG_DESC)
            .permission(Perms.COMMAND_EGG)
            .withArguments(
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.playerName(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addEgg(plugin, context, arguments))
        );

        rootNode.branch(Commands.literal("mysteryegg")
            .description(Lang.COMMAND_MYSTERY_EGG_DESC)
            .permission(Perms.COMMAND_MYSTERY_EGG)
            .withArguments(
                CommandArguments.templateArgument(plugin),
                Arguments.player(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> addMysteryEgg(plugin, context, arguments))
        );

        rootNode.branch(Commands.literal("food")
            .description(Lang.COMMAND_FOOD_DESC)
            .permission(Perms.COMMAND_FOOD)
            .withArguments(
                CommandArguments.foodCategoryArgument(plugin),
                Arguments.string(CommandArguments.NAME)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                    .suggestions((reader, context) -> {
                        FoodCategory category = plugin.getPetManager().getFoodCategory(reader.getArgs()[reader.getArgs().length - 2]);
                        if (category == null) return Collections.emptyList();

                        return category.getItemNames();
                    }),
                Arguments.integer(CommandArguments.AMOUNT, 1).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1", "8", "16", "32", "64")).optional(),
                Arguments.player(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> giveFood(plugin, context, arguments))
        );
        
        rootNode.branch(Commands.literal("collection")
            .description(Lang.COMMAND_COLLECTION_DESC)
            .permission(Perms.COMMAND_COLLECTION)
            .playerOnly()
            .withArguments(CommandArguments.tierArgument(plugin).optional())
            .executes((context, arguments) -> openCollection(plugin, context, arguments))
        );

        if (Config.GENERAL_COLLECTION_DEFAULT_COMMAND.get()) {
            rootNode.executes((context, arguments) -> openCollection(plugin, context, arguments));
        }

        rootNode.branch(Commands.literal("menu")
            .description(Lang.COMMAND_MENU_DESC)
            .permission(Perms.COMMAND_MENU)
            .playerOnly()
            .executes((context, arguments) -> openPetMenu(plugin, context, arguments))
        );

        rootNode.branch(Commands.literal("rename")
            .description(Lang.COMMAND_RENAME_DESC)
            .permission(Perms.COMMAND_RENAME)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin),
                Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
            )
            .executes((context, arguments) -> renamePet(plugin, context, arguments))
        );

        rootNode.branch(Commands.literal("revive")
            .description(Lang.COMMAND_REVIVE_DESC)
            .permission(Perms.COMMAND_REVIVE)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin)
            )
            .executes((context, arguments) -> revivePet(plugin, context, arguments))
        );

        rootNode.branch(Commands.literal("clearinventory")
            .description(Lang.COMMAND_CLEAR_INVENTORY_DESC)
            .permission(Perms.COMMAND_CLEAR_INVENTORY)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin)
            )
            .executes((context, arguments) -> clearInventory(plugin, context, arguments))
        );
    }

    public static boolean addOrRemovePet(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, boolean add) {
        plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);
            boolean hasPet = user.hasPet(template, tier);
            MessageLocale message;

            if (add) {
                if (!hasPet) {
                    plugin.getPetManager().addToCollection(user, tier, template);
                    message = Lang.COMMAND_ADD_DONE;
                }
                else message = Lang.PET_USER_ERROR_ALREADY_COLLECTED;
            }
            else {
                if (hasPet) {
                    plugin.getPetManager().removeFromCollection(user, tier, template);
                    message = Lang.COMMAND_REMOVE_DONE;
                }
                else message = Lang.PET_USER_ERROR_NOT_COLLECTED;
            }

            plugin.getUserManager().save(user);

            context.send(message, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(template.replacePlaceholders())
                .replace(tier.replacePlaceholders()));
        });

        return true;
    }

    public static boolean addOrRemoveAll(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, boolean add) {
        plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            MessageLocale message = add ? Lang.COMMAND_ADD_ALL_DONE : Lang.COMMAND_REMOVE_ALL_DONE;

            plugin.getPetManager().getTemplates().forEach(template -> {
                if (add) {
                    plugin.getPetManager().addToCollection(user, tier, template);
                }
                else plugin.getPetManager().removeFromCollection(user, tier, template);
            });

            plugin.getUserManager().save(user);

            context.send(message, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(tier.replacePlaceholders()));
        });

        return true;
    }

    public static boolean addEgg(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
        Template template = arguments.get(CommandArguments.PET, Template.class);

        Players.addItem(player, template.createEgg(tier));

        context.send(Lang.COMMAND_EGG_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(tier.replacePlaceholders())
            .replace(template.replacePlaceholders()));
        return true;
    }

    public static boolean addMysteryEgg(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        Template template = arguments.get(CommandArguments.PET, Template.class);
        ItemStack itemStack = plugin.getItemManager().createMysteryEgg(template);
        Players.addItem(player, itemStack);

        context.send(Lang.COMMAND_MYSTERY_EGG_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(template.replacePlaceholders()));
        return true;
    }

    public static boolean giveFood(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        FoodCategory category = arguments.get(CommandArguments.TYPE, FoodCategory.class);

        String itemName = arguments.getString(CommandArguments.NAME);
        FoodItem foodItem = category.getItem(itemName);
        if (foodItem == null) {
            context.send(Lang.ERROR_COMMAND_INVALID_FOOD_ITEM_ARGUMENT, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, itemName));
            return false;
        }

        int amount = arguments.getInt(CommandArguments.AMOUNT, 1);

        ItemStack itemStack = foodItem.getItem();
        itemStack.setAmount(amount);

        Players.addItem(player, itemStack);

        context.send(Lang.COMMAND_FOOD_DONE, replacer -> replacer
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(itemStack))
            .replace(Placeholders.PLAYER_NAME, player.getName()));
        return true;
    }

    public static boolean openCollection(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Tier tier = arguments.contains(CommandArguments.TIER) ? arguments.get(CommandArguments.TIER, Tier.class) : null;

        if (tier != null) {
            plugin.getPetManager().openPetsCollection(player, tier);
        }
        else {
            plugin.getPetManager().openTierCollection(player);
        }
        return true;
    }

    public static boolean openPetMenu(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        plugin.getPetManager().openPetMenu(player);
        return true;
    }

    public static boolean renamePet(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);
            String name = arguments.getString(CommandArguments.NAME);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
                return;
            }

            ActivePet holder = null;
            Player player = user.getPlayer();
            if (player != null) {
                holder = plugin.getPetManager().getPlayerPet(player);
            }

            if (holder != null && holder.getTier() == tier && holder.getTemplate() == template) {
                holder.setName(name);
            }
            else {
                petData.setName(name);
            }

            plugin.getUserManager().save(user);

            context.send(Lang.COMMAND_RENAME_DONE, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, name));
        });

        return true;
    }

    public static boolean revivePet(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
                return;
            }

            if (petData.isAlive()) {
                context.send(Lang.COMMAND_REVIVE_ERROR_ALIVE, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.PET_NAME, petData.getName()));
                return;
            }

            petData.revive();

            plugin.getUserManager().save(user);

            context.send(Lang.COMMAND_REVIVE_DONE, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, petData.getName())
                .replace(tier.replacePlaceholders())
                .replace(template.replacePlaceholders()));
        });
        return true;
    }

    public static boolean clearInventory(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
            Template template = arguments.get(CommandArguments.PET, Template.class);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
                return;
            }

            petData.getInventory().clear();

            plugin.getUserManager().save(user);

            context.send(Lang.COMMAND_CLEAR_INVENTORY_DONE, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, petData.getName())
                .replace(tier.replacePlaceholders())
                .replace(template.replacePlaceholders()));
        });
        return true;
    }
}
