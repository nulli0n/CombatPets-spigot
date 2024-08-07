package su.nightexpress.combatpets.nms.v1_20.pets.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;

public class EvokerPet extends Evoker implements PetEntity {

    private static final Constructor<?> CONSTRUCT_ATTACK_SPELL_GOAL;
    private static final Constructor<?> CONSTRUCT_CASTING_SPELL_GOAL;

    static {
        Class<?> classAttackSpellGoal = Reflex.getInnerClass(Evoker.class.getName(), "a");
        Class<?> clasCastSpellGoal = Reflex.getInnerClass(Evoker.class.getName(), "b");

        CONSTRUCT_ATTACK_SPELL_GOAL = classAttackSpellGoal == null ? null : Reflex.getConstructor(classAttackSpellGoal, Evoker.class);
        CONSTRUCT_CASTING_SPELL_GOAL = clasCastSpellGoal == null ? null : Reflex.getConstructor(clasCastSpellGoal, Evoker.class);
    }

    public EvokerPet(@NotNull ServerLevel world) {
        super(EntityType.EVOKER, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData,
                                        @org.jetbrains.annotations.Nullable CompoundTag tag) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        V1_20.hurtArmor(this, source, amount);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return entity.canBeSeenAsEnemy();
    }

    public boolean isAlliedTo(Entity entity) {
        return this.isAlliedTo(entity.getTeam());
    }
}
