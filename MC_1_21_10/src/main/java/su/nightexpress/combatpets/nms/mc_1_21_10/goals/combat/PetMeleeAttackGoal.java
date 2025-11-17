package su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.EnumSet;

public class PetMeleeAttackGoal extends AbstractPetAttackGoal {

    protected int ticksUntilNextPathCalc;

    private Path   path;
    private double pathTargetX;
    private double pathTargetY;
    private double pathTargetZ;

    public PetMeleeAttackGoal(@NotNull Mob pet) {
        super(pet);
    }

    public PetMeleeAttackGoal(@NotNull Mob pet, double speedModifier) {
        super(pet, speedModifier);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.pet.getNavigation().moveTo(this.path, this.getSpeedModifier());
        this.pet.setAggressive(true);
        this.ticksUntilNextPathCalc = 0;
        this.ticksUntilNextAttack = 0;
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

        return !(target instanceof Player player) || !player.isCreative();
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
        boolean isRandom = Rnd.RANDOM.nextFloat() < 0.05f;

        if (this.ticksUntilNextPathCalc <= 0 && (isNoPath || isDistOne || isRandom)) {
            this.pathTargetX = target.getX();
            this.pathTargetY = target.getY();
            this.pathTargetZ = target.getZ();
            this.ticksUntilNextPathCalc = 4 + Rnd.nextInt(7);
            if (distance > 1024D) {
                this.ticksUntilNextPathCalc += 10;
            }
            else if (distance > 256D) {
                this.ticksUntilNextPathCalc += 5;
            }
            if (!this.pet.getNavigation().moveTo(target, this.getSpeedModifier())) {
                this.ticksUntilNextPathCalc += 15;
            }
        }
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.attack(target);
    }

    protected boolean canPerformAttack(@NotNull LivingEntity target) {
        return this.pet.isWithinMeleeAttackRange(target) && this.isTimeToAttack();
    }

    protected void attack(@NotNull LivingEntity target) {
        if (!this.canPerformAttack(target)) return;

        this.setAttackCooldown();
        this.pet.swing(InteractionHand.MAIN_HAND);
        this.pet.doHurtTarget((ServerLevel) this.pet.level(), target);
        this.petHolder.doExhaust(ExhaustReason.COMBAT);
    }
}
