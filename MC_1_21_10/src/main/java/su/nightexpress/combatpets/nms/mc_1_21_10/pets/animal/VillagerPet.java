package su.nightexpress.combatpets.nms.mc_1_21_10.pets.animal;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_10.goals.follow.PetFollowOwnerGoal;

public class VillagerPet extends Villager implements PetEntity {

    public VillagerPet(@NotNull ServerLevel level) {
        super(EntityType.VILLAGER, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(4, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.brainProvider().makeBrain(dynamic);
    }

    public void refreshBrain(ServerLevel worldserver) {
        Brain<Villager> behaviorcontroller = this.getBrain();
        behaviorcontroller.stopAll(worldserver, this);
        this.brain = behaviorcontroller.copyWithoutBehaviors();
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    protected void ageBoundaryReached() {

    }

    @Override
    public boolean canRestock() {
        return false;
    }

    @Override
    public void restock() {

    }

    @Override
    public boolean shouldRestock() {
        return false;
    }

    @Override
    public void releaseAllPois() {

    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public void setOffers(MerchantOffers merchantrecipelist) {
        this.offers = null;
    }

    @Override
    public void increaseMerchantCareer() {

    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        PetBrain.thunderHit(this, level, bolt);
    }

    @Override
    public boolean wantsToPickUp(ServerLevel worldserver, ItemStack itemstack) {
        return false;
    }

    @Override
    protected void updateTrades() {

    }

    @Override
    public void gossip(ServerLevel worldserver, Villager entityvillager, long i) {

    }

    @Override
    public boolean wantsToSpawnGolem(long i) {
        return false;
    }

    @Override
    public void startSleeping(BlockPos blockposition) {

    }

    @Override
    public void stopSleeping() {

    }
}
