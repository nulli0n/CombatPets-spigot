package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Illusioner;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class IllusionerPet extends Illusioner implements PetEntity {

    private static Class<?> CLASS_ILLUSIONER_A;
    private static Class<?> CLASS_ILLUSIONER_B;

    private static Constructor<?> CONST_ILLUSIONER_A;
    private static Constructor<?> CONST_ILLUSIONER_B;

    static {
        CLASS_ILLUSIONER_A = Illusioner.class.getDeclaredClasses()[0];
        CLASS_ILLUSIONER_B = Illusioner.class.getDeclaredClasses()[1];

        CONST_ILLUSIONER_A = Reflex.getConstructor(CLASS_ILLUSIONER_A, Illusioner.class);
        CONST_ILLUSIONER_B = Reflex.getConstructor(CLASS_ILLUSIONER_B, Illusioner.class);
    }

    public IllusionerPet(@NotNull ServerLevel world) {
        super(EntityType.ILLUSIONER, world);
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

        Goal goalA = (Goal) Reflex.invokeConstructor(CONST_ILLUSIONER_A, this);
        Goal goalB = (Goal) Reflex.invokeConstructor(CONST_ILLUSIONER_B, this);

        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(4, goalB);
        this.goalSelector.addGoal(5, goalA);
        this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5, 20, 15.0f));
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return false;
    }
}
