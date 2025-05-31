package su.nightexpress.combatpets.nms.mc_1_21_5.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.follow.PetFollowOwnerGoal;

public class TraderLlamaPet extends TraderLlama implements PetEntity {

    public TraderLlamaPet(@NotNull ServerLevel world) {
        super(EntityType.TRADER_LLAMA, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public void aiStep() {
        this.setDespawnDelay(Integer.MAX_VALUE);
        super.aiStep();
    }

    @Override
    public void joinCaravan(Llama entityllama) {

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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    protected boolean handleEating(Player entityhuman, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean canEatGrass() {
        return false;
    }
}
