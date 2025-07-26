package su.nightexpress.combatpets.nms.mc_1_21_8.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.behavior.PetIdleBehaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PetBrain {

    protected static final ImmutableList<SensorType<? extends Sensor<? extends LivingEntity>>> SENSOR_TYPES;
    protected static final ImmutableList<MemoryModuleType<?>>                                  MEMORY_TYPES;

    static {
        SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY
            //SensorType.PIGLIN_SPECIFIC_SENSOR
        );
        MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.IS_IN_WATER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.LIKED_PLAYER,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.UNIVERSAL_ANGER,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.RIDE_TARGET,
            MemoryModuleType.ATE_RECENTLY,
            MemoryModuleType.RAM_COOLDOWN_TICKS,
            MemoryModuleType.RAM_TARGET,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryModuleType.NEAREST_REPELLENT);
    }

    public static <E extends Mob> ImmutableList<SensorType<? extends Sensor<? super E>>> getSensorTypes(@NotNull E entity) {
        return ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY,
            SensorType.PIGLIN_SPECIFIC_SENSOR);
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(@NotNull E entity) {
        return Brain.provider(MEMORY_TYPES, getSensorTypes(entity));
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(@NotNull E entity, List<MemoryModuleType<?>> extraMemory) {
        List<MemoryModuleType<?>> memoryTypes = new ArrayList<>(MEMORY_TYPES);
        memoryTypes.addAll(extraMemory);

        return Brain.provider(memoryTypes, getSensorTypes(entity));
    }

    @NotNull
    public static <E extends Mob> Brain<E> refreshBrain(@NotNull E pet, @NotNull Brain<E> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            PetCoreBehaviors.lookAtTarget(),
            PetCoreBehaviors.moveToTarget(),
            PetCoreBehaviors.swim(),
            PetFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            new RunOne<>(ImmutableList.of(Pair.of(PetIdleBehaviors.lookAtOwner(), 1))),
            PetIdleBehaviors.followOwner(),
            PetFightBehaviors.autoTargetAndAttack())
        );


        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            PetFightBehaviors.stopAttackIfTargetInvalid(pet),
            PetFightBehaviors.reachTargetWhenOutOfRange(),
            PetFightBehaviors.meleeAttack())
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    // Simulate natural mob damage, but without the actual damager to prevent certain AI goals from triggering.
    public static boolean hurt(@NotNull LivingEntity pet, @NotNull DamageSource original, @NotNull Function<DamageSource, Boolean> hurtServer) {
        DamageSource fixed = new DamageSource(original.typeHolder(), original.getDirectEntity(), null, original.sourcePositionRaw());

        if (original.getEntity() instanceof ServerPlayer player && pet instanceof PetEntity petEntity && petEntity.getOwnerId().equals(player.getUUID())) {
            return false;
        }

        boolean flag = hurtServer.apply(fixed);

        if (original.getEntity() instanceof LivingEntity mob) {
            pet.setLastHurtByMob(mob);
        }
        if (original.getEntity() instanceof ServerPlayer player) {
            pet.setLastHurtByPlayer(player, LivingEntity.PLAYER_HURT_EXPERIENCE_TIME);
        }

        return flag;
    }

    // Reimplemention of the Entity#tunderHit method to cancel lightning effects and apply damage for certain mobs like Mooshroms, Pigs.
    public static void thunderHit(@NotNull LivingEntity entity, @NotNull ServerLevel level, @NotNull LightningBolt bolt) {
        entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        org.bukkit.entity.Entity bukkitStorm = bolt.getBukkitEntity();
        if (entity.getRemainingFireTicks() == 0) {
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(bukkitStorm, bukkitEntity, 8.0F);
            Bukkit.getPluginManager().callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                entity.igniteForSeconds(entityCombustEvent.getDuration(), false);
            }
        }

        if (!entity.fireImmune()) {
            entity.hurtServer(level, entity.damageSources().lightningBolt().customEntityDamager(bolt), 5.0F);
        }
    }
}
