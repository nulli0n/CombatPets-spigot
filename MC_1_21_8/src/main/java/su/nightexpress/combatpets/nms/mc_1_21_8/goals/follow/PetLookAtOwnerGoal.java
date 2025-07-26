package su.nightexpress.combatpets.nms.mc_1_21_8.goals.follow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.EnumSet;

public class PetLookAtOwnerGoal extends Goal {

    protected final Mob       pet;
    protected final ActivePet holder;
    protected final float     lookDistance;
    protected final boolean   onlyHorizontal;
    protected       Entity    lookAt;
    protected       int       lookTime;

    public PetLookAtOwnerGoal(@NotNull Mob pet) {
        this.pet = pet;
        this.holder = ((PetEntity) pet).getHolder();
        this.lookDistance = 8F;
        this.onlyHorizontal = false;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pet.getTarget() != null && this.pet.getTarget().isAlive()) return false;

        double chance = 2D;

        Player owner = this.holder.getOwner();
        ItemStack item = owner.getInventory().getItemInMainHand();
        if (this.holder.getTemplate().isFood(item)) {
            chance = 100D;
        }

        if (!Rnd.chance(chance)) return false;

        this.lookAt = ((CraftPlayer) owner).getHandle();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) return false;
        if (this.pet.distanceToSqr(this.lookAt) > (double) (this.lookDistance * this.lookDistance)) return false;

        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.pet.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        if (!this.lookAt.isAlive()) return;

        double y = this.onlyHorizontal ? this.pet.getEyeY() : this.lookAt.getEyeY();
        this.pet.getLookControl().setLookAt(this.lookAt.getX(), y, this.lookAt.getZ());
        --this.lookTime;
    }
}
