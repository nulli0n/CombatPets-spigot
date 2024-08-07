package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Spider;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import java.util.UUID;

public class SpiderPet extends Spider implements PetEntity {

    public SpiderPet(@NotNull ServerLevel world) {
        super(EntityType.SPIDER, world);
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
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4f));
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this, 1D));
    }

    public static class SpiderAttackGoal extends MeleeAttackGoal {

        public SpiderAttackGoal(@NotNull Spider spider, double speed) {
            super(spider, 1D, true);
        }

        // .canUse()
        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        // .canContinueToUse()
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }
    }
}
