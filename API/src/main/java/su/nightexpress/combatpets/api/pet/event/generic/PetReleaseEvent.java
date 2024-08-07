package su.nightexpress.combatpets.api.pet.event.generic;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;

public class PetReleaseEvent extends Event implements Cancellable {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player   player;
    private final Tier     tier;
    private final Template template;

    private boolean cancelled;

    public PetReleaseEvent(@NotNull Player player, @NotNull Tier tier, @NotNull Template template) {
        this.player = player;
        this.tier = tier;
        this.template = template;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public Tier getTier() {
        return tier;
    }

    @NotNull
    public Template getTemplate() {
        return template;
    }
}
