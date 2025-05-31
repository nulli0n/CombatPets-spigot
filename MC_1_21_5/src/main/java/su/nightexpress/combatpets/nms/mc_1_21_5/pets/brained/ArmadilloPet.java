package su.nightexpress.combatpets.nms.mc_1_21_5.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetBrain;

public class ArmadilloPet extends Armadillo implements PetEntity {

    public ArmadilloPet(@NotNull ServerLevel level) {
        super(EntityType.ARMADILLO, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PetBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("armadilloBrain");
        ((Brain<LivingEntity>)this.brain).tick(level, this);
        filler.pop();
        filler.push("armadilloActivityUpdate");
        PetAI.updateActivity(this, this.brain);
        filler.pop();
    }

//    @Override
//    public boolean hurtServer(ServerLevel level, DamageSource damagesource, float damage) {
//        return PetBrain.hurt(this, level, damagesource, damage);
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
    public boolean isScaredBy(LivingEntity entityliving) {
        return false;
    }

    @Override
    public void rollUp() {

    }
}
