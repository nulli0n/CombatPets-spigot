package su.nightexpress.combatpets.nms.mc_1_21_3.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetBrain;

public class HoglinPet extends Hoglin implements PetEntity {

    public HoglinPet(@NotNull ServerLevel level) {
        super(EntityType.HOGLIN, level);
        this.setImmuneToZombification(true);
        this.cannotBeHunted = true;
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Hoglin> brainProvider() {
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
        filler.push("hoglinBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        PetAI.updateActivity(this, this.brain);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity entity) {
        if (entity instanceof LivingEntity target) {
            this.handleEntityEvent((byte) 4);
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
            return HoglinBase.hurtAndThrowTarget(level, this, target);
        }
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return PetBrain.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
        //return PetBrain.hurt(this, level, fixed, damage);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void ageBoundaryReached() {

    }

    @Override
    public boolean canBeHunted() {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }
}
