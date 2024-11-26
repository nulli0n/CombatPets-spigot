package su.nightexpress.combatpets.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.item.ItemType;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.Players;

public class ItemListener extends AbstractListener<PetsPlugin> {

    private final ItemManager manager;

    public ItemListener(@NotNull PetsPlugin plugin, @NotNull ItemManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemUse(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType().isAir()) return;
        if (event.useItemInHand() == Event.Result.DENY) return;

        ItemType type = this.manager.getItemType(itemStack);
        if (type == null) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Player player = event.getPlayer();

        if (!Players.isBedrock(player)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        }

        this.manager.onItemUse(player, itemStack, type);
    }
}
