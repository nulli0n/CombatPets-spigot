package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetAttackMeleeGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import java.util.UUID;

public class IronGolemPet extends IronGolem implements PetEntity {

    public IronGolemPet(@NotNull ServerLevel world) {
        super(EntityType.IRON_GOLEM, world);
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
        this.goalSelector.addGoal(1, new PetAttackMeleeGoal(this));
    }

    @Override
    public boolean isPlayerCreated() {
        return false;
    }

    /*public boolean c(EntityLiving entityliving) {
        return true;
    }*/

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean setTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (reason == EntityTargetEvent.TargetReason.COLLISION) {
            return false;
        }
        return super.setTarget(entityliving, reason, fireEvent);
    }


    @Override
    public boolean canAttackType(EntityType<?> entitytypes) {
        return true;
    }
}

