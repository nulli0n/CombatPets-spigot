package su.nightexpress.combatpets.nms.mc_1_21_8.goals.combat;

import net.minecraft.world.entity.monster.Zombie;
import org.jetbrains.annotations.NotNull;

public class PetZombieAttackGoal extends PetMeleeAttackGoal {

    private int raiseArmTicks;

    public PetZombieAttackGoal(@NotNull Zombie pet, double speedModifier) {
        super(pet, speedModifier);
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.pet.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();

        ++this.raiseArmTicks;

        this.pet.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackSpeedTicks() / 2);
    }
}
