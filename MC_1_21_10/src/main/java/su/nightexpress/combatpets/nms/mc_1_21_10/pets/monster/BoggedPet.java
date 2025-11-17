package su.nightexpress.combatpets.nms.mc_1_21_10.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetBowAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.follow.PetFollowOwnerGoal;

public class BoggedPet extends Bogged implements PetEntity {

    private PetBowAttackGoal<Bogged> bowGoal;
    private PetMeleeAttackGoal       meleeGoal;

    public BoggedPet(ServerLevel level) {
        super(EntityType.BOGGED, level);
    }

    @Override
    public void setGoals() {
        this.bowGoal = new PetBowAttackGoal<>(this, 1D, 15F);
        this.meleeGoal = new PetMeleeAttackGoal(this);

        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.reassessWeaponGoal();
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
    public void reassessWeaponGoal() {
        if (this.meleeGoal == null || this.bowGoal == null) return;

        Level level = this.level();
        if (level == null || level.isClientSide()) return;

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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void shear(ServerLevel worldserver, SoundSource soundcategory, ItemStack itemstack) {

    }
}
