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
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.IntStream;

public class WardrobeCommands {

    private static final String ACCESSORY = "accessory";

    public static void load(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager, @NotNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal(ACCESSORY)
            .description(Lang.COMMAND_ACCESSORY_DESC)
            .permission(Perms.COMMAND_ACCESSORY)
            .withArguments(
                variantArgument(plugin),
                Arguments.string(CommandArguments.NAME)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                    .suggestions((reader, context) -> {
                        EntityVariant<?> variant = VariantRegistry.getVariant(reader.getArgs()[reader.getArgs().length - 2]);
                        return variant == null ? Collections.emptyList() : variant.getHandler().rawValues();
                    }),
                Arguments.integer(CommandArguments.AMOUNT).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .optional()
                    .suggestions((reader, context) -> IntStream.range(1, 17).boxed().map(String::valueOf).toList()),
                Arguments.player(CommandArguments.PLAYER)
                    .optional()
            )
            .executes((context, arguments) -> giveAccessory(plugin, manager, context, arguments))
        );
    }

    @NotNull
    private static ArgumentNodeBuilder<? extends EntityVariant<?>> variantArgument(@NotNull PetsPlugin plugin) {
        return Commands.argument(CommandArguments.TYPE, (context, string) -> Optional.ofNullable(VariantRegistry.getVariant(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_VARIANT_ARGUMENT))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_TYPE)
            .suggestions((reader, context) -> VariantRegistry.getVariantNames());
    }

    public static boolean giveAccessory(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        EntityVariant<?> variant = arguments.get(CommandArguments.TYPE, EntityVariant.class);
        String value = arguments.getString(CommandArguments.NAME);
        int amount = arguments.getInt(CommandArguments.AMOUNT, 1);

        ItemStack itemStack = manager.getItem(variant, value);
        itemStack.setAmount(amount);
        Players.addItem(player, itemStack);

        Lang.COMMAND_ACCESSORY_DONE.message().send(context.getSender(), replacer -> replacer
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getItemName(itemStack))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.forPlayer(player))
        );
        return true;
    }
}
