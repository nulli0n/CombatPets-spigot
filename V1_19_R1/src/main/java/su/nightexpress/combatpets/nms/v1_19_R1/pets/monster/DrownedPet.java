package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class DrownedPet extends Drowned implements PetEntity {

    private static final Class<?> CLAZZ_GOAL_F = Drowned.class.getDeclaredClasses()[2];
    private static final Class<?> CLAZZ_GOAL_A = Drowned.class.getDeclaredClasses()[3];

    private static final Constructor<?> CONSTR_F = Reflex.getConstructor(CLAZZ_GOAL_F, RangedAttackMob.class, Double.TYPE, Integer.TYPE, Float.TYPE);
    private static final Constructor<?> CONSTR_A = Reflex.getConstructor(CLAZZ_GOAL_A, Drowned.class, Double.TYPE, Boolean.TYPE);

    public DrownedPet(@NotNull ServerLevel world) {
        super(EntityType.DROWNED, world);
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
        if (CONSTR_F != null) {
            RangedAttackGoal goal = (RangedAttackGoal) Reflex.invokeConstructor(CONSTR_F, this, 1D, 40, 10F);
            if (goal != null) this.goalSelector.addGoal(2, goal);
        }
        if (CONSTR_A != null) {
            ZombieAttackGoal goal = (ZombieAttackGoal) Reflex.invokeConstructor(CONSTR_A, this, 1D, false);
            if (goal != null) this.goalSelector.addGoal(2, goal);
        }
    }

    @Override
    public boolean okTarget(@Nullable LivingEntity var0) {
        return true;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }
}
