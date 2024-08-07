package su.nightexpress.combatpets.nms.v1_19_R1.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetAI;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetBrain;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetIdleBehaviors;

import java.util.UUID;

public class PiglinBrutePet extends PiglinBrute implements PetEntity {

    public PiglinBrutePet(@NotNull ServerLevel level) {
        super(EntityType.PIGLIN_BRUTE, level);
        this.setImmuneToZombification(true);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        PetBrain.setOwnerMemory(this, this.getHolder());
    }

    @Override
    protected Brain.Provider<PiglinBrute> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<PiglinBrute> refreshBrain(@NotNull PiglinBrute pet, @NotNull Brain<PiglinBrute> brain) {
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
            PetFightBehaviors.meleeAttack(20))
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateActivity();
    }

    protected void updateActivity() {
        Brain<PiglinBrute> brain = this.getBrain();

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
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void pickUpItem(ItemEntity entityitem) {

    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void playAngrySound() {

    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        return false;
    }
}
