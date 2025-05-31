package su.nightexpress.combatpets.nms.mc_1_21_5.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat.PetRangedAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

public class WitherPet extends WitherBoss implements PetEntity {

    public WitherPet(@NotNull ServerLevel world) {
        super(EntityType.WITHER, world);
        this.bossEvent.setVisible(false);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetRangedAttackGoal(this, 1.0D, 20.0F));
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
    protected void customServerAiStep(ServerLevel worldserver) {
        Reflex.setFieldValue(this, "bN", new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}); // nextHeadUpdate
        super.customServerAiStep(worldserver);
    }

    @Override
    public void makeInvulnerable() {

    }

    @Override
    public int getAlternativeTarget(int i) {
        return this.getTarget() == null ? 0 : this.getTarget().getId();
    }

    @Override
    public void setAlternativeTarget(int i, int j) {

    }
}
