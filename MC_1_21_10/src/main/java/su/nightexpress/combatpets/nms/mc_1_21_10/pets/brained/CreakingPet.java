package su.nightexpress.combatpets.nms.mc_1_21_10.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.behavior.PetIdleBehaviors;
import su.nightexpress.nightcore.util.Lists;

public class CreakingPet extends Creaking implements PetEntity {

    public CreakingPet(@NotNull ServerLevel world) {
        super(EntityType.CREAKING, world);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Creaking> brainProvider() {
        return PetBrain.brainProvider(this,
            Lists.newList(
                MemoryModuleType.BREEZE_JUMP_COOLDOWN,
                MemoryModuleType.BREEZE_JUMP_INHALING,
                MemoryModuleType.BREEZE_JUMP_TARGET,
                MemoryModuleType.BREEZE_LEAVING_WATER,
                MemoryModuleType.BREEZE_SHOOT,
                MemoryModuleType.BREEZE_SHOOT_CHARGING,
                MemoryModuleType.BREEZE_SHOOT_COOLDOWN,
                MemoryModuleType.BREEZE_SHOOT_RECOVERING
            )
        );
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Creaking> refreshBrain(@NotNull Creaking pet, @NotNull Brain<Creaking> brain) {
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
            PetFightBehaviors.meleeAttack()
        ));

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
        filler.push("creakingBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        filler.pop();
        PetAI.updateActivity(this, this.brain);
    }

//    @Override
//    public boolean hurtServer(ServerLevel worldserver, DamageSource damagesource, float damage) {
//        return PetBrain.hurt(this, worldserver, damagesource, damage);
//    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean checkCanMove() {
        return true;
    }

    @Override
    public void setHomePos(BlockPos blockposition) {

    }

    @Nullable
    @Override
    public BlockPos getHomePos() {
        return null;
    }
}
