package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class BeePet extends Bee implements PetEntity {

    private static Class<?> CLASS_B;

    private static Constructor<?> CONSTR_B;

    static {
        CLASS_B = Bee.class.getDeclaredClasses()[1];

        CONSTR_B = Reflex.getConstructor(CLASS_B, Bee.class, PathfinderMob.class, Double.TYPE, Boolean.TYPE);
    }

    public BeePet(@NotNull ServerLevel world) {
        super(EntityType.BEE, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));

        Goal goalB = (Goal) Reflex.invokeConstructor(CONSTR_B, this, this, 2.4D, true);

        this.goalSelector.addGoal(0, goalB); // Attack pathfinder
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        // Re-add goal target, so our bee won't turn to passive after a single attack.
        boolean b = super.doHurtTarget(entity);
        if (b && entity instanceof LivingEntity && entity.isAlive()) {
            this.setTarget((LivingEntity) entity, TargetReason.CUSTOM, true);
        }
        return b;
    }

    @Override
    public void setHasStung(boolean flag) {
        // We don't want our bee to have stung and die
    }

    @Override
    public boolean hasStung() {
        // We don't want our bee to have stung and die
        return false;
    }
}
