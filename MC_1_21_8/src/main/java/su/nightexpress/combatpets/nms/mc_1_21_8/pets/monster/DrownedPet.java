package su.nightexpress.combatpets.nms.mc_1_21_8.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetRangedAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetZombieAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow.PetFollowOwnerGoal;

public class DrownedPet extends Drowned implements PetEntity {

    public DrownedPet(@NotNull ServerLevel world) {
        super(EntityType.DROWNED, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new PetZombieAttackGoal(this, 1D));
        this.goalSelector.addGoal(3, new PetRangedAttackGoal(this, 1D, 10F) {

            @Override
            public boolean canUse() {
                return super.canUse() && this.pet.getMainHandItem().is(Items.TRIDENT);
            }

            public void start() {
                super.start();
                this.pet.setAggressive(true);
                this.pet.startUsingItem(InteractionHand.MAIN_HAND);
            }

            @Override
            public void stop() {
                super.stop();
                this.pet.stopUsingItem();
            }
        });
    }

    @Override
    protected void customServerAiStep(ServerLevel worldserver) {
        super.customServerAiStep(worldserver);
        this.setSearchingForLand(this.isUnderWater() && this.getY() < (this.level().getSeaLevel() - 2));
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
    public boolean okTarget(@Nullable LivingEntity var0) {
        return true;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }
}
