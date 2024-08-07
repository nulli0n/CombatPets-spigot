package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import javax.annotation.Nullable;
import java.util.UUID;

public class OcelotPet extends Ocelot implements PetEntity {

    public OcelotPet(@NotNull ServerLevel world) {
        super(EntityType.OCELOT, world);
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
        this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
    }

    @Override
    public boolean isTrusting() {
        return true;
    }

    @Override
    protected void reassessTrustingGoals() {

    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    // .isFood
    @Override
    public boolean isFood(ItemStack itemstack) {
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
    public boolean canMate(Animal entityanimal) {
        return false;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel worldserver, Animal entityanimal) {

    }
}
