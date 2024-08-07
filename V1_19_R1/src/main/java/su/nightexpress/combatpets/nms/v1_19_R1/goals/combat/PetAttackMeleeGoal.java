package su.nightexpress.combatpets.nms.v1_19_R1.goals.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.EnumSet;

public class PetAttackMeleeGoal extends Goal {

    protected Mob       pet;
    protected ActivePet petHolder;

    protected int tickUntilNextAttack;
    protected int ticksUntilNextPathCalc;

    private Path   path;
    private double pathTargetX;
    private double pathTargetY;
    private double pathTargetZ;

    public PetAttackMeleeGoal(@NotNull Mob pet) {
        this.pet = pet;
        this.petHolder = ((PetEntity) pet).getHolder();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /*private double getSpeed() {
        return this.pet.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }*/

    @Override
    public void start() {
        this.pet.getNavigation().moveTo(this.path, this.getSpeedModifier());
        this.pet.setAggressive(true);
        this.ticksUntilNextPathCalc = 0;
        this.tickUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        this.pet.setAggressive(false);
        this.pet.getNavigation().stop();
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.pet.getTarget();
        if (target == null || !target.isAlive() || target.getBukkitEntity() == this.petHolder.getOwner()) {
            this.pet.setTarget(null);
            return false;
        }

        this.path = this.pet.getNavigation().createPath(target, 0);
        if (this.path != null) return true;

        return this.pet.isWithinMeleeAttackRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.pet.getTarget();
        if (target == null || !target.isAlive() || target.getBukkitEntity() == this.petHolder.getOwner()) {
            this.pet.setTarget(null);
            return false;
        }
        if (target.isSpectator()) return false;
        if (target instanceof Player player && player.isCreative()) return false;

        return this.pet.isWithinRestriction(new BlockPos(target.getBlockX(), target.getBlockY(), target.getBlockZ()));
    }

    @Override
    public void tick() {
        LivingEntity target = this.pet.getTarget();
        if (target == null) return;

        this.pet.getLookControl().setLookAt(target, 30.0f, 30.0f);
        double distance = this.pet.distanceToSqr(target.getX(), target.getY(), target.getZ());
        this.ticksUntilNextPathCalc = Math.max(this.ticksUntilNextPathCalc - 1, 0);

        boolean isNoPath = (this.pathTargetX == 0 && this.pathTargetY == 0 && this.pathTargetZ == 0);
        boolean isDistOne = target.distanceToSqr(this.pathTargetX, this.pathTargetY, this.pathTargetZ) >= 1;
        boolean isRandom = Rnd.nextFloat() < 0.05f;

        if (this.ticksUntilNextPathCalc <= 0 && (isNoPath || isDistOne || isRandom)) {
            this.pathTargetX = target.getX();
            this.pathTargetY = target.getY();
            this.pathTargetZ = target.getZ();
            this.ticksUntilNextPathCalc = 4 + Rnd.nextInt(7);
            if (distance > 1024.0) {
                this.ticksUntilNextPathCalc += 10;
            }
            else if (distance > 256.0) {
                this.ticksUntilNextPathCalc += 5;
            }
            if (!this.pet.getNavigation().moveTo(target, this.getSpeedModifier())) {
                this.ticksUntilNextPathCalc += 15;
            }
        }
        this.tickUntilNextAttack = Math.max(this.tickUntilNextAttack - 1, 0);
        this.attack(target, distance);
    }

    protected void attack(LivingEntity target, double distance) {
        //double atkRange = this.getMinAttackRange(target);
        if (!this.pet.isWithinMeleeAttackRange(target)) return;
        if (!this.isTimeToAttack()) return;

        this.resetAttackCooldown();
        this.pet.swing(InteractionHand.MAIN_HAND);
        this.pet.doHurtTarget(target);
    }

    protected void resetAttackCooldown() {
        this.tickUntilNextAttack = (int) Math.max(1, 20D / this.getAttackSpeed());//this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.tickUntilNextAttack <= 0;
    }

    /*protected double getMinAttackRange(LivingEntity target) {
        return this.pet.getBbWidth() * 2.0f * (this.pet.getBbWidth() * 2.0f) + target.getBbWidth();
    }*/

    protected double getSpeedModifier() {
        return this.pet.isInWater() ? 2D : 1D;
    }

    protected double getAttackSpeed() {
        AttributeInstance instance = pet.getAttribute(Attributes.ATTACK_SPEED);
        return instance == null ? 0.5 : instance.getBaseValue();
    }
}
