package su.nightexpress.combatpets.nms.mc_1_21_3.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetFollowOwnerGoal;

public class CatPet extends Cat implements PetEntity {

    public CatPet(@NotNull ServerLevel world) {
        super(EntityType.CAT, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
        this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }
}
