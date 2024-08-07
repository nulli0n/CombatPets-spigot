package su.nightexpress.combatpets.nms.v1_19_R1.pets.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.UUID;

public class FoxPet extends Fox implements PetEntity {

    private static Class<?> CLAZZ_CROUCH_U;
    private static Class<?> CLAZZ_ATTACK_L;
    private static Class<?> CLAZZ_MOVE_B;

    private static Constructor<?> CONSTR_U;
    private static Constructor<?> CONSTR_L;
    private static Constructor<?> CONSTR_B;

    static {
        CLAZZ_CROUCH_U = Reflex.getInnerClass(Fox.class.getName(), "u");
        CLAZZ_ATTACK_L = Reflex.getInnerClass(Fox.class.getName(), "l");
        CLAZZ_MOVE_B = Reflex.getInnerClass(Fox.class.getName(), "b");

        CONSTR_U = Reflex.getConstructor(CLAZZ_CROUCH_U, Fox.class);
        CONSTR_L = Reflex.getConstructor(CLAZZ_ATTACK_L, Fox.class, Double.TYPE, Boolean.TYPE);
        CONSTR_B = Reflex.getConstructor(CLAZZ_MOVE_B, Fox.class);
    }

    public FoxPet(@NotNull ServerLevel world) {
        super(EntityType.FOX, world);
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

        Goal goalCrouch = (Goal) Reflex.invokeConstructor(CONSTR_U, this);
        Goal goalAttack = (Goal) Reflex.invokeConstructor(CONSTR_L, this, 1.2, true);
        Goal goalMove = (Goal) Reflex.invokeConstructor(CONSTR_B, this);

        this.goalSelector.addGoal(1, goalMove);
        this.goalSelector.addGoal(5, goalCrouch);
        this.goalSelector.addGoal(6, new FoxPounceGoal()); // Need, Jump
        this.goalSelector.addGoal(7, goalAttack);
        this.goalSelector.addGoal(9, new LeapAtTargetGoal(this, 0.4f));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide());
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
}
