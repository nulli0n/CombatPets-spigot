package su.nightexpress.combatpets.nms.mc_1_21_10.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;

public class EvokerPet extends Evoker implements PetEntity {

    private static final Constructor<?> CONSTRUCT_ATTACK_SPELL_GOAL;
    private static final Constructor<?> CONSTRUCT_CASTING_SPELL_GOAL;

    static {
        Class<?> classAttackSpellGoal = Reflex.safeInnerClass(Evoker.class.getName(), "EvokerAttackSpellGoal"); // a
        Class<?> clasCastSpellGoal = Reflex.safeInnerClass(Evoker.class.getName(), "EvokerCastingSpellGoal"); // b

        CONSTRUCT_ATTACK_SPELL_GOAL = Reflex.getConstructor(classAttackSpellGoal, Evoker.class);
        CONSTRUCT_CASTING_SPELL_GOAL = Reflex.getConstructor(clasCastSpellGoal, Evoker.class);
    }

    public EvokerPet(@NotNull ServerLevel world) {
        super(EntityType.EVOKER, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        if (CONSTRUCT_ATTACK_SPELL_GOAL != null) {
            Goal goalA = (Goal) Reflex.invokeConstructor(CONSTRUCT_ATTACK_SPELL_GOAL, this);
            this.goalSelector.addGoal(5, goalA);
        }
        if (CONSTRUCT_CASTING_SPELL_GOAL != null) {
            Goal goalB = (Goal) Reflex.invokeConstructor(CONSTRUCT_CASTING_SPELL_GOAL, this);
            this.goalSelector.addGoal(1, goalB);
        }
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
    public boolean canAttack(LivingEntity entity) {
        return entity.canBeSeenAsEnemy();
    }

    @Override
    public boolean isAlliedTo(@Nullable Team scoreboardteambase) {
        return false;
    }
}
