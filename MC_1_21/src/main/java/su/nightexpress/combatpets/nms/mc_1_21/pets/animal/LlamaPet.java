package su.nightexpress.combatpets.nms.mc_1_21.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.follow.PetFollowOwnerGoal;

import java.util.UUID;

public class LlamaPet extends Llama implements PetEntity {

    public LlamaPet(@NotNull ServerLevel world) {
        super(EntityType.LLAMA, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public boolean inCaravan() {
        return false;
    }

    @Override
    public void joinCaravan(Llama entityllama) {

    }

    @Override
    public boolean isSaddleable() {
        return true;
    }

    @Override
    public boolean isSaddled() {
        return true;
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    @NotNull
    public UUID getOwnerUUID() {
        return this.getHolder().getOwner().getUniqueId();
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        MC_1_21.hurtArmor(this, source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected boolean handleEating(Player entityhuman, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isEating() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void followMommy() {

    }

    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    protected boolean canParent() {
        return false;
    }
}
