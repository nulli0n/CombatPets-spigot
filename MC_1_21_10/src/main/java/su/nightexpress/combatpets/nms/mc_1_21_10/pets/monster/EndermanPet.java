package su.nightexpress.combatpets.nms.mc_1_21_10.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

public class EndermanPet extends EnderMan implements PetEntity {

    private static final String TARGET_CHANGE_TIME = "cx";

    private boolean teleportAllowed;

    public EndermanPet(@NotNull ServerLevel world) {
        super(EntityType.ENDERMAN, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel worldserver) {
        Reflex.setFieldValue(this, TARGET_CHANGE_TIME, this.tickCount + 600); // targetChangeTime
        super.customServerAiStep(worldserver);
    }

    @Override
    public boolean hurtServer(ServerLevel worldserver, DamageSource damagesource, float f) {
        this.teleportAllowed = true;
        return super.hurtServer(worldserver, damagesource, f);
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
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
}
