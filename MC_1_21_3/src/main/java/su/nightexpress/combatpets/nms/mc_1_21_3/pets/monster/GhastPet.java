package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.AbstractPetAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.AbstractPetFollowOwnerGoal;

public class GhastPet extends Ghast implements PetEntity {

    public GhastPet(@NotNull ServerLevel world) {
        super(EntityType.GHAST, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new GhastFollowTargetGoal(this));
        this.goalSelector.addGoal(3, new GhastShootFireballGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    private static class GhastShootFireballGoal extends AbstractPetAttackGoal {

        private final Ghast ghast;

        public GhastShootFireballGoal(@NotNull Ghast ghast) {
            super(ghast, 1D);
            this.ghast = ghast;
        }

        @Override
        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {
            this.ghast.setCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null) return;

            if (target.distanceToSqr(this.ghast) < 4096 && this.ghast.hasLineOfSight(target)) {
                Level world = this.ghast.level();
                this.ticksUntilNextAttack--;

                if (this.ticksUntilNextAttack <= 10 && !this.ghast.isSilent()) {
                    world.levelEvent(null, 1015, this.ghast.blockPosition(), 0);
                }

                if (this.isTimeToAttack()) {
                    double dirMod = 4D;
                    Vec3 direction = this.ghast.getViewVector(1.0F);
                    double xMod = target.getX() - (this.ghast.getX() + direction.x * dirMod);
                    double yMod = target.getY(0.5) - (0.5 + this.ghast.getY(0.5));
                    double zMod = target.getZ() - (this.ghast.getZ() + direction.z * dirMod);
                    if (!this.ghast.isSilent()) {
                        world.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                    }

                    Vec3 vec3d1 = new Vec3(xMod, yMod, zMod);
                    LargeFireball fireball = new LargeFireball(world, this.ghast, vec3d1.normalize(), this.ghast.getExplosionPower());

                    //LargeFireball fireball = new LargeFireball(world, this.ghast, xMod, yMod, zMod, this.ghast.getExplosionPower());
                    fireball.bukkitYield = (float)(fireball.explosionPower = this.ghast.getExplosionPower());
                    fireball.setPos(this.ghast.getX() + direction.x * dirMod, this.ghast.getY(0.5) + 0.5, fireball.getZ() + direction.z * dirMod);
                    world.addFreshEntity(fireball);
                    this.setAttackCooldown();
                    this.petHolder.doExhaust(ExhaustReason.COMBAT);
                }
            }
            else {
                this.setAttackCooldown();
            }

            this.ghast.setCharging(this.ticksUntilNextAttack <= 10);
        }
    }

    private static class GhastFollowTargetGoal extends AbstractPetFollowOwnerGoal {

        public GhastFollowTargetGoal(@NotNull Mob pet) {
            super(pet);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                return this.pet.distanceToSqr(target) >= 512D;
            }
            //LivingEntity target = this.pet.getTarget();
            //if (target != null && target.isAlive()) return false;
            /*
            if (target != null && !target.isAlive()) {
                this.pet.setTarget(null);
            }

            if (this.pet.passengers.contains(this.owner)) {
                return false;
            }

            MoveControl control = this.pet.getMoveControl();
            if (!control.hasWanted()) {
                return true;
            }
            double x = control.getWantedX() - this.pet.getX();
            double y = control.getWantedY() - this.pet.getY();
            double z = control.getWantedZ() - this.pet.getZ();
            double sqr = (x * x) + (y * y) + (z * z);

            return sqr < 1D || sqr > 3600D;*/
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                return this.pet.distanceToSqr(target) >= 512D;
            }

            return true;
        }

        @Override
        public void start() {//tick() {
            LivingEntity follow = this.owner;
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                follow = target;
            }

            this.pet.getNavigation().moveTo(follow, 1D);
            this.pet.lookAt(this.owner, 30f, 30f);
        }

        @Override
        public void tick() {//start() {
            LivingEntity follow = this.owner;
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                follow = target;
            }

            Location location = follow.getBukkitEntity().getLocation();
            Vector direction = location.getDirection();
            location = location.clone().subtract(direction.multiply(4));

            double x = location.getX();
            double z = location.getZ();
            double y = follow.getY() + 4D;

            // Move & Look to the owner.
            this.pet.getMoveControl().setWantedPosition(x, y, z, 1D);
            this.pet.lookAt(follow, 30f, 30f);
        }
    }
}
