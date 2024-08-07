package su.nightexpress.combatpets.api.pet.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;

public abstract class PetEvent extends Event {

    protected ActivePet pet;

    public PetEvent(@NotNull ActivePet pet) {
        this.pet = pet;
    }

    @NotNull
    public ActivePet getPet() {
        return this.pet;
    }
}
