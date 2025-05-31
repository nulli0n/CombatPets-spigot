package su.nightexpress.combatpets.nms.mc_1_21_5.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetBrain;

public class SnifferPet extends Sniffer implements PetEntity {

    public SnifferPet(@NotNull ServerLevel world) {
        super(EntityType.SNIFFER, world);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Sniffer> brainProvider() {
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
        filler.push("snifferBrain");
        this.getBrain().tick(level, this);
        filler.popPush("snifferActivityUpdate");
        PetAI.updateActivity(this, this.brain);
        filler.pop();
    }

//    @Override
//    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float damage) {
//        return PetBrain.hurt(this, level, damageSource, damage);
//    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
    }

    @Override
    public Sniffer storeExploredPosition(BlockPos blockposition) {
        return this;
    }
}
