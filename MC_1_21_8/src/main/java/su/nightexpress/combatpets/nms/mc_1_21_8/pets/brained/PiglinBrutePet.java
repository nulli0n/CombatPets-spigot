package su.nightexpress.combatpets.nms.mc_1_21_8.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_8.brain.PetBrain;

public class PiglinBrutePet extends PiglinBrute implements PetEntity {

    public PiglinBrutePet(@NotNull ServerLevel level) {
        super(EntityType.PIGLIN_BRUTE, level);
        this.setImmuneToZombification(true);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<PiglinBrute> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PetBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("piglinBruteBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        PetAI.updateActivity(this, this.brain);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return PetBrain.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
        //return PetBrain.hurt(this, level, fixed, damage);
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean canHunt() {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void playAngrySound() {

    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        return false;
    }
}
