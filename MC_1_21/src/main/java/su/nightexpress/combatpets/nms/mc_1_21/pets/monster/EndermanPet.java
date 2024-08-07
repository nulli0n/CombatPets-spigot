package su.nightexpress.combatpets.nms.mc_1_21.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

public class EndermanPet extends EnderMan implements PetEntity {

    private boolean teleportAllowed;

    public EndermanPet(@NotNull ServerLevel world) {
        super(EntityType.ENDERMAN, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep() {
        Reflex.setFieldValue(this, "ch", this.tickCount + 600); // targetChangeTime
        super.customServerAiStep();
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        MC_1_21.hurtArmor(this, source, amount);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean teleport() {
        if (this.teleportAllowed) {
            this.teleportAllowed = false;
            return super.teleport();
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        this.teleportAllowed = true;
        return super.hurt(damagesource, f);
    }
}
