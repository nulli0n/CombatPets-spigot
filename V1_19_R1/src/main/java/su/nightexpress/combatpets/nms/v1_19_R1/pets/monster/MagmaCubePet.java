package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import java.util.UUID;

public class MagmaCubePet extends MagmaCube implements PetEntity {

    public MagmaCubePet(@NotNull ServerLevel world) {
        super(EntityType.MAGMA_CUBE, world);
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
        this.goalSelector.addGoal(2, new SlimePet.SlimeAttackGoal(this));
    }

    @Override
    protected int getJumpDelay() {
        return 15;
    }

    @Override
    public void push(Entity entity) {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (entity.getUUID().equals(this.getHolder().getOwner().getUniqueId())) return;
        if (!entity.equals(target)) return;
        if (!(entity instanceof IronGolem)) {
            this.dealDamage(target);
        }
        super.push(entity);
    }

    @Override
    public void playerTouch(Player entity) {
        if (entity.getUUID().equals(this.getHolder().getOwner().getUniqueId())) return;
        super.playerTouch(entity);
    }

    @Override
    protected boolean isDealsDamage() {
        return true;
    }
}
