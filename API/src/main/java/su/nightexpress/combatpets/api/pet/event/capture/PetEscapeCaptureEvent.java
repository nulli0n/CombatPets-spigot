package su.nightexpress.combatpets.api.pet.event.capture;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;

public class PetEscapeCaptureEvent extends PetCaptureEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public PetEscapeCaptureEvent(@NotNull Player player, @NotNull LivingEntity entity, @NotNull Template template, @NotNull Tier tier) {
        super(player, entity, template, tier);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
