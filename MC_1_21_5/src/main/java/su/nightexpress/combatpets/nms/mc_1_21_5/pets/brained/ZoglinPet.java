package su.nightexpress.combatpets.nms.mc_1_21_5.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetBrain;

public class ZoglinPet extends Zoglin implements PetEntity {

    public ZoglinPet(@NotNull ServerLevel level) {
        super(EntityType.ZOGLIN, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Zoglin> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PetBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("zoglinBrain");
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
    protected void playAngrySound() {

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }
}
