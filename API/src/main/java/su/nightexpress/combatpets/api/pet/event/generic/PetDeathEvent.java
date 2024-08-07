package su.nightexpress.combatpets.api.pet.event.generic;

import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.event.PetEvent;

public class PetDeathEvent extends PetEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final EntityDeathEvent parentEvent;
    private       boolean          permanentDeath;
    private       boolean          dropInventory;
    private       boolean          dropEquipment;

    public PetDeathEvent(@NotNull ActivePet pet, @NotNull EntityDeathEvent parentEvent) {
        super(pet);
        this.parentEvent = parentEvent;
        this.setPermanentDeath(false);
        this.setDropInventory(false);
        this.setDropEquipment(false);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    public EntityDeathEvent getParentEvent() {
        return parentEvent;
    }

    public boolean isPermanentDeath() {
        return permanentDeath;
    }

    public void setPermanentDeath(boolean permanentDeath) {
        this.permanentDeath = permanentDeath;
    }

    public boolean isDropInventory() {
        return dropInventory;
    }

    public void setDropInventory(boolean dropInventory) {
        this.dropInventory = dropInventory;
    }

    public boolean isDropEquipment() {
        return dropEquipment;
    }

    public void setDropEquipment(boolean dropEquipment) {
        this.dropEquipment = dropEquipment;
    }
}
