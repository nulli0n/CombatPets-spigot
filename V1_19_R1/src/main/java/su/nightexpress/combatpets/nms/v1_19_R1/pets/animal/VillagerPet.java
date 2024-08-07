package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetAttackMeleeGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import java.util.UUID;

public class VillagerPet extends Villager implements PetEntity {

    public VillagerPet(@NotNull ServerLevel level) {
        super(EntityType.VILLAGER, level);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));
        this.goalSelector.addGoal(4, new PetAttackMeleeGoal(this));
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
    public void setChasing(boolean flag) {
        super.setChasing(false);
    }

    @Override
    public boolean isChasing() {
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
    public void thunderHit(ServerLevel worldserver, LightningBolt entitylightning) {

    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
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
