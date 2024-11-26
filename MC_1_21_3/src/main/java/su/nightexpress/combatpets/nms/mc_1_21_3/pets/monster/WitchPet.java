package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.MC_1_21_3;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetRangedAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetFollowOwnerGoal;

import javax.annotation.Nullable;

public class WitchPet extends Witch implements PetEntity {

    public WitchPet(@NotNull ServerLevel level) {
        super(EntityType.WITCH, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetRangedAttackGoal(this, 1.0D, 10.0F));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public boolean isAlliedTo(Team team) {
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
    protected void pickUpItem(ServerLevel worldserver, ItemEntity entityitem) {

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