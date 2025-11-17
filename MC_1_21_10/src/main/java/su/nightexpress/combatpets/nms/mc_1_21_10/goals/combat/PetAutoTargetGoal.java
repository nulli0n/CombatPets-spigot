package su.nightexpress.combatpets.nms.mc_1_21_10.goals.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_10.brain.PetAI;

import java.util.EnumSet;

public class PetAutoTargetGoal extends TargetGoal {

    private final ActivePet activePet;

    public PetAutoTargetGoal(@NotNull Mob mob) {
        super(mob, false);
        this.activePet = ((PetEntity) mob).getHolder();
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity target = PetAI.findTarget(this.mob, this.activePet);
        if (target == null || !this.mob.canAttack(target)) return false;

        this.targetMob = target;
        return true;
    }
}
