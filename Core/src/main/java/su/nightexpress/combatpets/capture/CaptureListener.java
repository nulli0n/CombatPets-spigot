package su.nightexpress.combatpets.capture;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

public class CaptureListener extends AbstractListener<PetsPlugin> {

    private final CaptureManager manager;

    public CaptureListener(@NotNull PetsPlugin plugin, @NotNull CaptureManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCaptureQuit(PlayerQuitEvent event) {
        this.manager.stopCapture(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCaptureDeath(PlayerDeathEvent event) {
        this.manager.stopCapture(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCaptureClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;

        if (this.manager.isBeingCaptured(entity)) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getHand());
        if (itemStack == null || !this.manager.isCaptureItem(itemStack)) return;

        event.setCancelled(true);

        this.manager.tryCapture(player, entity, itemStack);
    }
}
