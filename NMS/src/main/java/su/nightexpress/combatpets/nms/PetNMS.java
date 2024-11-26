package su.nightexpress.combatpets.nms;

import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.ActivePet;

import java.util.Set;
import java.util.function.Function;

public interface PetNMS {

    @NotNull Set<EntityType> getSupportedEntities();

    default boolean canSpawn(@NotNull EntityType entityType) {
        return this.getSupportedEntities().contains(entityType);
    }

    default void damageItem(@NotNull EquipmentSlot slot, @NotNull LivingEntity entity, @NotNull DamageSource source, int damage) {

    }

    @NotNull
    ActivePet spawnPet(@NotNull Template config, @NotNull Location location, @NotNull Function<LivingEntity, ActivePet> holderFunction);

    void sneak(@NotNull LivingEntity entity, boolean value);

    void setLeashedTo(@NotNull LivingEntity entity, @Nullable Entity holder);

    boolean hasNavigationPath(@NotNull LivingEntity entity);
}
