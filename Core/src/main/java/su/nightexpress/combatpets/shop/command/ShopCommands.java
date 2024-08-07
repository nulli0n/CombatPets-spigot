package su.nightexpress.combatpets.shop.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;

public class ShopCommands {

    private static final String SHOP = "shop";

    public static void load(@NotNull PetsPlugin plugin, @NotNull ShopManager manager) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(DirectNode.builder(plugin, SHOP)
            .description(Lang.COMMAND_SHOP_DESC)
            .permission(Perms.COMMAND_SHOP)
            .executes((context, arguments) -> openShop(plugin, manager, context, arguments))
        );
    }

    public static void unload(@NotNull PetsPlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.removeChildren(SHOP);
    }

    public static boolean openShop(@NotNull PetsPlugin plugin, @NotNull ShopManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.openTiersMenu(player);
        return true;
    }
}
