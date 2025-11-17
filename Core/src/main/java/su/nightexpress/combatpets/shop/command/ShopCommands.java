package su.nightexpress.combatpets.shop.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;

public class ShopCommands {

    private static final String SHOP = "shop";

    public static void load(@NotNull PetsPlugin plugin, @NotNull ShopManager manager, @NotNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal(SHOP)
            .description(Lang.COMMAND_SHOP_DESC)
            .permission(Perms.COMMAND_SHOP)
            .executes((context, arguments) -> openShop(plugin, manager, context, arguments))
        );
    }

    public static boolean openShop(@NotNull PetsPlugin plugin, @NotNull ShopManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.openTiersMenu(player);
        return true;
    }
}
