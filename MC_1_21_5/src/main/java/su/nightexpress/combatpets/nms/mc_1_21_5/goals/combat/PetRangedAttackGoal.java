package su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;

import java.util.EnumSet;

public class PetRangedAttackGoal extends AbstractPetAttackGoal {

    protected final RangedAttackMob rangedMob;
    protected final double          attackRadius;
    protected final double          attackRadiusSqr;

    protected int seeTime;

    public PetRangedAttackGoal(@NotNull RangedAttackMob pet, double speedModifier, float attackRadius) {
        super((Mob) pet, speedModifier);
        this.rangedMob = pet;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.pet.getNavigation().isDone());
    }

    private boolean isValidTarget() {
        return this.pet.getTarget() != null && this.pet.getTarget().isAlive();
    }

    @Override
    public void stop() {
        this.pet.setAggressive(false);
        this.pet.setTarget(null);
        this.seeTime = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.pet.getTarget();
        if (target == null) return;

        double distanceSqr = this.pet.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSee = this.pet.getSensing().hasLineOfSight(target);
        if (canSee) {
            ++this.seeTime;
        }
        else {
            this.seeTime = 0;
        }

        if (distanceSqr <= this.attackRadiusSqr && this.seeTime >= 5) {
            this.pet.getNavigation().stop();
        }
        else {
            this.pet.getNavigation().moveTo(target, this.speedModifier);
        }

        this.pet.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.ticksUntilNextAttack--;

        if (this.isTimeToAttack()) {
            if (!canSee) {
                return;
            }

            double distanceRradius = Math.sqrt(distanceSqr) / this.attackRadius;
            float clamped = (float) Mth.clamp(distanceRradius, 0.1F, 1.0F);
            this.rangedMob.performRangedAttack(target, clamped);
            this.petHolder.doExhaust(ExhaustReason.COMBAT);
            this.setAttackCooldown();
        }
    }
}
