package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetFollowOwnerGoal;

public class MagmaCubePet extends MagmaCube implements PetEntity {

    public MagmaCubePet(@NotNull ServerLevel world) {
        super(EntityType.MAGMA_CUBE, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new SlimePet.SlimeFollowTargetGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected int getJumpDelay() {
        return 15;
    }

    @Override
    public void push(Entity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || entity != target || entity.getBukkitEntity() == this.getHolder().getOwner()) return;

        if (!(entity instanceof IronGolem)) {
            this.dealDamage(target);
        }

        super.push(entity);
    }

    @Override
    public void playerTouch(Player entity) {
        if (entity.getBukkitEntity() == this.getHolder().getOwner()) return;

        super.playerTouch(entity);
    }

    @Override
    protected boolean isDealsDamage() {
        return true;
    }
}
