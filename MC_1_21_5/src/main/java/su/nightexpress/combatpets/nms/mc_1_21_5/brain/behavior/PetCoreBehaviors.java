package su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.behavior.impl.MoveToTarget;

public class PetCoreBehaviors {

    @NotNull
    public static Behavior<Mob> lookAtTarget() {
        return new LookAtTargetSink(45, 90);
    }

    @NotNull
    public static Behavior<Mob> moveToTarget() {
        return new MoveToTarget();
    }

    @NotNull
    public static Behavior<Mob> swim() {
        return swim(0.8F);
    }

    @NotNull
    public static Behavior<Mob> swim(float chance) {
        return new Swim<>(chance);
    }
}
