package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PetEntityBridge {

    public static final Map<PetEntity, ActivePet> BY_PET = new WeakHashMap<>();
    public static final Map<UUID, ActivePet>      BY_ID  = new HashMap<>();

    public static ActivePet getByPet(@NotNull PetEntity entity) {
        return BY_PET.get(entity);
    }

    public static boolean isPet(@NotNull LivingEntity entity) {
        return getByMob(entity) != null;
    }

    @Nullable
    public static ActivePet getByMob(@NotNull LivingEntity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            return null;
        }
        return getByMobId(entity.getUniqueId());
    }

    @Nullable
    public static ActivePet getByPlayer(@NotNull Player player) {
        return BY_ID.get(player.getUniqueId());
    }

    @Nullable
    public static ActivePet getByMobId(@NotNull UUID uuid) {
        return BY_ID.get(uuid);
    }

    @NotNull
    public static Collection<ActivePet> getAll() {
        return new HashSet<>(BY_PET.values());
    }

    public static void addHolder(@NotNull PetEntity entity, @NotNull ActivePet holder) {
        BY_PET.put(entity, holder);
        BY_ID.put(holder.getOwner().getUniqueId(), holder);
        BY_ID.put(holder.getEntity().getUniqueId(), holder);
    }

    /*public static void removeHolder(@NotNull PetEntity entity) {
        IPetHolder holder = BY_ENTITY.remove(entity);
        if (holder == null) return;

        BY_ID.remove(holder.getEntity().getUniqueId());
        BY_ID.remove(holder.getOwner().getUniqueId());
    }*/

    public static void removeHolder(@NotNull ActivePet holder) {
        BY_PET.values().remove(holder);
        BY_ID.remove(holder.getEntity().getUniqueId());
        BY_ID.remove(holder.getOwner().getUniqueId());
    }
}
