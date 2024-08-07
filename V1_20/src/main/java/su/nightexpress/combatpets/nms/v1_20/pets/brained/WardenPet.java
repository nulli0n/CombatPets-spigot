package su.nightexpress.combatpets.nms.v1_20.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.brain.PetAI;
import su.nightexpress.combatpets.nms.v1_20.brain.PetBrain;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetIdleBehaviors;

import javax.annotation.Nullable;

public class WardenPet extends Warden implements PetEntity {

    public WardenPet(@NotNull ServerLevel level) {
        super(EntityType.WARDEN, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Warden> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Warden> refreshBrain(@NotNull Warden pet, @NotNull Brain<Warden> brain) {
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
            PetFightBehaviors.meleeAttack(20)));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        V1_20.hurtArmor(this, source, amount);
    }

    @Override
    public void increaseAngerAt(@Nullable Entity entity) {
        if (entity == null || !this.getHolder().getOwner().getUniqueId().equals(entity.getUUID())) {
            super.increaseAngerAt(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.getHolder().getOwner().getUniqueId().equals(entity.getUUID())) {
            if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
                this.increaseAngerAt(entity);
                WardenAi.setDisturbanceLocation(this, entity.blockPosition());
            }
        }
        entity.push(this);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        boolean flag = PetBrain.hurt(this, damagesource, damage);
        if (!this.level().isClientSide) {
            Entity entity = damagesource.getEntity();
            if (entity != null && !this.getHolder().getOwner().getUniqueId().equals(entity.getUUID())) {
                this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
                if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity target) {
                    if (!damagesource.isIndirect() || this.closerThan(target, 5.0)) {
                        this.setAttackTarget(target);
                    }
                }
            }
        }

        return flag;
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel worldserver = (ServerLevel)this.level();
        ProfilerFiller filler = worldserver.getProfiler();

        if (filler != null) {
            filler.push("wardenBrain");
        }
        this.getBrain().tick(worldserver, this);
        if (filler != null) {
            filler.pop();
        }

        this.updateActivity();
    }

    protected void updateActivity() {
        Brain<Warden> brain = this.getBrain();
        if (PetAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }
}
