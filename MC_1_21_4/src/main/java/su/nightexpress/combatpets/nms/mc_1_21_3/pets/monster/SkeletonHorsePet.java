package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetFollowOwnerGoal;

import java.util.UUID;

public class SkeletonHorsePet extends SkeletonHorse implements PetEntity {

    public SkeletonHorsePet(@NotNull ServerLevel level) {
        super(EntityType.SKELETON_HORSE, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(4, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    @NotNull
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected boolean handleEating(Player entityhuman, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    public boolean canEatGrass() {
        return false;
    }
}
