package su.nightexpress.combatpets.nms.mc_1_21_3.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;

public class FoxPet extends Fox implements PetEntity {

    private final static Class<?>       CLASS_STALK_PREY_GOAL;

    private final static Constructor<?> CONSTRUCT_STALK_PREY_GOAL;

    static {
        CLASS_STALK_PREY_GOAL = Reflex.getInnerClass(Fox.class.getName(), "StalkPreyGoal"); // u

        CONSTRUCT_STALK_PREY_GOAL = Reflex.getConstructor(CLASS_STALK_PREY_GOAL, Fox.class);
    }

    public FoxPet(@NotNull ServerLevel world) {
        super(EntityType.FOX, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FoxFloatGoal());
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        Goal goalCrouch = (Goal) Reflex.invokeConstructor(CONSTRUCT_STALK_PREY_GOAL, this);

        this.goalSelector.addGoal(5, goalCrouch);
        this.goalSelector.addGoal(6, new FoxPounceGoal()); // Need, Jump
        this.goalSelector.addGoal(7, new FoxMeleeAttackGoal(this, 1.2));
        this.goalSelector.addGoal(9, new LeapAtTargetGoal(this, 0.4f));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    private class FoxMeleeAttackGoal extends PetMeleeAttackGoal {

        public FoxMeleeAttackGoal(@NotNull Mob pet, double speedModifier) {
            super(pet, speedModifier);
        }

        public void start() {
            FoxPet.this.setIsInterested(false);
            super.start();
        }

        public boolean canUse() {
            return !FoxPet.this.isSitting() && !FoxPet.this.isSleeping() && !FoxPet.this.isCrouching() && !FoxPet.this.isFaceplanted() && super.canUse();
        }
    }

    private class FoxFloatGoal extends FloatGoal {

        public FoxFloatGoal() {
            super(FoxPet.this);
        }

        @Override
        public void start() {
            super.start();
            FoxPet.this.setIsInterested(false);
            FoxPet.this.setIsCrouching(false);
            FoxPet.this.setSitting(false);
            FoxPet.this.setSleeping(false);
        }

        @Override
        public boolean canUse() {
            return FoxPet.this.isInWater() && FoxPet.this.getFluidHeight(FluidTags.WATER) > 0.25 || FoxPet.this.isInLava();
        }
    }
}
