package su.nightexpress.combatpets.nms.v1_19_R1.brain;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
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
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

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

    public static Player getOwner(@NotNull Mob pet) {
        Optional<UUID> optional = pet.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
        return optional.map(id -> pet.level.getPlayerByUUID(id)).orElse(null);
    }

    public static void setOwnerMemory(@NotNull Mob pet, @NotNull ActivePet holder) {
        pet.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, holder.getOwner().getUniqueId());
    }

    private static final Method ACTUALLY_HURT      = Reflex.getMethod(LivingEntity.class, "damageEntity0", DamageSource.class, Float.TYPE);
    private static final Field  NO_ACTION_TIME     = Reflex.getField(LivingEntity.class, "bf");
    private static final Field  LAST_DAMAGE_TIME   = Reflex.getField(LivingEntity.class, "ch");
    private static final Field  LAST_DAMAGE_SOURCE = Reflex.getField(LivingEntity.class, "cg");

    /*public static boolean hurt(LivingEntity pet, DamageSource damagesource, float damage) {
        if (ACTUALLY_HURT == null) return false;
        if (pet.isInvulnerableTo(damagesource)) return false;
        if (pet.level.isClientSide) return false;
        if (pet.isRemoved() || !pet.isAlive() || pet.getHealth() <= 0.0F) return false;
        if (damagesource.is(DamageTypeTags.IS_FIRE) && pet.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;

        if (NO_ACTION_TIME != null) {
            try {
                NO_ACTION_TIME.trySetAccessible();
                NO_ACTION_TIME.set(pet, 0);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        boolean blocked = damage > 0.0F && pet.isDamageSourceBlocked(damagesource);
        pet.walkAnimation.setSpeed(1.5F);
        boolean punch = true;
        if ((float) pet.invulnerableTime > (float) pet.invulnerableDuration / 2.0F) {
            if (damage <= pet.lastHurt) {
                return false;
            }
            Object val = Reflex.invokeMethod(ACTUALLY_HURT, pet, damagesource, damage - pet.lastHurt);
            if (val != null && !(boolean) val) {
                return false;
            }
            pet.lastHurt = damage;
            punch = false;
        }
        else {
            Object val = Reflex.invokeMethod(ACTUALLY_HURT, pet, damagesource, damage);
            if (val != null && !(boolean) val) {
                return false;
            }
            pet.lastHurt = damage;
            pet.invulnerableTime = pet.invulnerableDuration;
            pet.hurtDuration = 10;
            pet.hurtTime = pet.hurtDuration;
        }

        pet.hurtDir = 0.0F;
        Entity damager = damagesource.getEntity();
        if (damager != null) {
            if (damager instanceof LivingEntity && !damagesource.isNoAggro()) {
                pet.setLastHurtByMob((LivingEntity) damager);
            }

            int ticksHas = pet.tickCount;
            if (damager instanceof Player player) {
                pet.tickCount = 100;
                pet.setLastHurtByPlayer(player);
                pet.tickCount = ticksHas;
            }
            else if (damager instanceof Wolf entitywolf) {
                if (entitywolf.isTame()) {
                    LivingEntity entityliving = entitywolf.getOwner();
                    if (entityliving instanceof Player player) {
                        pet.tickCount = 100;
                        pet.setLastHurtByPlayer(player);
                        pet.tickCount = ticksHas;
                    }
                    else pet.lastHurtByPlayer = null;
                }
            }
        }

        if (punch) {
            if (blocked) {
                pet.level.broadcastEntityEvent(pet, (byte) 29);
            }
            else if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).isThorns()) {
                pet.level.broadcastEntityEvent(pet, (byte) 33);
            }
            else {
                byte b0 = 2;
                if (damagesource == DamageSource.DROWN) b0 = 36;
                else if (damagesource.isFire()) b0 = 37;
                else if (damagesource == DamageSource.SWEET_BERRY_BUSH) b0 = 44;
                else if (damagesource == DamageSource.FREEZE) b0 = 57;
                pet.level.broadcastEntityEvent(pet, b0);
            }

            if (damagesource != DamageSource.DROWN && (!blocked || damage > 0.0F)) {
                pet.hurtMarked = true;
            }

            if (damager != null) {
                double d0 = damager.getX() - pet.getX();

                double d1;
                for (d1 = damager.getZ() - pet.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                    d0 = (Math.random() - Math.random()) * 0.01D;
                }

                pet.hurtDir = (float) (Mth.atan2(d1, d0) * 57.2957763671875D - (double) pet.getYRot());
                pet.knockback(0.4000000059604645D, d0, d1);
            }
            else {
                pet.hurtDir = (float) ((int) (Math.random() * 2.0D) * 180);
            }
        }

        if (pet.isDeadOrDying()) {

            pet.die(damagesource);

        }
        else if (punch) {
            //pet.playHurtSound(damagesource);
        }

        boolean damaged = !blocked || damage > 0.0F;
        if (damaged) {
            if (LAST_DAMAGE_TIME != null && LAST_DAMAGE_SOURCE != null) {
                try {
                    LAST_DAMAGE_SOURCE.trySetAccessible();
                    LAST_DAMAGE_SOURCE.set(pet, damagesource);

                    LAST_DAMAGE_TIME.trySetAccessible();
                    LAST_DAMAGE_TIME.set(pet, pet.level.getGameTime());
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (damager instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) damager, pet, damagesource, damage, damage, blocked);
        }

        if (damaged) {
            if (damager instanceof LivingEntity target) {
                PetAI.setAngerTarget((Mob) pet, target, false);
                return true;
            }
        }

        return damaged;
    }*/

    public static boolean hurt(LivingEntity pet, DamageSource damagesource, float damage) {
        if (ACTUALLY_HURT == null) return false;
        if (pet.isInvulnerableTo(damagesource)) return false;
        if (pet.level.isClientSide) return false;
        if (pet.isAlive()) {
            if (damagesource.is(DamageTypeTags.IS_FIRE) && pet.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                return false;
            }
            else {
                if (pet.isSleeping() && !pet.level.isClientSide) {
                    pet.stopSleeping();
                }

                if (NO_ACTION_TIME != null) {
                    try {
                        NO_ACTION_TIME.trySetAccessible();
                        NO_ACTION_TIME.set(pet, 0);
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                float f1 = damage;
                boolean blocked = damage > 0.0F && pet.isDamageSourceBlocked(damagesource);
                float f2 = 0.0F;
                if (damagesource.is(DamageTypeTags.IS_FREEZING) && pet.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    damage *= 5.0F;
                }

                pet.walkAnimation.setSpeed(1.5F);
                boolean punch = true;
                if ((float)pet.invulnerableTime > (float)pet.invulnerableDuration / 2.0F && !damagesource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                    if (damage <= pet.lastHurt) {
                        return false;
                    }

                    Object val = Reflex.invokeMethod(ACTUALLY_HURT, pet, damagesource, damage - pet.lastHurt);
                    if (val != null && !(boolean) val) {
                        return false;
                    }

                    pet.lastHurt = damage;
                    punch = false;
                } else {
                    Object val = Reflex.invokeMethod(ACTUALLY_HURT, pet, damagesource, damage);
                    if (val != null && !(boolean) val) {
                        return false;
                    }
                    pet.lastHurt = damage;
                    pet.invulnerableTime = pet.invulnerableDuration;
                    pet.hurtDuration = 10;
                    pet.hurtTime = pet.hurtDuration;
                }

                Entity damager = damagesource.getEntity();
                if (damager != null) {
                    if (damager instanceof LivingEntity entityliving1) {
                        if (!damagesource.is(DamageTypeTags.NO_ANGER)) {
                            pet.setLastHurtByMob(entityliving1);
                        }
                    }

                    int ticksHas = pet.tickCount;
                    if (damager instanceof Player player) {
                        pet.tickCount = 100;
                        pet.setLastHurtByPlayer(player);
                        pet.tickCount = ticksHas;
                    }
                    else if (damager instanceof Wolf entitywolf) {
                        if (entitywolf.isTame()) {
                            LivingEntity entityliving = entitywolf.getOwner();
                            if (entityliving instanceof Player player) {
                                pet.tickCount = 100;
                                pet.setLastHurtByPlayer(player);
                                pet.tickCount = ticksHas;
                            }
                            else pet.lastHurtByPlayer = null;
                        }
                    }
                }

                if (punch) {
                    if (blocked) {
                        pet.level.broadcastEntityEvent(pet, (byte)29);
                    } else {
                        pet.level.broadcastDamageEvent(pet, damagesource);
                    }

                    if (!damagesource.is(DamageTypeTags.NO_IMPACT) && (!blocked || damage > 0.0F)) {
                        pet.hurtMarked = true;
                    }

                    if (damager != null && !damagesource.is(DamageTypeTags.IS_EXPLOSION)) {
                        double d0 = damager.getX() - pet.getX();

                        double d1;
                        for(d1 = damager.getZ() - pet.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        pet.knockback(0.4000000059604645D, d0, d1);
                        if (!blocked) {
                            pet.indicateDamage(d0, d1);
                        }
                    }
                }

                if (pet.isDeadOrDying()) {
                    /*if (!pet.checkTotemDeathProtection(damagesource)) {
                        SoundEvent soundeffect = pet.getDeathSound();
                        if (punch && soundeffect != null) {
                            pet.playSound(soundeffect, pet.getSoundVolume(), pet.getVoicePitch());
                        }
                    }*/
                    pet.die(damagesource);
                }
                else if (punch) {
                    //pet.playHurtSound(damagesource);
                }

                boolean damaged = !blocked || damage > 0.0F;
                if (damaged) {
                    if (LAST_DAMAGE_TIME != null && LAST_DAMAGE_SOURCE != null) {
                        try {
                            LAST_DAMAGE_SOURCE.trySetAccessible();
                            LAST_DAMAGE_SOURCE.set(pet, damagesource);

                            LAST_DAMAGE_TIME.trySetAccessible();
                            LAST_DAMAGE_TIME.set(pet, pet.level.getGameTime());
                        }
                        catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (damager instanceof ServerPlayer) {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)damager, pet, damagesource, f1, damage, blocked);
                }

                return damaged;
            }
        } else {
            return false;
        }
    }
}
