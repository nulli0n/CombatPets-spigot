package su.nightexpress.combatpets.nms.mc_1_21.brain;

import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PetAI {

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    public static boolean setAngerTarget(@NotNull Mob pet, @NotNull LivingEntity target, boolean force) {
        if (!force) {
            if (!Sensor.isEntityAttackableIgnoringLineOfSight(pet, target)) return false;
        }

        pet.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 60L & 20L);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 60L * 20L);
        pet.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        return true;
    }

    public static void setAvoidTargetAndDontHuntForAWhile(@NotNull Mob pet, @NotNull LivingEntity target) {
        pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        pet.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, target, RETREAT_DURATION.sample(pet.level().random));
    }

    @NotNull
    public static Optional<LivingEntity> getAngerTarget(@NotNull Mob pet) {
        return pet.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        //return BehaviorUtils.getLivingEntityFromUUIDMemory(pet, MemoryModuleType.ANGRY_AT);
    }

}
