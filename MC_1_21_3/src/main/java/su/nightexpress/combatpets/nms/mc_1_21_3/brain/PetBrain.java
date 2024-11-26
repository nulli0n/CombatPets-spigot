package su.nightexpress.combatpets.nms.mc_1_21_3.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetIdleBehaviors;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    private static final Method HANDLE_ENTITY_DAMAGE = Reflex.getMethod(LivingEntity.class, "handleEntityDamage", DamageSource.class, Float.TYPE, Float.TYPE);
    //private static final Method ACTUALLY_HURT        = Reflex.getMethod(LivingEntity.class, "actuallyHurt", DamageSource.class, Float.TYPE, EntityDamageEvent.class);
    private static final Method ACTUALLY_HURT        = Reflex.getMethod(LivingEntity.class, "actuallyHurt", ServerLevel.class, DamageSource.class, Float.TYPE, EntityDamageEvent.class);
    private static final Field  NO_ACTION_TIME       = Reflex.getField(LivingEntity.class, "bf");
    private static final Field  LAST_DAMAGE_SOURCE   = Reflex.getField(LivingEntity.class, "cj");
    private static final Field  LAST_DAMAGE_TIME     = Reflex.getField(LivingEntity.class, "ck");

    /*
        Reimplemention of the LivingEntity#hurtServer method to 'override' AI effects and calls for 'brained' mobs.
    */
    public static boolean hurt(LivingEntity pet, ServerLevel level, DamageSource source, float amount) {
        //Method hurtMethod = ACTUALLY_HURT == null ? ACTUALLY_HURT_FIX : ACTUALLY_HURT;

        if (ACTUALLY_HURT == null) return false;
        if (HANDLE_ENTITY_DAMAGE == null) return false;
        if (level.isClientSide) return false;
        if (pet.isInvulnerableTo(level, source)) return false;
        if (pet.isRemoved() || !pet.isAlive()) return false;
        if (source.is(DamageTypeTags.IS_FIRE) && pet.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;

        if (pet.isSleeping()) pet.stopSleeping();

        if (NO_ACTION_TIME != null) {
            try {
                NO_ACTION_TIME.trySetAccessible();
                NO_ACTION_TIME.set(pet, 0);
            }
            catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
        if (amount < 0F) {
            amount = 0F;
        }

        float originDamage = amount;
        boolean attackBlocked = amount > 0.0F && pet.isDamageSourceBlocked(source);
        EntityDamageEvent event;

        pet.walkAnimation.setSpeed(1.5F);
        if (Float.isNaN(amount) || Float.isInfinite(amount)) {
            amount = Float.MAX_VALUE;
        }

        boolean punch = true;

        if ((float) pet.invulnerableTime > (float) pet.invulnerableDuration / 2.0F && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
            if (amount <= pet.lastHurt) {
                return false;
            }

            event = (EntityDamageEvent) Reflex.invokeMethod(HANDLE_ENTITY_DAMAGE, pet, source, amount, pet.lastHurt);
            amount = computeAmountFromEntityDamageEvent(event);

            Object hurtResult = Reflex.invokeMethod(ACTUALLY_HURT, pet, level, source, (float) event.getFinalDamage(), event);
            if (hurtResult != null && !(boolean) hurtResult) {
                return false;
            }

            pet.lastHurt = amount;
            punch = false;
        }
        else {
            event = (EntityDamageEvent) Reflex.invokeMethod(HANDLE_ENTITY_DAMAGE, pet, source, amount, 0F);
            amount = computeAmountFromEntityDamageEvent(event);

            Object hurtResult = Reflex.invokeMethod(ACTUALLY_HURT, pet, level, source, (float) event.getFinalDamage(), event);
            if (hurtResult != null && !(boolean) hurtResult) {
                return false;
            }

            pet.lastHurt = amount;
            pet.invulnerableTime = pet.invulnerableDuration;
            pet.hurtDuration = 10;
            pet.hurtTime = pet.hurtDuration;
        }

        Entity damager = source.getEntity();
        if (damager != null) {
            if (damager instanceof LivingEntity livingEntity) {
                if (!source.is(DamageTypeTags.NO_ANGER) && (!source.is(DamageTypes.WIND_CHARGE) || !pet.getType().is(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE))) {
                    pet.setLastHurtByMob(livingEntity);
                }
            }

            int ticksHas = pet.tickCount;
            if (damager instanceof Player player) {
                pet.tickCount = 100;
                pet.setLastHurtByPlayer(player);
                pet.tickCount = ticksHas;
            }
            else if (damager instanceof Wolf wolf) {
                if (wolf.isTame()) {
                    LivingEntity wolfOwner = wolf.getOwner();
                    if (wolfOwner instanceof Player playerOwner) {
                        pet.tickCount = 100;
                        pet.setLastHurtByPlayer(playerOwner);
                        pet.tickCount = ticksHas;
                    }
                    else pet.lastHurtByPlayer = null;
                }
            }
        }

        if (punch) {
            if (attackBlocked) {
                level.broadcastEntityEvent(pet, (byte) 29);
            }
            else {
                level.broadcastDamageEvent(pet, source);
            }

            if (!source.is(DamageTypeTags.NO_IMPACT) && !attackBlocked) {
                pet.hurtMarked = true;
            }

            if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
                double x = 0.0;
                double z = 0.0;
                Entity directDamager = source.getDirectEntity();
                if (directDamager instanceof Projectile projectile) {
                    DoubleDoubleImmutablePair doubledoubleimmutablepair = projectile.calculateHorizontalHurtKnockbackDirection(pet, source);
                    x = -doubledoubleimmutablepair.leftDouble();
                    z = -doubledoubleimmutablepair.rightDouble();
                }
                else if (source.getSourcePosition() != null) {
                    x = source.getSourcePosition().x() - pet.getX();
                    z = source.getSourcePosition().z() - pet.getZ();
                }

                //pet.knockback(0.4, x, z, damager, damager == null ? EntityKnockbackEvent.KnockbackCause.DAMAGE : EntityKnockbackEvent.KnockbackCause.ENTITY_ATTACK);
                pet.knockback(0.4, x, z);
                if (!attackBlocked) {
                    pet.indicateDamage(x, z);
                }
            }
        }

        if (pet.isDeadOrDying()) {
            pet.die(source);
        }
//        else if (punch) {
//            pet.playHurtSound(damagesource);
//        }

        boolean hurtResult = !attackBlocked;
        if (hurtResult) {
            if (LAST_DAMAGE_TIME != null && LAST_DAMAGE_SOURCE != null) {
                try {
                    LAST_DAMAGE_SOURCE.trySetAccessible();
                    LAST_DAMAGE_SOURCE.set(pet, source);

                    LAST_DAMAGE_TIME.trySetAccessible();
                    LAST_DAMAGE_TIME.set(pet, level.getGameTime());
                }
                catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }

            for (MobEffectInstance mobeffect : pet.getActiveEffects()) {
                mobeffect.onMobHurt(level, pet, source, amount);
            }
        }

        if (damager instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) damager, pet, source, originDamage, amount, attackBlocked);
        }

        return hurtResult;
    }

    @SuppressWarnings("deprecation")
    private static float computeAmountFromEntityDamageEvent(EntityDamageEvent event) {
        float amount = 0.0f;
        amount += (float)event.getDamage(EntityDamageEvent.DamageModifier.BASE);
        amount += (float)event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        amount += (float)event.getDamage(EntityDamageEvent.DamageModifier.FREEZING);
        amount += (float)event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT);

        return amount;
    }

    /*
        Reimplemention of the Entity#tunderHit method to cancel lightning effects and apply damage for certain mobs like Mooshroms, Pigs.
    */
    public static void thunderHit(LivingEntity entity, ServerLevel worldserver, LightningBolt entitylightning) {
        entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        org.bukkit.entity.Entity bukkitStorm = entitylightning.getBukkitEntity();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (entity.getRemainingFireTicks() == 0) {
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(bukkitStorm, bukkitEntity, 8.0F);
            pluginManager.callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                entity.igniteForSeconds(entityCombustEvent.getDuration(), false);
            }
        }

        if (bukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging)bukkitEntity, bukkitStorm);
            pluginManager.callEvent(hangingEvent);
            if (hangingEvent.isCancelled()) {
                return;
            }
        }

        if (!entity.fireImmune()) {
            entity.hurtServer(worldserver, entity.damageSources().lightningBolt().customEntityDamager(entitylightning), 5.0F);
        }
    }
}
