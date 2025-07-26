package su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;

import java.util.EnumSet;

public class PetCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends PetRangedAttackGoal {

    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);

    private final CrossbowAttackMob crossbowMob;

    private State state;
    private int   updatePathDelay;

    public PetCrossbowAttackGoal(@NotNull T mob, double speedModifier, float attackRadius) {
        super(mob, speedModifier, attackRadius);
        this.crossbowMob = mob;
        this.state = State.UNCHARGED;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.isHoldingCrossbow();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.pet.isHolding(Items.CROSSBOW);
    }

    private boolean isValidTarget() {
        return this.pet.getTarget() != null && this.pet.getTarget().isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        if (this.pet.isUsingItem()) {
            this.pet.stopUsingItem();
            this.crossbowMob.setChargingCrossbow(false);
            this.pet.getUseItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.pet.getTarget();
        if (target == null) return;

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

        double distanceToTarget = this.pet.distanceToSqr(target);
        boolean isFarAway = (distanceToTarget > (double) this.attackRadiusSqr || this.seeTime < 5) && this.isTimeToAttack();
        if (isFarAway) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
                this.pet.getNavigation().moveTo(target, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.pet.getRandom());
            }
        }
        else {
            this.updatePathDelay = 0;
            this.pet.getNavigation().stop();
        }

        this.pet.getLookControl().setLookAt(target, 30F, 30F);

        if (this.state == State.UNCHARGED) {
            if (!isFarAway) {
                this.pet.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.pet, Items.CROSSBOW));
                this.state = State.CHARGING;
                this.crossbowMob.setChargingCrossbow(true);
            }
        }
        else if (this.state == State.CHARGING) {
            if (!this.pet.isUsingItem()) {
                this.state = State.UNCHARGED;
            }

            int var6 = this.pet.getTicksUsingItem();
            ItemStack var7 = this.pet.getUseItem();
            if (var6 >= CrossbowItem.getChargeDuration(var7, this.pet)) {
                this.pet.releaseUsingItem();
                this.state = State.CHARGED;
                this.crossbowMob.setChargingCrossbow(false);
                this.setAttackCooldown();
            }
        }
        else if (this.state == State.CHARGED) {
            this.ticksUntilNextAttack--;
            if (this.isTimeToAttack()) {
                this.state = State.READY_TO_ATTACK;
            }
        }
        else if (this.state == State.READY_TO_ATTACK && canSee) {
            this.rangedMob.performRangedAttack(target, 1F);
            this.petHolder.doExhaust(ExhaustReason.COMBAT);
            ItemStack itemInHand = this.pet.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.pet, Items.CROSSBOW));
            //CrossbowItem.setCharged(itemInHand, false);
            this.pet.getUseItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            this.state = State.UNCHARGED;
        }

    }

    private boolean canRun() {
        return this.state == State.UNCHARGED;
    }

    enum State {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK
    }
}
