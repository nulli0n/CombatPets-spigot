package su.nightexpress.combatpets.nms.mc_1_21.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.mc_1_21.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21.brain.behavior.PetIdleBehaviors;

public class ArmadilloPet extends Armadillo implements PetEntity {

    public ArmadilloPet(@NotNull ServerLevel level) {
        super(EntityType.ARMADILLO, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Armadillo> refreshBrain(@NotNull Armadillo pet, @NotNull Brain<Armadillo> brain) {
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

        this.brain = brain;
        return brain;
    }

    @Override
    public Brain<? extends LivingEntity> getBrain() {
        return super.getBrain();
    }

    @SuppressWarnings("unchecked")
    private <E extends LivingEntity> E get() {
        return (E) this;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.level().getProfiler().push("armadilloBrain");
        this.getBrain().tick((ServerLevel) this.level(), this.get());
        this.level().getProfiler().pop();
        this.level().getProfiler().push("armadilloActivityUpdate");
        this.updateActivity();
        this.level().getProfiler().pop();
    }

    protected void updateActivity() {
        Brain<?> brain = this.getBrain();
        if (PetAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        return PetBrain.hurt(this, damagesource, damage);
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        MC_1_21.hurtArmor(this, source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public boolean isScaredBy(LivingEntity entityliving) {
        return false;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return false;
    }
}
