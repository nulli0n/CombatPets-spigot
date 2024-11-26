package su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;

import java.util.EnumSet;

public abstract class AbstractPetAttackGoal extends Goal {

    protected final Mob       pet;
    protected final ActivePet petHolder;
    protected final double    speedModifier;

    protected int ticksUntilNextAttack;

    public AbstractPetAttackGoal(@NotNull Mob pet) {
        this(pet, 1D);
    }

    public AbstractPetAttackGoal(@NotNull Mob pet, double speedModifier) {
        this.pet = pet;
        this.petHolder = ((PetEntity) pet).getHolder();
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void setAttackCooldown() {
        this.setAttackCooldown(1D);
    }

    public void setAttackCooldown(double fractal) {
        this.ticksUntilNextAttack = (int) (fractal * this.getAttackSpeedTicks());
    }

    public boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    public int getTicksUntilNextAttack() {
        return ticksUntilNextAttack;
    }

    public double getSpeedModifier() {
        return this.pet.isInWater() ? 2D : this.speedModifier;
    }

    public double getAttackSpeed() {
        AttributeInstance instance = pet.getAttribute(Attributes.ATTACK_SPEED);
        return instance == null ? 0.5 : instance.getBaseValue();
    }

    public int getAttackSpeedTicks() {
        return (int) Math.max(1, 20D / this.getAttackSpeed());
    }
}
