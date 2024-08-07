package su.nightexpress.combatpets.nms.v1_20.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.brain.PetAI;
import su.nightexpress.combatpets.nms.v1_20.brain.PetBrain;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.PetIdleBehaviors;
import su.nightexpress.combatpets.nms.v1_20.brain.behavior.impl.ShootTongue;

public class FrogPet extends Frog implements PetEntity {

    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);

    public FrogPet(@NotNull ServerLevel world) {
        super(EntityType.FROG, world);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Frog> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Frog> refreshBrain(@NotNull Frog pet, @NotNull Brain<Frog> brain) {
        BehaviorControl<LivingEntity> cooldownTicks = new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);

        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            PetCoreBehaviors.lookAtTarget(),
            PetCoreBehaviors.moveToTarget(),
            //PetCoreBehaviors.swim(), swims good enough without this behavior
            cooldownTicks,
            PetFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            new RunOne<>(ImmutableList.of(Pair.of(PetIdleBehaviors.lookAtOwner(), 1))),
            PetIdleBehaviors.followOwner(),
            new RunOne<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableList.of(Pair.of(new Croak(), 3))
            ),
            PetFightBehaviors.autoTargetAndAttack())
        );


        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            PetFightBehaviors.stopAttackIfTargetInvalid(pet),
            new ShootTongue())
        );

        brain.addActivityWithConditions(Activity.SWIM, ImmutableList.of(
            Pair.of(0, PetIdleBehaviors.lookAtOwner()),
            Pair.of(2, PetFightBehaviors.autoTargetAndAttack()),
            Pair.of(5, new GateBehavior<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableSet.of(),
                GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL,
                ImmutableList.of(
                    Pair.of(RandomStroll.swim(0.75F), 1),
                    Pair.of(PetIdleBehaviors.followOwner(), 1),
                    Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                    Pair.of(BehaviorBuilder.triggerIf(Entity::isInWaterOrBubble), 5)))
            )),
            ImmutableSet.of()
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        brain.setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(this.random));

        this.brain = brain;
        return brain;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData groupData,
                                        @Nullable CompoundTag tag) {
        return groupData;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateActivity();
    }

    protected void updateActivity() {
        Brain<Frog> brain = this.getBrain();
        if (PetAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT));
        }
        else if (this.isInWaterOrBubble()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.SWIM));
        }
        else {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        if (super.hurt(damagesource, damage)) {
            if (damagesource.getEntity() instanceof LivingEntity target) {
                PetAI.setAngerTarget(this, target, false);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        V1_20.hurtArmor(this, source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }
}
