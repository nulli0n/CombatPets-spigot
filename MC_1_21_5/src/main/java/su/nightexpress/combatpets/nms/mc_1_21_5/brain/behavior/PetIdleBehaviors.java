package su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R4.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;

import java.util.HashSet;

public class PetIdleBehaviors {

    private static final UniformInt FOLLOW_RANGE = UniformInt.of(2, 30);

    @NotNull
    public static OneShot<LivingEntity> lookAtOwner() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(
                builder.absent(MemoryModuleType.LOOK_TARGET)
            ).apply(builder, (memLookTarget) -> {
                return (world, mob, i) -> {
                    ActivePet holder = PetEntityBridge.getByMobId(mob.getUUID());
                    if (holder == null) return false;

                    CraftPlayer craftPlayer = (CraftPlayer) holder.getOwner();
                    ServerPlayer owner = craftPlayer.getHandle();

                    memLookTarget.set(new EntityTracker(owner, true));
                    return true;
                };
            });
        });
    }

    @NotNull
    public static BehaviorControl<Mob> followOwner() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(
                builder.registered(MemoryModuleType.LOOK_TARGET),
                builder.absent(MemoryModuleType.WALK_TARGET))
                .apply(builder, (memLookTarget, memWalkTarget) -> {
                    return (world, pet, i) -> {
                        ActivePet holder = PetEntityBridge.getByMobId(pet.getUUID());
                        if (holder == null) return false;

                        CraftPlayer craftPlayer = (CraftPlayer) holder.getOwner();
                        ServerPlayer owner = craftPlayer.getHandle();

                        boolean isFarAway = !pet.closerThan(owner, FOLLOW_RANGE.getMaxValue() + 1);
                        if (isFarAway) {
                            pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                            pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                            pet.teleportTo(world, owner.getX(), owner.getY(), owner.getZ(), new HashSet<>(), 0F, 0F, true);
                            return false;
                        }

                        if (!pet.closerThan(owner, FOLLOW_RANGE.getMinValue())) {
                            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(pet, owner, EntityTargetEvent.TargetReason.FOLLOW_LEADER);
                            if (event.isCancelled()) return false;

                            boolean forEyes = pet.getType() == EntityType.ALLAY;

                            WalkTarget walkTarget = new WalkTarget(new EntityTracker(owner, forEyes), 1F, FOLLOW_RANGE.getMinValue() - 1);
                            memLookTarget.set(new EntityTracker(owner, true));
                            memWalkTarget.set(walkTarget);
                            return true;
                        }
                        return false;
                    };
                });
        });
    }
}
