package su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.EnumSet;

public class PetBowAttackGoal<T extends Monster & RangedAttackMob> extends PetRangedAttackGoal {

    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int     strafingTime = -1;

    public PetBowAttackGoal(@NotNull T mob, double speedModifier, float attackRadius) {
        super(mob, speedModifier, attackRadius);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected boolean isHoldingBow() {
        return this.pet.isHolding(Items.BOW);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.isHoldingBow();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.pet.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.pet.stopUsingItem();
    }

    @Override
    public void tick() {
        LivingEntity target = this.pet.getTarget();
        if (target == null) return;

        double distanceToTarget = this.pet.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSee = this.pet.getSensing().hasLineOfSight(target);
        boolean seen = this.seeTime > 0;
        if (canSee != seen) {
            this.seeTime = 0;
        }

        if (canSee) {
            ++this.seeTime;
        }
        else {
            --this.seeTime;
        }

        if (distanceToTarget <= this.attackRadiusSqr && this.seeTime >= 20) {
            this.pet.getNavigation().stop();
            ++this.strafingTime;
        }
        else {
            this.pet.getNavigation().moveTo(target, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if (Rnd.RANDOM.nextFloat() < 0.3) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if (Rnd.RANDOM.nextFloat() < 0.3) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distanceToTarget > (this.attackRadiusSqr * 0.75F)) {
                this.strafingBackwards = false;
            }
            else if (distanceToTarget < (this.attackRadiusSqr * 0.25F)) {
                this.strafingBackwards = true;
            }

            this.pet.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            Entity vehicle = this.pet.getControlledVehicle();
            if (vehicle instanceof Mob horse) {
                horse.lookAt(target, 30.0F, 30.0F);
            }

            this.pet.lookAt(target, 30.0F, 30.0F);
        }
        else {
            this.pet.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.pet.isUsingItem()) {
            if (!canSee && this.seeTime < -60) {
                this.pet.stopUsingItem();
            }
            else if (canSee) {
                int ticksUsingItem = this.pet.getTicksUsingItem();
                if (ticksUsingItem >= 20) {
                    this.pet.stopUsingItem();
                    this.rangedMob.performRangedAttack(target, BowItem.getPowerForTime(ticksUsingItem));
                    this.petHolder.doExhaust(ExhaustReason.COMBAT);
                    this.setAttackCooldown();
                }
            }
        }
        else {
            this.ticksUntilNextAttack--;
            if (this.isTimeToAttack() && this.seeTime >= -60) {
                this.pet.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.pet, Items.BOW));
            }
        }
    }
}
