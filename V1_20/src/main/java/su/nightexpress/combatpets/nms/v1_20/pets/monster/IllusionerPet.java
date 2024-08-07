package su.nightexpress.combatpets.nms.v1_20.pets.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetBowAttackGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;

public class IllusionerPet extends Illusioner implements PetEntity {

    private static final Constructor<?> CONSTRUCT_BLIND_SPELL_GOAL;
    private static final Constructor<?> CONSTRUCT_MIRROR_SPELL_GOAL;

    static {
        Class<?> blindSpellGoalClass = Illusioner.class.getDeclaredClasses()[0];
        Class<?> mirrorSpellGoalClass = Illusioner.class.getDeclaredClasses()[1];

        CONSTRUCT_BLIND_SPELL_GOAL = Reflex.getConstructor(blindSpellGoalClass, Illusioner.class);
        CONSTRUCT_MIRROR_SPELL_GOAL = Reflex.getConstructor(mirrorSpellGoalClass, Illusioner.class);
    }

    public IllusionerPet(@NotNull ServerLevel world) {
        super(EntityType.ILLUSIONER, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        Goal blindSpellGoal = (Goal) Reflex.invokeConstructor(CONSTRUCT_BLIND_SPELL_GOAL, this);
        Goal mirrorSpellGoal = (Goal) Reflex.invokeConstructor(CONSTRUCT_MIRROR_SPELL_GOAL, this);

        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(4, mirrorSpellGoal);
        this.goalSelector.addGoal(5, blindSpellGoal);
        this.goalSelector.addGoal(6, new PetBowAttackGoal<>(this, 0.5, 15.0f));
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
    public boolean isAlliedTo(Entity entity) {
        return false;
    }
}
