package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetAttackMeleeGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import javax.annotation.Nullable;
import java.util.UUID;

public class WanderingTraderPet extends WanderingTrader implements PetEntity {

    public WanderingTraderPet(@NotNull ServerLevel level) {
        super(EntityType.WANDERING_TRADER, level);
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
        this.goalSelector.addGoal(4, new PetAttackMeleeGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide());
    }

    @Override
    protected void updateTrades() {

    }

    @Override
    public void setWanderTarget(@Nullable BlockPos blockposition) {

    }
}
