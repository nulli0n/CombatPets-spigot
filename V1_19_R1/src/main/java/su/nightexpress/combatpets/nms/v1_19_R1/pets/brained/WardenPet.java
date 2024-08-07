package su.nightexpress.combatpets.nms.v1_19_R1.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetAI;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetBrain;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetIdleBehaviors;

import java.util.UUID;

public class WardenPet extends Warden implements PetEntity {

    public WardenPet(@NotNull ServerLevel level) {
        super(EntityType.WARDEN, level);
    }

    @Override
    public void setGoals() {
        //this.refreshBrain((ServerLevel) this.level());
        //this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        //this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));

        PetBrain.setOwnerMemory(this, this.getHolder());
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.getBukkitEntity().getUniqueId();
    }

    @Override
    protected Brain.Provider<Warden> brainProvider() {
        return PetBrain.brainProvider(this);
    }

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
        //behaviorcontroller.stopAll(worldserver, this);
        this.brain = brain;//.copyWithoutBehaviors();
        return brain;
    }

    /*public void refreshBrain(ServerLevel worldserver) {

        Behavior<Warden> behavior = new Behavior<>(ImmutableMap.of(MemoryModuleType.DIG_COOLDOWN, MemoryStatus.REGISTERED)) {
            protected void start(ServerLevel var0, Warden var1, long var2) {

            }
        };

        Brain<Warden> brain = this.getBrain();
        BehaviorControl<Warden> sonicBoom = new SonicBoom();
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), (Behavior<? super Mob>) SetWardenLookTarget.create(), new LookAtTargetSink(45, 90), new MoveToTargetSink()));
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.EMERGE, 5, ImmutableList.of(new Emerging<>(134)), MemoryModuleType.IS_EMERGING);
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(SetRoarTarget.create(Warden::getEntityAngryAt), TryToSniff.create(), new RunOne<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(RandomStroll.stroll(0.5F), 2), Pair.of(new DoNothing(30, 60), 1)))));
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(SetRoarTarget.create(Warden::getEntityAngryAt), GoToTargetLocation.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7F)), MemoryModuleType.DISTURBANCE_LOCATION);
        //brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(new SetRoarTarget<>(Warden::getEntityAngryAt), new Sniffing<>(84)), MemoryModuleType.IS_SNIFFING);
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.ROAR, 10, ImmutableList.of(new Roar()), MemoryModuleType.ROAR_TARGET);
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(behavior, StopAttackingIfTargetInvalid.create((var1x) -> {
            return !this.getAngerLevel().isAngry() || !this.canTargetEntity(var1x);
        }, WardenPet::onTargetInvalid, false), SetEntityLookTarget.create((var1x) -> {
            return isTarget(this, var1x);
        }, (float) this.getAttributeValue(Attributes.FOLLOW_RANGE)), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F), sonicBoom, MeleeAttack.create(18)), MemoryModuleType.ATTACK_TARGET);


        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
    }*/

    @Override
    protected void customServerAiStep() {
        ServerLevel worldserver = (ServerLevel)this.level;
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

    private static boolean isTarget(Warden var0, LivingEntity var1) {
        return var0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((var1x) -> {
            return var1x == var1;
        }).isPresent();
    }

    private static void onTargetInvalid(Warden var0, LivingEntity var1) {
        if (!var0.canTargetEntity(var1)) {
            var0.clearAnger(var1);
        }
    }
}
