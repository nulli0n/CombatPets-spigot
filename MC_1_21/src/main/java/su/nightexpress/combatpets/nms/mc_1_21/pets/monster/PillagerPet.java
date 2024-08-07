package su.nightexpress.combatpets.nms.mc_1_21.pets.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetCrossbowAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.follow.PetFollowOwnerGoal;

import javax.annotation.Nullable;

public class PillagerPet extends Pillager implements PetEntity {

    public PillagerPet(@NotNull ServerLevel level) {
        super(EntityType.PILLAGER, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new PetCrossbowAttackGoal<>(this, 1.0D, 8.0F));
        this.goalSelector.addGoal(4, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        MC_1_21.hurtArmor(this, source, amount);
    }

    @Override
    public boolean isAlliedTo(Entity var0) {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity var0) {
        return var0.canBeSeenAsEnemy();
    }

    @Override
    public boolean canJoinPatrol() {
        return false;
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    public void setCanJoinRaid(boolean flag) {

    }

    @Override
    public void setCurrentRaid(@Nullable Raid raid) {

    }

    @Override
    public @Nullable
    Raid getCurrentRaid() {
        return null;
    }

    @Override
    protected boolean isPatrolling() {
        return false;
    }

    @Override
    public boolean isPatrolLeader() {
        return false;
    }

    @Override
    protected void setPatrolling(boolean var0) {

    }

    @Override
    public void setPatrolLeader(boolean var0) {

    }

    @Override
    public void setPatrolTarget(BlockPos var0) {

    }

    @Override
    public void setWave(int i) {

    }

    @Override
    public boolean isCelebrating() {
        return false;
    }

    @Override
    protected void pickUpItem(ItemEntity var0) {

    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public void findPatrolTarget() {

    }

    @Override
    public void applyRaidBuffs(ServerLevel worldserver, int i, boolean flag) {

    }
}
