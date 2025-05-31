package su.nightexpress.combatpets.nms.mc_1_21_3.pets.brained;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetBrain;

import javax.annotation.Nullable;

public class WardenPet extends Warden implements PetEntity {

    public WardenPet(@NotNull ServerLevel level) {
        super(EntityType.WARDEN, level);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Warden> brainProvider() {
        return PetBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PetBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public void increaseAngerAt(@Nullable Entity entity) {
        if (entity == null || !this.getOwnerId().equals(entity.getUUID())) {
            super.increaseAngerAt(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.getOwnerId().equals(entity.getUUID())) {
            if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
                this.increaseAngerAt(entity);
                WardenAi.setDisturbanceLocation(this, entity.blockPosition());
            }
        }
        entity.push(this);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return PetBrain.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
        //return PetBrain.hurt(this, level, fixed, damage);
    }

//    @Override
//    public boolean hurtServer(ServerLevel worldserver, DamageSource damagesource, float damage) {
//        boolean flag = PetBrain.hurt(this, worldserver, damagesource, damage);
//        if (!this.level().isClientSide && flag) {
//            Entity entity = damagesource.getEntity();
//            if (entity != null && !this.getHolder().getOwner().getUniqueId().equals(entity.getUUID())) {
//                this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
//                if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity target) {
//                    if (damagesource.isDirect() || this.closerThan(target, 5.0)) {
//                        this.setAttackTarget(target);
//                    }
//                }
//            }
//        }
//
//        return flag;
//    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("wardenBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        PetAI.updateActivity(this, this.brain);
    }
}
