package su.nightexpress.combatpets.capture.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.capture.CaptureManager;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.*;

public class CaptureCommands {

    private static final String CAPTURE_ITEM = "captureitem";

    public static void load(@NotNull PetsPlugin plugin, @NotNull CaptureManager manager, @NotNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal(CAPTURE_ITEM)
            .description(Lang.COMMAND_CAPTURE_ITEM_DESC)
            .permission(Perms.COMMAND_CAPTURE_ITEM)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1", "8", "16")).optional(),
                Arguments.player(CommandArguments.PLAYER).optional()
            )
            .executes((context, arguments) -> giveItem(plugin, manager, context, arguments))
        );
    }

    @Deprecated
    public static void unload(@NotNull PetsPlugin plugin) {
        /*ChainedNode rootNode = plugin.getRootNode();

        rootNode.removeChildren(CAPTURE_ITEM);*/
    }

    public static boolean giveItem(@NotNull PetsPlugin plugin, @NotNull CaptureManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer() && !arguments.contains(CommandArguments.PLAYER)) {
            context.printUsage();
            return false;
        }

        Player player = context.isPlayer() ? context.getPlayerOrThrow() : arguments.getPlayer(CommandArguments.PLAYER);

        int amount = arguments.getInt(CommandArguments.AMOUNT, 1);
        if (amount == 0) return false;

        ItemStack captureItem = manager.createCaptureItem();
        Players.addItem(player, captureItem, amount);

        context.send(Lang.COMMAND_CATCH_ITEM_DONE, replacer -> replacer
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(captureItem))
            .replace(Placeholders.PLAYER_NAME, player.getName()));

        return true;
    }
}
