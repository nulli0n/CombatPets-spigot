package su.nightexpress.combatpets.nms.v1_20.pets.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.combat.PetZombieAttackGoal;
import su.nightexpress.combatpets.nms.v1_20.goals.follow.PetFollowOwnerGoal;

import javax.annotation.Nullable;
import java.util.UUID;

public class ZombieVillagerPet extends ZombieVillager implements PetEntity {

    public ZombieVillagerPet(@NotNull ServerLevel level) {
        super(EntityType.ZOMBIE_VILLAGER, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetZombieAttackGoal(this, 1));
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
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean isUnderWaterConverting() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public void startUnderWaterConversion(int i) {

    }

    @Override
    protected void doUnderWaterConversion() {

    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    public void startConverting(@Nullable UUID uuid, int i) {

    }
}
