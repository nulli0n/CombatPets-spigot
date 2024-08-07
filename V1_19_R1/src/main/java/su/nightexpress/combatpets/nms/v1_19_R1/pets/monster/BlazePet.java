package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.SmallFireball;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.UUID;

public class BlazePet extends Blaze implements PetEntity {

    private static final Method METHOD_SET_CHARGED;

    static {
        METHOD_SET_CHARGED = Reflex.getMethod(Blaze.class, "w", Boolean.TYPE);
    }

    public BlazePet(@NotNull ServerLevel world) {
        super(EntityType.BLAZE, world);
        //this.navigation = new NavigatorFly(this, world);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new PathfinderGoalBlazeFireball(this));
        this.goalSelector.addGoal(7, new PetFollowOwnerGroundGoal(this));
    }

    /*@Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            ServerPlayer owner = ((CraftPlayer) this.getHolder().getOwner()).getHandle();
            if (owner.getEyeY() > this.getEyeY() + 0.5F) {
                Vec3 movement = this.getDeltaMovement();
                this.setDeltaMovement(this.getDeltaMovement().add(0D, (0.3 - movement.y) * 0.2, 0D));
                this.hasImpulse = true;
            }
        }
        super.customServerAiStep();
    }*/

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    class PathfinderGoalBlazeFireball extends Goal {

        private final Blaze        blaze;
        private final ServerPlayer owner;
        private       int          balls;
        private       int          cooldown;
        private       int          noSeeTicks;

        public PathfinderGoalBlazeFireball(@NotNull Blaze blaze) {
            this.blaze = blaze;
            this.owner = ((CraftPlayer) getHolder().getOwner()).getHandle();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.blaze.getTarget();
            return target != null && !target.equals(owner) && target.isAlive() && this.blaze.canAttack(target);
        }

        // .start
        @Override
        public void start() {
            this.balls = 0;
        }

        // .stop
        @Override
        public void stop() {
            Reflex.invokeMethod(METHOD_SET_CHARGED, this.blaze, false);
            this.noSeeTicks = 0;
        }

        @Override
        public void tick() {
            --this.cooldown;

            LivingEntity target = this.blaze.getTarget();
            if (target == null) {
                return;
            }

            boolean canSee = this.blaze.getSensing().hasLineOfSight(target);
            if (canSee) {
                this.noSeeTicks = 0;
            }
            else {
                ++this.noSeeTicks;
            }
            double distanceSqr = this.blaze.distanceToSqr(target);
            if (distanceSqr < 4.0) {
                if (!canSee) {
                    return;
                }
                if (this.cooldown <= 0) {
                    this.cooldown = 20;
                    this.blaze.doHurtTarget(target);
                }
                this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }
            else if (distanceSqr < this.followRange() * this.followRange() && canSee) {
                double ballX = target.getX() - this.blaze.getX();
                double ballY = target.getY(0.5) - this.blaze.getY(0.5);
                double ballZ = target.getZ() - this.blaze.getZ();
                if (this.cooldown <= 0) {
                    ++this.balls;
                    if (this.balls == 1) {
                        this.cooldown = 10;
                        Reflex.invokeMethod(METHOD_SET_CHARGED, this.blaze, true);
                    }
                    else if (this.balls <= 4) {
                        this.cooldown = 6;
                    }
                    else {
                        this.cooldown = 20;
                        this.balls = 0;
                        Reflex.invokeMethod(METHOD_SET_CHARGED, this.blaze, false);
                    }
                    if (this.balls > 1) {
                        double var10 = Math.sqrt(Math.sqrt(distanceSqr)) * 0.5D;
                        if (!this.blaze.isSilent()) {
                            this.blaze.level.levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                        }

                        for (int var12 = 0; var12 < 1; ++var12) {
                            SmallFireball var13 = new SmallFireball(this.blaze.level, this.blaze, ballX + this.blaze.getRandom().nextGaussian() * var10, ballY, ballZ + this.blaze.getRandom().nextGaussian() * var10);
                            var13.setPos(var13.getX(), this.blaze.getY(0.5D) + 0.5D, var13.getZ());
                            this.blaze.level.addFreshEntity(var13);
                        }
                    }
                }
                this.blaze.getLookControl().setLookAt(target, 10.0f, 10.0f);
            }
            else if (this.noSeeTicks < 5) {
                this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }
            super.tick();
        }

        private double followRange() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
