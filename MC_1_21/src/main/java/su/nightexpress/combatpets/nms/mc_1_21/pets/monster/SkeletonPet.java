package su.nightexpress.combatpets.nms.mc_1_21.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetBowAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21.goals.follow.PetFollowOwnerGoal;

public class SkeletonPet extends Skeleton implements PetEntity {

    private PetBowAttackGoal<Skeleton> bowGoal;
    private PetMeleeAttackGoal         meleeGoal;

    public SkeletonPet(@NotNull ServerLevel level) {
        super(EntityType.SKELETON, level);
        this.setFreezeConverting(false);
    }

    @Override
    public void setGoals() {
        this.bowGoal = new PetBowAttackGoal<>(this, 1D, 15F);
        this.meleeGoal = new PetMeleeAttackGoal(this);

        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.reassessWeaponGoal();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficultyInstance,
                                        MobSpawnType spawnType,
                                        @org.jetbrains.annotations.Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.meleeGoal == null || this.bowGoal == null) return;

        Level level = this.level();
        if (level == null || level.isClientSide) return;

        this.goalSelector.removeGoal(this.meleeGoal);
        this.goalSelector.removeGoal(this.bowGoal);
        ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        if (itemstack.is(Items.BOW)) {
            this.goalSelector.addGoal(4, this.bowGoal);
        }
        else {
            this.goalSelector.addGoal(4, this.meleeGoal);
        }
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        MC_1_21.hurtArmor(this, source, amount);
    }

    @Override
    public boolean isFreezeConverting() {
        return false;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }
}
