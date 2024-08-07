package su.nightexpress.combatpets.nms.v1_20.pets.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetRangedAttackGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

public class WitherPet extends WitherBoss implements PetEntity {

    public WitherPet(@NotNull ServerLevel world) {
        super(EntityType.WITHER, world);
        this.bossEvent.setVisible(false);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetRangedAttackGoal(this, 1.0D, 20.0F));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData,
                                        @org.jetbrains.annotations.Nullable CompoundTag tag) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        V1_20.hurtArmor(this, source, amount);
    }

    @Override
    protected void customServerAiStep() {
        // nextHeadUpdate
        Reflex.setFieldValue(this, "bZ", new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE});
        super.customServerAiStep();
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
