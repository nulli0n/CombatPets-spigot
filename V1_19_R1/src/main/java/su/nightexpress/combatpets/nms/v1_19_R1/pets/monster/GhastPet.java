package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.AbstractPetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class GhastPet extends Ghast implements PetEntity {

    private static final Class<?>       CLASS_ATTACK  = Ghast.class.getDeclaredClasses()[3];
    private static final Constructor<?> CONSTR_ATTACK = Reflex.getConstructor(CLASS_ATTACK, Ghast.class);

    public GhastPet(@NotNull ServerLevel world) {
        super(EntityType.GHAST, world);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new GhastFollowTargetGoal(this));
        if (CONSTR_ATTACK != null) this.goalSelector.addGoal(7, (Goal) Reflex.invokeConstructor(CONSTR_ATTACK, this));
    }

    static class GhastFollowTargetGoal extends AbstractPetFollowOwnerGoal {

        public GhastFollowTargetGoal(@NotNull Mob pet) {
            super(pet);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.pet.getTarget();
            if (target != null && !target.isAlive()) {
                this.pet.setTarget(null);
            }

            if (this.pet.passengers.contains(this.owner)) {
                return false;
            }

            MoveControl controllermove = this.pet.getMoveControl();
            if (!controllermove.hasWanted()) {
                return true;
            }
            double x = controllermove.getWantedX() - this.pet.getX();
            double y = controllermove.getWantedY() - this.pet.getY();
            double z = controllermove.getWantedZ() - this.pet.getZ();
            double sqr = (x * x) + (y * y) + (z * z);

            return sqr < 1D || sqr > 3600D;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            double speed = this.pet.getAttributeValue(Attributes.MOVEMENT_SPEED);
            this.pet.getNavigation().moveTo(this.owner, speed);
        }

        @Override
        public void start() {
            LivingEntity follow = this.owner;
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                follow = target;
            }

            double speed = this.pet.getAttributeValue(Attributes.MOVEMENT_SPEED);
            double x = follow.getX() - 4D;
            double y = follow.getY() + 4D;
            double z = follow.getZ() - 4D;

            // Move & Look to the owner.
            this.pet.getMoveControl().setWantedPosition(x, y, z, speed);
            this.pet.lookAt(follow, 30f, 30f);
        }
    }
}
