package su.nightexpress.combatpets.wardrobe.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.wardrobe.WardrobeManager;
import su.nightexpress.combatpets.wardrobe.util.EntityVariant;
import su.nightexpress.nightcore.manager.AbstractListener;

public class WardrobeListener extends AbstractListener<PetsPlugin> {

    private final WardrobeManager manager;

    public WardrobeListener(@NotNull PetsPlugin plugin, @NotNull WardrobeManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCustomizerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) return;

        EntityVariant<?> variant = this.manager.getType(item);
        if (variant == null) return;

        String value = this.manager.getValue(item);
        if (value == null) return;

        event.setCancelled(true);

        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;

        this.manager.applyAccessory(player, entity, item, variant, value);
    }
}
