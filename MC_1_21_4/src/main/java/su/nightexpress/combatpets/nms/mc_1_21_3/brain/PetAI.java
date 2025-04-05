package su.nightexpress.combatpets.nms.mc_1_21_3.brain;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PetAI {

    private static final int MAX_TICKS_TO_AUTO_ATTACK = 50;
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    public static boolean ownerDamaged(@NotNull Player owner) {
        return owner.tickCount - owner.getLastHurtByMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && owner.getLastHurtByMob() != owner;
    }

    public static boolean ownerAttacked(@NotNull Player owner) {
        return owner.tickCount - owner.getLastHurtMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && owner.getLastHurtMob() != owner;
    }

    public static <T extends LivingEntity> void updateActivity(@NotNull Mob entity, @NotNull Brain<T> brain) {
        if (PetAI.getAngerTarget(entity).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else if (entity.isInWaterOrBubble()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.SWIM));
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        entity.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    public static boolean setAngerTarget(@NotNull Mob pet, @NotNull LivingEntity target, boolean force) {
        if (!force) {
            if (!Sensor.isEntityAttackableIgnoringLineOfSight((ServerLevel) pet.level(), pet, target)) return false;
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
