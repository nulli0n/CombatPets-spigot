package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import javax.annotation.Nullable;
import java.util.UUID;

public class TraderLlamaPet extends TraderLlama implements PetEntity {

    public TraderLlamaPet(@NotNull ServerLevel world) {
        super(EntityType.TRADER_LLAMA, world);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
    }

    @Override
    public boolean inCaravan() {
        return false;
    }

    @Override
    public void joinCaravan(Llama entityllama) {

    }

    @Override
    public boolean canMate(Animal entityanimal) {
        return false;
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
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
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
    public void setInLove(@Nullable Player entityhuman) {

    }

    @Override
    public void setInLoveTime(int i) {

    }

    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel worldserver, Animal entityanimal) {

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
