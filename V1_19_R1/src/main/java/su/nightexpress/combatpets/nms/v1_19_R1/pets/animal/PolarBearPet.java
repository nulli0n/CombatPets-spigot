package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.UUID;

public class PolarBearPet extends PolarBear implements PetEntity {

    private static Constructor<?> CONSTR_C;

    static {
        Class<?> clazzC = Reflex.getInnerClass(PolarBear.class.getName(), "c");

        CONSTR_C = Reflex.getConstructor(clazzC, PolarBear.class);
    }

    public PolarBearPet(@NotNull ServerLevel level) {
        super(EntityType.POLAR_BEAR, level);
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
        this.goalSelector.addGoal(1, (Goal) Reflex.invokeConstructor(CONSTR_C, this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public void setInLove(@Nullable Player entityhuman) {

    }

    @Override
    public void setInLoveTime(int i) {

    }

    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public boolean canMate(Animal entityanimal) {
        return false;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel worldserver, Animal entityanimal) {

    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return false;
    }
}
