package su.nightexpress.combatpets.nms.mc_1_21_5.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior.PetIdleBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior.impl.PrepareRamTarget;

public class GoatPet extends Goat implements PetEntity {

    public static final  UniformInt          TIME_BETWEEN_RAMS     = UniformInt.of(100, 300);
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat();

    public GoatPet(@NotNull ServerLevel level) {
        super(EntityType.GOAT, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Goat> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Goat> refreshBrain(@NotNull Goat pet, @NotNull Brain<Goat> brain) {
        BehaviorControl<LivingEntity> cooldownTicks = new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS);

        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            PetCoreBehaviors.lookAtTarget(),
            PetCoreBehaviors.moveToTarget(),
            PetCoreBehaviors.swim(),
            cooldownTicks,
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

        brain.addActivityWithConditions(Activity.RAM,
            ImmutableList.of(
                Pair.of(0, new RamTarget(
                    goat -> TIME_BETWEEN_RAMS,
                    RAM_TARGET_CONDITIONS,
                    3.0F,
                    goat -> goat.isBaby() ? 1.0D : 2.5D,
                    goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT,
                    goat -> SoundEvents.GOAT_HORN_BREAK)
                ),
                Pair.of(1, new PrepareRamTarget<>(
                    4, 10, 1.5F, 20,
                    goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_PREPARE_RAM : SoundEvents.GOAT_PREPARE_RAM)
                )
            ),
            ImmutableSet.of(
                Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)
            )
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        brain.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, TIME_BETWEEN_RAMS.sample(this.level().random));

        this.brain = brain;
        return brain;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("goatBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        filler.push("goatActivityUpdate");
        this.updateActivity();
        filler.pop();
    }

    protected void updateActivity() {
        Brain<Goat> brain = this.getBrain();
        if (PetAI.getAttackTarget(this).isPresent()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.RAM, Activity.FIGHT));
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

//    @Override
//    public boolean hurtServer(ServerLevel worldserver, DamageSource damagesource, float damage) {
//        if (super.hurtServer(worldserver, damagesource, damage)) {
//            if (damagesource.getEntity() instanceof LivingEntity target) {
//                PetAI.setAngerTarget(this, target, false);
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    protected void ageBoundaryReached() {

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean dropHorn() {
        return false;
    }
}
