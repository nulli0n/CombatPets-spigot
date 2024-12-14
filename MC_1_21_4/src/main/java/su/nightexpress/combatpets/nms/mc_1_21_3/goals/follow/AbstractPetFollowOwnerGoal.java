package su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.random.Rnd;

import java.lang.reflect.Method;
import java.util.EnumSet;

public abstract class AbstractPetFollowOwnerGoal extends Goal {

    protected Mob          pet;
    protected ActivePet    petHolder;
    protected ServerPlayer owner;

    public static final Method SLIME_ROTATE;

    static {
        SLIME_ROTATE = Reflex.getMethod(Slime.class.getDeclaredClasses()[0], "a", float.class, boolean.class);
    }

    public AbstractPetFollowOwnerGoal(@NotNull Mob pet) {
        this.pet = pet;
        this.petHolder = ((PetEntity) pet).getHolder();
        this.owner = ((CraftPlayer) this.petHolder.getOwner()).getHandle();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected double getReach(@NotNull LivingEntity target) {
        return (Math.pow(this.pet.getBbWidth() * 2F, 2) + target.getBbWidth()) * 1.5;
    }

    public boolean isTargettedToEnemy() {
        LivingEntity target = this.pet.getTarget();
        if (target != null && !target.isAlive()) {
            this.pet.setTarget(null);
            return false;
        }

        return target != null && !target.equals(this.owner) && target.isAlive();
    }

    @Override
    public boolean canUse() {
        if (this.isTargettedToEnemy() || this.owner == null || !this.owner.isAlive()) return false;

        if (this.pet.passengers.contains(this.owner)) {
            return false;
        }

        if (!this.pet.level().equals(this.owner.level()) || this.pet.distanceTo(this.owner) > 32D) {
            this.moveToOwner();
            return false;
        }

        double distance = this.pet.distanceToSqr(this.owner);
        double reach = this.getReach(this.owner);

        return distance > reach || Rnd.nextFloat() < 0.05;
    }

    public void moveToOwner() {
        this.pet.setTarget(null);
        this.pet.getBukkitEntity().teleport(this.owner.getBukkitEntity());
    }
}
