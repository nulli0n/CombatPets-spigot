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
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.*;

public class CaptureCommands {

    private static final String CAPTURE_ITEM = "captureitem";

    public static void load(@NotNull PetsPlugin plugin, @NotNull CaptureManager manager) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(DirectNode.builder(plugin, CAPTURE_ITEM)
            .description(Lang.COMMAND_CAPTURE_ITEM_DESC)
            .permission(Perms.COMMAND_CAPTURE_ITEM)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "8", "16"))
            )
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> giveItem(plugin, manager, context, arguments))
        );
    }

    public static void unload(@NotNull PetsPlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.removeChildren(CAPTURE_ITEM);
    }

    public static boolean giveItem(@NotNull PetsPlugin plugin, @NotNull CaptureManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) {
            context.errorBadPlayer();
            return false;
        }

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT, 1);
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
