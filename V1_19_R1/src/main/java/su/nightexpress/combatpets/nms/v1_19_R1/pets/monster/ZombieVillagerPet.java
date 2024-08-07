package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import javax.annotation.Nullable;
import java.util.UUID;

public class ZombieVillagerPet extends ZombieVillager implements PetEntity {

    public ZombieVillagerPet(@NotNull ServerLevel level) {
        super(EntityType.ZOMBIE_VILLAGER, level);
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
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1, false));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean isUnderWaterConverting() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public void startUnderWaterConversion(int i) {

    }

    @Override
    protected void doUnderWaterConversion() {

    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    public void startConverting(@Nullable UUID uuid, int i) {

    }
}
