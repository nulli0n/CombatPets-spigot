package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.UUID;

public class WitherPet extends WitherBoss implements PetEntity {

    public WitherPet(@NotNull ServerLevel world) {
        super(EntityType.WITHER, world);
        this.bossEvent.setVisible(false);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
    }

    @Override
    protected void customServerAiStep() {
        // nextHeadUpdate
        Reflex.setFieldValue(this, "bZ", new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE});
        super.customServerAiStep();
    }

    @Override
    public void makeInvulnerable() {

    }

    @Override
    public int getAlternativeTarget(int i) {
        return this.getTarget() == null ? 0 : this.getTarget().getId();
    }

    @Override
    public void setAlternativeTarget(int i, int j) {

    }
}
