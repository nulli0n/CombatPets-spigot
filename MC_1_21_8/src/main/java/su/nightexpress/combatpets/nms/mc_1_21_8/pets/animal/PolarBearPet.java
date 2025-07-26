package su.nightexpress.combatpets.nms.mc_1_21_8.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow.PetFollowOwnerGoal;

public class PolarBearPet extends PolarBear implements PetEntity {

    public PolarBearPet(@NotNull ServerLevel level) {
        super(EntityType.POLAR_BEAR, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PolarBearAttackGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    private static class PolarBearAttackGoal extends PetMeleeAttackGoal {

        private final PolarBearPet bearPet;

        public PolarBearAttackGoal(@NotNull PolarBearPet bearPet) {
            super(bearPet, 1.25);
            this.bearPet = bearPet;
        }

        @Override
        protected void attack(@NotNull LivingEntity target) {
            if (this.canPerformAttack(target)) {
                this.setAttackCooldown();
                this.bearPet.doHurtTarget(getServerLevel(this.pet), target);
                this.bearPet.setStanding(false);
                return;
            }

            if (this.bearPet.distanceToSqr(target) < (double) ((target.getBbWidth() + 3F) * (target.getBbWidth() + 3F))) {
                if (this.isTimeToAttack()) {
                    this.bearPet.setStanding(false);
                    this.setAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    this.bearPet.setStanding(true);
                    this.bearPet.playWarningSound();
                }

                return;
            }

            this.setAttackCooldown();
            this.bearPet.setStanding(false);
        }

        public void stop() {
            this.bearPet.setStanding(false);
            super.stop();
        }
    }
}
