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
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.util.*;

import java.util.Collections;

public class BaseCommands {

    public static void load(@NotNull PetsPlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        rootNode.addChildren(DirectNode.builder(plugin, "add")
            .description(Lang.COMMAND_ADD_DESC)
            .permission(Perms.COMMAND_ADD)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> addOrRemovePet(plugin, context, arguments, true))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "remove")
            .description(Lang.COMMAND_REMOVE_DESC)
            .permission(Perms.COMMAND_REMOVE)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> addOrRemovePet(plugin, context, arguments, false))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "addall")
            .description(Lang.COMMAND_ADD_ALL_DESC)
            .permission(Perms.COMMAND_ADD_ALL)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> addOrRemoveAll(plugin, context, arguments, true))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "removeall")
            .description(Lang.COMMAND_REMOVE_ALL_DESC)
            .permission(Perms.COMMAND_REMOVE_ALL)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> addOrRemoveAll(plugin, context, arguments, false))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "egg")
            .description(Lang.COMMAND_EGG_DESC)
            .permission(Perms.COMMAND_EGG)
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> addEgg(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "mysteryegg")
            .description(Lang.COMMAND_MYSTERY_EGG_DESC)
            .permission(Perms.COMMAND_MYSTERY_EGG)
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> addMysteryEgg(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "food")
            .description(Lang.COMMAND_FOOD_DESC)
            .permission(Perms.COMMAND_FOOD)
            .withArgument(CommandArguments.foodCategoryArgument(plugin).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                .required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
                .withSamples(context -> {
                    FoodCategory category = plugin.getPetManager().getFoodCategory(context.getArgs()[context.getArgs().length - 2]);
                    if (category == null) return Collections.emptyList();

                    return category.getItemNames();
                })
            )
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "8", "16", "32", "64"))
            )
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> giveFood(plugin, context, arguments))
        );

        DirectNode collectionNode = DirectNode.builder(plugin, "collection")
            .description(Lang.COMMAND_COLLECTION_DESC)
            .permission(Perms.COMMAND_COLLECTION)
            .playerOnly()
            .withArgument(CommandArguments.tierArgument(plugin))
            .executes((context, arguments) -> openCollection(plugin, context, arguments)).build();

        if (Config.GENERAL_COLLECTION_DEFAULT_COMMAND.get()) {
            rootNode.setFallback(collectionNode);
        }

        rootNode.addChildren(collectionNode);

        rootNode.addChildren(DirectNode.builder(plugin, "menu")
            .description(Lang.COMMAND_MENU_DESC)
            .permission(Perms.COMMAND_MENU)
            .playerOnly()
            .executes((context, arguments) -> openPetMenu(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "rename")
            .description(Lang.COMMAND_RENAME_DESC)
            .permission(Perms.COMMAND_RENAME)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().complex().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
            .executes((context, arguments) -> renamePet(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "revive")
            .description(Lang.COMMAND_REVIVE_DESC)
            .permission(Perms.COMMAND_REVIVE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .executes((context, arguments) -> revivePet(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "clearinventory")
            .description(Lang.COMMAND_CLEAR_INVENTORY_DESC)
            .permission(Perms.COMMAND_CLEAR_INVENTORY)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.tierArgument(plugin).required())
            .withArgument(CommandArguments.templateArgument(plugin).required())
            .executes((context, arguments) -> clearInventory(plugin, context, arguments))
        );
    }

    public static boolean addOrRemovePet(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, boolean add) {
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);
            boolean hasPet = user.hasPet(template, tier);
            LangText message;

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
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            LangText message = add ? Lang.COMMAND_ADD_ALL_DONE : Lang.COMMAND_REMOVE_ALL_DONE;

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
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
        Template template = arguments.getArgument(CommandArguments.PET, Template.class);

        Players.addItem(player, template.createEgg(tier));

        context.send(Lang.COMMAND_EGG_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(tier.replacePlaceholders())
            .replace(template.replacePlaceholders()));
        return true;
    }

    public static boolean addMysteryEgg(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        Template template = arguments.getArgument(CommandArguments.PET, Template.class);
        ItemStack itemStack = plugin.getItemManager().createMysteryEgg(template);
        Players.addItem(player, itemStack);

        context.send(Lang.COMMAND_MYSTERY_EGG_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(template.replacePlaceholders()));
        return true;
    }

    public static boolean giveFood(@NotNull PetsPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        FoodCategory category = arguments.getArgument(CommandArguments.TYPE, FoodCategory.class);

        String itemName = arguments.getStringArgument(CommandArguments.NAME);
        FoodItem foodItem = category.getItem(itemName);
        if (foodItem == null) {
            context.send(Lang.ERROR_COMMAND_INVALID_FOOD_ITEM_ARGUMENT, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, itemName));
            return false;
        }

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT, 1);

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
        Tier tier = arguments.hasArgument(CommandArguments.TIER) ? arguments.getArgument(CommandArguments.TIER, Tier.class) : null;

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
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName()), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);
            String name = arguments.getStringArgument(CommandArguments.NAME);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.getMessage().send(context.getSender());
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
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.getMessage().send(context.getSender());
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
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            Tier tier = arguments.getArgument(CommandArguments.TIER, Tier.class);
            Template template = arguments.getArgument(CommandArguments.PET, Template.class);

            PetData petData = user.getPet(template, tier);
            if (petData == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.getMessage().send(context.getSender());
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
