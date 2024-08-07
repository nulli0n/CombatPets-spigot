package su.nightexpress.combatpets.wardrobe.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.wardrobe.WardrobeManager;
import su.nightexpress.combatpets.wardrobe.util.EntityVariant;
import su.nightexpress.combatpets.wardrobe.util.VariantRegistry;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;

import java.util.Collections;
import java.util.stream.IntStream;

public class WardrobeCommands {

    private static final String ACCESSORY = "accessory";

    public static void load(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager) {
        plugin.getRootNode().addChildren(DirectNode.builder(plugin, ACCESSORY)
            .description(Lang.COMMAND_ACCESSORY_DESC)
            .permission(Perms.COMMAND_ACCESSORY)
            .withArgument(variantArgument(plugin).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                .required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
                .withSamples(context -> {
                    EntityVariant<?> variant = VariantRegistry.getVariant(context.getArgs()[context.getArgs().length - 2]);
                    return variant == null ? Collections.emptyList() : variant.getHandler().rawValues();
                })
            )
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> IntStream.range(1, 17).boxed().map(String::valueOf).toList())
            )
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> giveAccessory(plugin, manager, context, arguments))
        );
    }

    public static void unload(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager) {
        plugin.getRootNode().removeChildren(ACCESSORY);
    }

    @NotNull
    private static ArgumentBuilder<? extends EntityVariant<?>> variantArgument(@NotNull PetsPlugin plugin) {
        return CommandArgument.builder(CommandArguments.TYPE, (string, context) -> VariantRegistry.getVariant(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_VARIANT_ARGUMENT)
            .withSamples(context -> VariantRegistry.getVariantNames());
    }

    public static boolean giveAccessory(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        EntityVariant<?> variant = arguments.getArgument(CommandArguments.TYPE, EntityVariant.class);
        String value = arguments.getStringArgument(CommandArguments.NAME);
        int amount = arguments.getIntArgument(CommandArguments.AMOUNT, 1);

        ItemStack itemStack = manager.getItem(variant, value);
        itemStack.setAmount(amount);
        Players.addItem(player, itemStack);

        Lang.COMMAND_ACCESSORY_DONE.getMessage()
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getItemName(itemStack))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.forPlayer(player))
            .send(context.getSender());
        return true;
    }
}
