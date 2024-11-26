package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.ServerLevelAccessor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

public class VexPet extends Vex implements PetEntity {

    private static Class<?> CLASS_VEX_A;

    private static Constructor<?> CONSTR_VEX_A;

    static {
        CLASS_VEX_A = Reflex.getInnerClass(Vex.class.getName(), "VexChargeAttackGoal"); // a

        CONSTR_VEX_A = Reflex.getConstructor(CLASS_VEX_A, Vex.class);
    }

    public VexPet(@NotNull ServerLevel world) {
        super(EntityType.VEX, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        Goal goalAttack = (Goal) Reflex.invokeConstructor(CONSTR_VEX_A, this);

        this.goalSelector.addGoal(4, goalAttack);
        this.goalSelector.addGoal(8, new PathfinderFollowOwner());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    /*@Override
    protected void mobTick() {
    	if (this.getBukkitEntity().getLocation().getBlock().getLocation().add(0.0, -1.0, 0.0).getBlock().getType().isSolid()) {
        	Vec3 vec3d3 = this.getDeltaMovement();
        	this.setDeltaMovement(vec3d3.x, vec3d3.y + 0.20000000298023224, vec3d3.z);
    	}
    }*/

    class PathfinderFollowOwner extends Goal {

        public PathfinderFollowOwner() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target != null && !target.isAlive()) {
                setTarget(null);
            }

            if (!VexPet.this.getMoveControl().hasWanted() && VexPet.this.random.nextInt(7) == 0) {
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            Player p = getHolder().getOwner();
            double speed = VexPet.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            double range = 4;
            double d0 = p.getLocation().getX() - range / 2;
            double d1 = p.getLocation().getY() + range / 2;
            double d2 = p.getLocation().getZ() - range / 2;

            VexPet.this.getMoveControl().setWantedPosition(d0, d1, d2, 1D);
        }
    }
}
