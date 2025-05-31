package su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R4.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;

import java.util.Optional;
import java.util.function.Function;

public class PetFightBehaviors {

    @NotNull
    public static BehaviorControl<Mob> autoTargetAndAttack() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(
                builder.absent(MemoryModuleType.ATTACK_TARGET),
                builder.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
            ).apply(builder, (memAttackTarget, memCantReach) -> {
                return (world, mob, l) -> {
                    Optional<LivingEntity> currentTarget = PetAI.getAttackTarget(mob);
                    if (currentTarget.isPresent()) return false;
                    if (!(mob instanceof PetEntity petEntity)) return false;

                    ActivePet activePet = petEntity.getHolder();

                    LivingEntity target = PetAI.findTarget(mob, activePet);
                    if (target == null || !mob.canAttack(target)) return false;

                    EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(mob, target, EntityTargetEvent.TargetReason.CUSTOM);
                    if (event.isCancelled() || event.getTarget() == null) {
                        memAttackTarget.erase();
                        return true;
                    }

                    target = ((CraftLivingEntity) event.getTarget()).getHandle();
                    return PetAI.setAngerTarget(mob, target, false);
                };
            });
        });
    }

    @NotNull
    public static OneShot<Mob> meleeAttack() {
        return BehaviorBuilder.create((mobInstance) -> {
            return mobInstance.group(
                mobInstance.registered(MemoryModuleType.LOOK_TARGET),
                mobInstance.present(MemoryModuleType.ATTACK_TARGET),
                mobInstance.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                mobInstance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            ).apply(mobInstance, (lookTarget, attackTarget, attackCooldown, nearest) -> {
                return (world, pet, i) -> {
                    LivingEntity target = mobInstance.get(attackTarget);
                    if (isHoldingUsableProjectileWeapon(pet)) return false;
                    if (!pet.isWithinMeleeAttackRange(target)) return false;
                    if (!mobInstance.get(nearest).contains(target)) return false;

                    AttributeInstance instance = pet.getAttribute(Attributes.ATTACK_SPEED);
                    double atkSpeed = instance == null ? 0.5 : instance.getBaseValue();
                    int ticks = (int) Math.max(1, 20D / atkSpeed);

                    lookTarget.set(new EntityTracker(target, true));
                    pet.swing(InteractionHand.MAIN_HAND);
                    pet.doHurtTarget(world, target);
                    pet.lookAt(target, 30f, 30f);
                    if (pet instanceof PetEntity petEntity) {
                        petEntity.getHolder().doExhaust(ExhaustReason.COMBAT);
                    }
                    attackCooldown.setWithExpiry(true, ticks);
                    return true;
                };
            });
        });
    }

    private static boolean isHoldingUsableProjectileWeapon(Mob pet) {
        return pet.isHolding((itemStack) -> {
            Item item = itemStack.getItem();
            return item instanceof ProjectileWeaponItem pj && pet.canFireProjectileWeapon(pj);
        });
    }

    @NotNull
    public static BehaviorControl<Mob> stopAttackIfTargetInvalid(@NotNull Mob pet) {
        return StopAttackingIfTargetInvalid.create((level, target) -> {
            Optional<LivingEntity> optional = PetAI.getAttackTarget(pet);
            if (optional.isEmpty() || !target.equals(optional.get())) return false;

            return !Sensor.isEntityAttackableIgnoringLineOfSight(level, pet, optional.get());
        });
    }

    @NotNull
    public static BehaviorControl<LivingEntity> stopAngryIfTargetDead() {
        return StopBeingAngryIfTargetDead.create();
    }

    @NotNull
    public static BehaviorControl<Mob> reachTargetWhenOutOfRange() {
        Function<LivingEntity, Float> speedFunc = mob -> 1F;
        return BehaviorBuilder.create((mobInstance) -> {
            return mobInstance.group(
                mobInstance.registered(MemoryModuleType.WALK_TARGET),
                mobInstance.registered(MemoryModuleType.LOOK_TARGET),
                mobInstance.present(MemoryModuleType.ATTACK_TARGET)).apply(mobInstance, (walkTarget, lookTarget, attackTarget) -> {
                return (level, mob, num) -> {
                    LivingEntity target = mobInstance.get(attackTarget);
                    if (BehaviorUtils.isWithinAttackRange(mob, target, 1)) {
                        walkTarget.erase();
                    }
                    else {
                        lookTarget.set(new EntityTracker(target, true));
                        walkTarget.set(new WalkTarget(new EntityTracker(target, false), speedFunc.apply(mob), 2));
                    }
                    return true;
                };
            });
        });
    }
}
