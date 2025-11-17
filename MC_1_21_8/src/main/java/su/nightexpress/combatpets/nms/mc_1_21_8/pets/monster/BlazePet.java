package su.nightexpress.combatpets.nms.mc_1_21_8.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.AbstractPetAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Method;
import java.util.EnumSet;

public class BlazePet extends Blaze implements PetEntity {

    private static final Method SET_CHARGED = Reflex.safeMethod(Blaze.class,  "setCharged", "x", Boolean.TYPE);

    public BlazePet(@NotNull ServerLevel world) {
        super(EntityType.BLAZE, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(4, new BlazeFireballGoal(this));
        this.goalSelector.addGoal(7, new PetFollowOwnerGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    static class BlazeFireballGoal extends AbstractPetAttackGoal {

        private final Blaze blaze;
        private       int   balls;
        private       int   noSeeTicks;

        public BlazeFireballGoal(@NotNull Blaze blaze) {
            super(blaze);
            this.blaze = blaze;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.blaze.getTarget();
            return target != null && target.isAlive() && this.blaze.canAttack(target);
        }

        @Override
        public void start() {
            this.balls = 0;
        }

        @Override
        public void stop() {
            Reflex.invokeMethod(SET_CHARGED, this.blaze, false);
            this.noSeeTicks = 0;
        }

        @Override
        public void tick() {
            this.ticksUntilNextAttack--;

            LivingEntity target = this.blaze.getTarget();
            if (target == null) return;

            boolean canSee = this.blaze.getSensing().hasLineOfSight(target);
            if (canSee) {
                this.noSeeTicks = 0;
            }
            else {
                ++this.noSeeTicks;
            }

            double distanceSqr = this.blaze.distanceToSqr(target);
            if (distanceSqr < 4D) {
                if (!canSee) return;
                if (this.isTimeToAttack()) {
                    this.setAttackCooldown(1D);
                    this.blaze.doHurtTarget((ServerLevel) this.blaze.level(), target);
                    this.petHolder.doExhaust(ExhaustReason.COMBAT);
                }
                this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }
            else if (distanceSqr < this.followRange() * this.followRange() && canSee) {
                double ballX = target.getX() - this.blaze.getX();
                double ballY = target.getY(0.5) - this.blaze.getY(0.5);
                double ballZ = target.getZ() - this.blaze.getZ();
                if (this.isTimeToAttack()) {
                    ++this.balls;

                    if (this.balls == 1) {
                        this.setAttackCooldown(0.25);
                        Reflex.invokeMethod(SET_CHARGED, this.blaze, true);
                    }
                    else if (this.balls <= 4) {
                        this.setAttackCooldown(0.05);
                    }
                    else {
                        this.setAttackCooldown(1D);
                        this.balls = 0;
                        Reflex.invokeMethod(SET_CHARGED, this.blaze, false);
                        this.petHolder.doExhaust(ExhaustReason.COMBAT);
                    }

                    if (this.balls > 1) {
                        double mod = Math.sqrt(Math.sqrt(distanceSqr)) * 0.5D;
                        if (!this.blaze.isSilent()) {
                            this.blaze.level().levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                        }

                        for (int count = 0; count < 1; ++count) {
                            Vec3 direction = new Vec3(this.blaze.getRandom().triangle(ballX, 2.297 * mod), ballY, this.blaze.getRandom().triangle(ballZ, 2.297 * mod));
                            SmallFireball fireball = new SmallFireball(this.blaze.level(), this.blaze, direction.normalize());

                            //SmallFireball fireball = new SmallFireball(this.blaze.level(), this.blaze, ballX + Rnd.nextGaussian() * mod, ballY, ballZ + Rnd.nextGaussian() * mod);
                            fireball.setPos(fireball.getX(), this.blaze.getY(0.5D) + 0.5D, fireball.getZ());
                            this.blaze.level().addFreshEntity(fireball);
                        }
                    }
                }
                this.blaze.getLookControl().setLookAt(target, 10F, 10F);
            }
            else if (this.noSeeTicks < 5) {
                this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }

            super.tick();
        }

        private double followRange() {
            return 32D;
        }
    }
}
