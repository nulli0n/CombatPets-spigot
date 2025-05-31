package su.nightexpress.combatpets.nms.mc_1_21_3.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetIdleBehaviors;

public class PiglinPet extends Piglin implements PetEntity {

    public PiglinPet(@NotNull ServerLevel world) {
        super(EntityType.PIGLIN, world);
        this.setImmuneToZombification(true);
        this.cannotHunt = true;
    }


    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Piglin> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Piglin> refreshBrain(@NotNull Piglin pet, @NotNull Brain<Piglin> brain) {
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
            PetFightBehaviors.meleeAttack(),
            new CrossbowAttack<>()));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("piglinBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        PetAI.updateActivity(this, this.brain);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return PetBrain.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
        //return PetBrain.hurt(this, level, fixed, damage);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public void setDancing(boolean flag) {

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        return false;
    }
}
