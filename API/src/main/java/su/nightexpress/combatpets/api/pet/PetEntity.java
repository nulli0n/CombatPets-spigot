package su.nightexpress.combatpets.api.pet;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface PetEntity {

    void setGoals();

    @NotNull
    default Optional<ActivePet> holder() {
        return Optional.ofNullable(PetEntityBridge.getByPet(this));
    }

    @NotNull
    default ActivePet getHolder() {
        return PetEntityBridge.getByPet(this);
    }

    @NotNull
    default UUID getOwnerId() {
        return this.getHolder().getOwner().getUniqueId();
    }
}
