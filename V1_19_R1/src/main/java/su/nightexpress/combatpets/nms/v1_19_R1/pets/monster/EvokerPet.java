package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Evoker;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class EvokerPet extends Evoker implements PetEntity {

    private static final Constructor<?> CONSTR_A;
    private static final Constructor<?> CONSTR_B;

    static {
        Class<?> CLAZZ_A = Reflex.getInnerClass(Evoker.class.getName(), "a");
        Class<?> CLAZZ_B = Reflex.getInnerClass(Evoker.class.getName(), "b");

        CONSTR_A = CLAZZ_A == null ? null : Reflex.getConstructor(CLAZZ_A, Evoker.class);
        CONSTR_B = CLAZZ_B == null ? null : Reflex.getConstructor(CLAZZ_B, Evoker.class);
    }

    public EvokerPet(@NotNull ServerLevel world) {
        super(EntityType.EVOKER, world);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));

        if (CONSTR_A != null) {
            Goal goalA = (Goal) Reflex.invokeConstructor(CONSTR_A, this);
            this.goalSelector.addGoal(5, goalA);
        }
        if (CONSTR_B != null) {
            Goal goalB = (Goal) Reflex.invokeConstructor(CONSTR_B, this);
            this.goalSelector.addGoal(1, goalB);
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return entity.canBeSeenAsEnemy();
    }

    public boolean isAlliedTo(Entity entity) {
        return this.isAlliedTo(entity.getTeam());
    }
}
