package su.nightexpress.combatpets.nms.v1_19_R1.goals.follow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PetFollowOwnerFlyGoal extends AbstractPetFollowOwnerGoal {

    public PetFollowOwnerFlyGoal(@NotNull Mob pet) {
        super(pet);
    }

    @Override
    public void stop() {
        //super.stop();
    }

    @Override
    public void tick() {
        if (this.pet instanceof Bat) {
            LivingEntity target = this.owner;
            BlockPos d = new BlockPos(target.getBlockX(), target.getBlockY() + 2, target.getBlockZ());

            double d0 = (double) d.getX() + 0.15 - this.pet.getX();
            double d1 = (double) d.getY() + 0.1 - this.pet.getY();
            double d2 = (double) d.getZ() + 0.15 - this.pet.getZ();
            Vec3 vec3d = this.pet.getDeltaMovement();
            Vec3 vec3d1 = vec3d.add((Math.signum(d0) * 0.5 - vec3d.x) * 0.1, (Math.signum(d1) * 0.7 - vec3d.y) * 0.1, (Math.signum(d2) * 0.5 - vec3d.z) * 0.1);
            this.pet.setDeltaMovement(vec3d1);
            this.pet.lookAt(this.owner, 10f, 10f);
        }
        else {
            LivingEntity target = this.pet.getTarget();
            //if (target == null || !target.isAlive()) {
                //this.pet.setTarget(target = this.owner, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, false);
            //}
            if (this.pet.distanceToSqr(this.owner) > 2) {
                double speed = this.pet.getAttributeValue(Attributes.MOVEMENT_SPEED);
                this.pet.getMoveControl().setWantedPosition(owner.getX(), owner.getY(), owner.getZ(), speed);
            }
        }
        super.tick();
    }
}
