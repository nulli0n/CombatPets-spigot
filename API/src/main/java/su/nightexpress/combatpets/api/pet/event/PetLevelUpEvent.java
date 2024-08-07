package su.nightexpress.combatpets.api.pet.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;

public class PetLevelUpEvent extends PetEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public PetLevelUpEvent(@NotNull ActivePet pet) {
        super(pet);
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
