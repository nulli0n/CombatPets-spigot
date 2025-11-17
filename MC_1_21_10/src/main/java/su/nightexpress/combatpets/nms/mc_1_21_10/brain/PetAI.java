package su.nightexpress.combatpets.nms.mc_1_21_10.brain;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.type.CombatMode;

import java.util.Optional;

public class PetAI {

    private static final int MAX_TICKS_TO_AUTO_ATTACK = 50;
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    public static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean isDamagedBySomeone(@NotNull LivingEntity entity) {
        return entity.tickCount - entity.getLastHurtByMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && entity.getLastHurtByMob() != entity;
    }

    public static boolean isAttackedSomeone(@NotNull LivingEntity entity) {
        return entity.tickCount - entity.getLastHurtMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && entity.getLastHurtMob() != entity;
    }

    @Nullable
    public static LivingEntity findTarget(@NotNull LivingEntity pet, @NotNull ActivePet activePet) {
        Player owner = ((CraftPlayer) activePet.getOwner()).getHandle();
        CombatMode combatMode = activePet.getCombatMode();

        LivingEntity angerTarget = null;

        if (isDamagedBySomeone(pet) && combatMode != CombatMode.PASSIVE) {
            angerTarget = pet.getLastHurtByMob();
        }

        if (angerTarget == null && (combatMode == CombatMode.PROTECTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE)) {
            if (PetAI.isDamagedBySomeone(owner)) {
                angerTarget = owner.getLastHurtByMob();
            }
        }

        if (angerTarget == null && (combatMode == CombatMode.SUPPORTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE)) {
            if (PetAI.isAttackedSomeone(owner)) {
                angerTarget = owner.getLastHurtMob();

                // Projectiles set lastHurtMob even if damage event was cancelled
                // so need to double check if target was actually damaged.
                if (angerTarget != null && angerTarget.getLastHurtByMob() != owner) {
                    angerTarget = null;
                }
            }
        }

        return angerTarget;
    }

    public static <T extends LivingEntity> void updateActivity(@NotNull Mob entity, @NotNull Brain<T> brain) {
        if (PetAI.getAttackTarget(entity).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else if (entity.isInWater()) {
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
    public static Optional<LivingEntity> getAttackTarget(@NotNull Mob pet) {
        return pet.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
    }

    public static boolean hasAttackTarget(@NotNull Mob mob) {
        return getAttackTarget(mob).isPresent();
    }
}
