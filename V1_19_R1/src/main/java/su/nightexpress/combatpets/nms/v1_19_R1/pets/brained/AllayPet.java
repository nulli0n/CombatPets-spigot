package su.nightexpress.combatpets.nms.v1_19_R1.pets.brained;

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
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetAI;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.PetBrain;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.v1_19_R1.brain.behavior.PetIdleBehaviors;

import java.util.UUID;
import java.util.function.BiConsumer;

public class AllayPet extends Allay implements PetEntity {

    public AllayPet(@NotNull ServerLevel world) {
        super(EntityType.ALLAY, world);
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
    protected Brain.Provider<Allay> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Allay> refreshBrain(@NotNull Allay pet, @NotNull Brain<Allay> brain) {
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
            PetFightBehaviors.meleeAttack(20)
        ));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
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
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateActivity();
    }

    protected void updateActivity() {
        Brain<Allay> brain = this.getBrain();
        if (PetAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var0) {

    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    protected void dropEquipment() {

    }

    @Override
    public boolean wantsToPickUp(ItemStack var0) {
        return false;
    }

    @Override
    public boolean isPanicking() {
        return false;
    }

    @Override
    public boolean canDuplicate() {
        return false;
    }
}
