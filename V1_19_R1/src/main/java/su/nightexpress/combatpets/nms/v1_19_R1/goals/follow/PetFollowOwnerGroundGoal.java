package su.nightexpress.combatpets.nms.v1_19_R1.goals.follow;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.entity.Frog;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;

public class PetFollowOwnerGroundGoal extends AbstractPetFollowOwnerGoal {

    public PetFollowOwnerGroundGoal(@NotNull Mob pet) {
        super(pet);
    }

    @Override
    public void stop() {
        // Fix for stupid pet movements when they trying to reach the owner.
        //this.pet.getNavigation().moveTo(this.pet, 1);
        // Clear navigation.
        this.pet.getNavigation().stop();
        //super.stop();
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

        this.pet.getNavigation().moveTo(this.owner, speedModifier);
        super.tick();
    }
}
