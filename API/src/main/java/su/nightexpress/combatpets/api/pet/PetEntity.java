package su.nightexpress.combatpets.api.pet;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PetEntity {

    void setGoals();

    @Deprecated default UUID getUniqueId(){
        return null;
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
