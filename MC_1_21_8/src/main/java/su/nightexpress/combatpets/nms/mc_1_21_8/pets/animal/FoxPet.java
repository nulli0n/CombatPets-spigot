package su.nightexpress.combatpets.nms.mc_1_21_8.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Method;
import java.util.EnumSet;

public class FoxPet extends Fox implements PetEntity {

    private static final Method SET_FACEPLANTED = Reflex.safeMethod(Fox.class, "B", boolean.class);
    private static final Method CLEAR_STATES    = Reflex.safeMethod(Fox.class, "gY");

    public FoxPet(@NotNull ServerLevel world) {
        super(EntityType.FOX, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FoxFloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        this.goalSelector.addGoal(5, new FoxStalkPreyGoal(this));
        this.goalSelector.addGoal(6, new FoxPounceGoal()); // Need, Jump
        this.goalSelector.addGoal(7, new FoxAttackGoal(this, 1.2));
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

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    private static class FoxAttackGoal extends PetMeleeAttackGoal {

        private final FoxPet pet;

        public FoxAttackGoal(@NotNull FoxPet pet, double speedModifier) {
            super(pet, speedModifier);
            this.pet = pet;
        }

        public void start() {
            this.pet.setIsInterested(false);
            super.start();
        }

        public boolean canUse() {
            return !this.pet.isSitting() && !this.pet.isSleeping() && !this.pet.isCrouching() && !this.pet.isFaceplanted() && super.canUse();
        }
    }

    private static class FoxFloatGoal extends FloatGoal {

        private final FoxPet pet;

        public FoxFloatGoal(@NotNull FoxPet pet) {
            super(pet);
            this.pet = pet;
        }

        @Override
        public void start() {
            super.start();
            if (CLEAR_STATES != null) Reflex.invokeMethod(CLEAR_STATES, this.pet);
        }

        @Override
        public boolean canUse() {
            return this.pet.isInWater() && this.pet.getFluidHeight(FluidTags.WATER) > 0.25 || this.pet.isInLava();
        }
    }

    private static class FoxStalkPreyGoal extends Goal {

        private static final float MAX_DISTANCE = 36;

        private final FoxPet pet;

        public FoxStalkPreyGoal(@NotNull FoxPet pet) {
            this.pet = pet;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.pet.isSleeping()) {
                return false;
            }
            LivingEntity target = this.pet.getTarget();
            return target != null && target.isAlive() && this.pet.distanceToSqr(target) > MAX_DISTANCE && !this.pet.isCrouching() && !this.pet.isInterested() && !this.pet.jumping;
        }

        @Override
        public void start() {
            this.pet.setSitting(false);
            if (SET_FACEPLANTED != null) Reflex.invokeMethod(SET_FACEPLANTED, this.pet, false);
        }

        @Override
        public void stop() {
            LivingEntity target = this.pet.getTarget();
            if (target != null && Fox.isPathClear(this.pet, target)) {
                this.pet.setIsInterested(true);
                this.pet.setIsCrouching(true);
                this.pet.getNavigation().stop();
                this.pet.getLookControl().setLookAt(target, (float) this.pet.getMaxHeadYRot(), (float) this.pet.getMaxHeadXRot());
            }
            else {
                this.pet.setIsInterested(false);
                this.pet.setIsCrouching(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.pet.getTarget();
            if (target == null) return;

            this.pet.getLookControl().setLookAt(target, (float) this.pet.getMaxHeadYRot(), (float) this.pet.getMaxHeadXRot());
            if (this.pet.distanceToSqr(target) <= MAX_DISTANCE) {
                this.pet.setIsInterested(true);
                this.pet.setIsCrouching(true);
                this.pet.getNavigation().stop();
            }
            else {
                this.pet.getNavigation().moveTo(target, 1.5);
            }
        }
    }
}
