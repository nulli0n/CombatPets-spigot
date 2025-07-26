package su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Frog;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.random.Rnd;

public class PetFollowOwnerGoal extends AbstractPetFollowOwnerGoal {

    public PetFollowOwnerGoal(@NotNull Mob pet) {
        super(pet);
    }

    @Override
    public void stop() {
        // Fix for stupid pet movements when they trying to reach the owner.
        this.pet.getNavigation().moveTo(this.pet, 1D);
        // Clear navigation.
        this.pet.getNavigation().stop();
    }

    @Override
    public void tick() {
        double speedModifier = this.pet.isInWater() ? 2D : 1D;

        if (this.pet instanceof Slime) {
            this.pet.lookAt(this.owner, 10.0F, 10.0F);
            Reflex.invokeMethod(SLIME_ROTATE, this.pet.getMoveControl(), this.pet.getYRot(), true);
            this.pet.getMoveControl().setWantedPosition(owner.getX(), owner.getY(), owner.getZ(), speedModifier);
            return;
        }
        else if (this.pet instanceof Frog) {
            this.pet.lookAt(this.owner, 10.0F, 10.0F);
            this.pet.getMoveControl().setWantedPosition(owner.getX(), owner.getY(), owner.getZ(), speedModifier);
            return;
        }
        else if (this.pet instanceof Blaze) {
            this.pet.getNavigation().moveTo(owner.getX(), owner.getY() + 0.5, owner.getZ(), speedModifier);
            return;
        }

        Vec3 target = this.owner.position();

        double distance = this.pet.distanceToSqr(this.owner);
        double reach = this.getReach(this.owner);
        if (distance <= reach) {
            double xOff = Rnd.getDouble(0.25);
            target = new Vec3(this.owner.getBlockX() + xOff, this.owner.getBlockY(), this.owner.getBlockZ() + xOff);
        }

        this.pet.getNavigation().moveTo(target.x, target.y, target.z, speedModifier);
        super.tick();
    }
}
