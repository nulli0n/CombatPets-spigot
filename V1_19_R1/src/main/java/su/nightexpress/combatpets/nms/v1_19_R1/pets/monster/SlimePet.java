package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.AbstractPetFollowOwnerGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.EnumSet;
import java.util.UUID;

public class SlimePet extends Slime implements PetEntity {

    public SlimePet(@NotNull ServerLevel world) {
        super(EntityType.SLIME, world);
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
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
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

    public static class SlimeAttackGoal extends Goal {

        private final Slime slime;

        public SlimeAttackGoal(Slime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.slime.getTarget();
            if (target == null) return;

            this.slime.lookAt(target, 10.0F, 10.0F);

            Reflex.invokeMethod(AbstractPetFollowOwnerGoal.SLIME_ROTATE, slime.getMoveControl(), slime.getYRot(), true);
            slime.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1);
        }
    }
}
