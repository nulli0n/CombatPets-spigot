package su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior;

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
import org.bukkit.craftbukkit.v1_19_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.NotNull;

public class PetIdleBehaviors {

    private static final UniformInt FOLLOW_RANGE = UniformInt.of(2, 30);

    @NotNull
    public static OneShot<LivingEntity> lookAtOwner() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(builder.absent(MemoryModuleType.LOOK_TARGET), builder.present(MemoryModuleType.LIKED_PLAYER)).apply(builder, (memLookTarget, memLikedPlayer) -> {
                return (world, mob, l) -> {
                    LivingEntity owner = world.getPlayerByUUID(builder.get(memLikedPlayer));
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
                builder.present(MemoryModuleType.LIKED_PLAYER),
                builder.registered(MemoryModuleType.LOOK_TARGET),
                builder.absent(MemoryModuleType.WALK_TARGET))
                .apply(builder, (memLikedPlayer, memLookTarget, memWalkTarget) -> {
                    return (world, pet, i) -> {
                        LivingEntity owner = world.getPlayerByUUID(builder.get(memLikedPlayer));
                        if (owner == null) return false;

                        boolean isFarAway = !pet.closerThan(owner, FOLLOW_RANGE.getMaxValue() + 1);
                        if (isFarAway) {
                            pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                            pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                            if (pet.level == owner.level) {
                                pet.moveTo(owner.getX(), owner.getY(), owner.getZ());
                            }
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
