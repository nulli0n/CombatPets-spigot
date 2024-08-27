package su.nightexpress.combatpets.nms.mc_1_21.brain;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
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

    private static final Method HANDLE_ENTITY_DAMAGE = Reflex.getMethod(LivingEntity.class, "handleEntityDamage", DamageSource.class, Float.TYPE);
    private static final Method ACTUALLY_HURT        = Reflex.getMethod(LivingEntity.class, "actuallyHurt", DamageSource.class, Float.TYPE, EntityDamageEvent.class);
    private static final Method ACTUALLY_HURT_FIX    = Reflex.getMethod(LivingEntity.class, "damageEntity0", DamageSource.class, Float.TYPE, EntityDamageEvent.class);
    private static final Field  NO_ACTION_TIME       = Reflex.getField(LivingEntity.class, "bf");
    private static final Field  LAST_DAMAGE_TIME     = Reflex.getField(LivingEntity.class, "cm");
    private static final Field  LAST_DAMAGE_SOURCE   = Reflex.getField(LivingEntity.class, "cl");

    @SuppressWarnings("deprecation")
    public static boolean hurt(LivingEntity pet, DamageSource damagesource, float damage) {
        Method hurtMethod = ACTUALLY_HURT == null ? ACTUALLY_HURT_FIX : ACTUALLY_HURT;

        if (hurtMethod == null) return false;
        if (HANDLE_ENTITY_DAMAGE == null) return false;
        if (pet.isInvulnerableTo(damagesource)) return false;
        if (pet.level().isClientSide) return false;
        if (pet.isRemoved() || !pet.isAlive()) return false;
        if (damagesource.is(DamageTypeTags.IS_FIRE) && pet.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;
        if (pet.isSleeping() && !pet.level().isClientSide) {
            pet.stopSleeping();
        }

        if (NO_ACTION_TIME != null) {
            try {
                NO_ACTION_TIME.trySetAccessible();
                NO_ACTION_TIME.set(pet, 0);
            }
            catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        float f1 = damage;
        boolean attackBlocked = damage > 0.0F && pet.isDamageSourceBlocked(damagesource);
        float f2 = 0.0F;
        EntityDamageEvent event = (EntityDamageEvent) Reflex.invokeMethod(HANDLE_ENTITY_DAMAGE, pet, damagesource, damage);
        if (event == null) return false;


        damage = 0.0F;
        damage += (float) event.getDamage(EntityDamageEvent.DamageModifier.BASE);
        damage += (float) event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        damage += (float) event.getDamage(EntityDamageEvent.DamageModifier.FREEZING);
        damage += (float) event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT);
        pet.walkAnimation.setSpeed(1.5F);
        boolean punch = true;

        if ((float) pet.invulnerableTime > (float) pet.invulnerableDuration / 2.0F && !damagesource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
            if (damage <= pet.lastHurt) {
                return false;
            }

            Object hurtResult = Reflex.invokeMethod(hurtMethod, pet, damagesource, (float) event.getFinalDamage() - pet.lastHurt, event);
            if (hurtResult != null && !(boolean) hurtResult) {
                return false;
            }

            pet.lastHurt = damage;
            punch = false;
        }
        else {
            Object hurtResult = Reflex.invokeMethod(hurtMethod, pet, damagesource, (float) event.getFinalDamage() - pet.lastHurt, event);
            if (hurtResult != null && !(boolean) hurtResult) {
                return false;
            }

            pet.lastHurt = damage;
            pet.invulnerableTime = pet.invulnerableDuration;
            pet.hurtDuration = 10;
            pet.hurtTime = pet.hurtDuration;
        }

        Entity damager = damagesource.getEntity();
        if (damager != null) {
            if (damager instanceof LivingEntity livingEntity) {
                if (!damagesource.is(DamageTypeTags.NO_ANGER) && (!damagesource.is(DamageTypes.WIND_CHARGE) || !pet.getType().is(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE))) {
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
                pet.level().broadcastEntityEvent(pet, (byte) 29);
            }
            else {
                pet.level().broadcastDamageEvent(pet, damagesource);
            }

            if (!damagesource.is(DamageTypeTags.NO_IMPACT) && !attackBlocked) {
                pet.hurtMarked = true;
            }

            if (!damagesource.is(DamageTypeTags.NO_KNOCKBACK)) {
                double x = 0.0;
                double z = 0.0;
                Entity directDamager = damagesource.getDirectEntity();
                if (directDamager instanceof Projectile projectile) {
                    DoubleDoubleImmutablePair doubledoubleimmutablepair = projectile.calculateHorizontalHurtKnockbackDirection(pet, damagesource);
                    x = -doubledoubleimmutablepair.leftDouble();
                    z = -doubledoubleimmutablepair.rightDouble();
                }
                else if (damagesource.getSourcePosition() != null) {
                    x = damagesource.getSourcePosition().x() - pet.getX();
                    z = damagesource.getSourcePosition().z() - pet.getZ();
                }

                //pet.knockback(0.4, x, z, damager, damager == null ? EntityKnockbackEvent.KnockbackCause.DAMAGE : EntityKnockbackEvent.KnockbackCause.ENTITY_ATTACK);
                pet.knockback(0.4, x, z);
                if (!attackBlocked) {
                    pet.indicateDamage(x, z);
                }
            }
        }

        if (pet.isDeadOrDying()) {
            pet.die(damagesource);
        }
//        else if (punch) {
//            pet.playHurtSound(damagesource);
//        }

        boolean hurtResult = !attackBlocked;
        if (hurtResult) {
            if (LAST_DAMAGE_TIME != null && LAST_DAMAGE_SOURCE != null) {
                try {
                    LAST_DAMAGE_SOURCE.trySetAccessible();
                    LAST_DAMAGE_SOURCE.set(pet, damagesource);

                    LAST_DAMAGE_TIME.trySetAccessible();
                    LAST_DAMAGE_TIME.set(pet, pet.level().getGameTime());
                }
                catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }

            for (MobEffectInstance mobeffect : pet.getActiveEffects()) {
                mobeffect.onMobHurt(pet, damagesource, damage);
            }
        }

        if (damager instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) damager, pet, damagesource, f1, damage, attackBlocked);
        }

        return hurtResult;
    }
}
