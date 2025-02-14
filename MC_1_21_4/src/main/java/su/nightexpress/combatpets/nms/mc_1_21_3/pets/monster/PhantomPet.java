package su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.random.Rnd;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

public class PhantomPet extends Phantom implements PetEntity {

    private static final String MOVE_TARGET_POINT = "d";
    private static final String ATTACK_FASE = "bY";
    private static final String ANCHOR_POINT = "bX";

    private LivingEntity lastTarget;

    private static final Constructor<?> CONSTR_I;
    private static final Constructor<?> CONSTR_C;

    static {
        Class<?> CLASS_I = Reflex.getInnerClass(Phantom.class.getName(), "PhantomSweepAttackGoal"); // i
        Class<?> CLASS_C = Reflex.getInnerClass(Phantom.class.getName(), "PhantomAttackStrategyGoal"); // c

        CONSTR_I = CLASS_I == null ? null : Reflex.getConstructor(CLASS_I, Phantom.class);
        CONSTR_C = CLASS_C == null ? null : Reflex.getConstructor(CLASS_C, Phantom.class);
    }

    public PhantomPet(@NotNull ServerLevel world) {
        super(EntityType.PHANTOM, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        if (CONSTR_C != null) {
            this.goalSelector.addGoal(1, (Goal) Reflex.invokeConstructor(CONSTR_C, this));
        }
        if (CONSTR_I != null) {
            this.goalSelector.addGoal(2, (Goal) Reflex.invokeConstructor(CONSTR_I, this));
        }
        this.goalSelector.addGoal(3, new PathfinderFollowOwner());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public boolean setTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (super.setTarget(entityliving, reason, fireEvent)) {
            if (entityliving != null) this.lastTarget = entityliving;
            return true;
        }
        return false;
    }

    abstract class PhantomMoveTargetGoal extends Goal {

        protected Class<?> phaseClass;
        protected Object[] phaseValues;

        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));

            Object phase = this.getAttackPhase();

            this.phaseClass = phase.getClass(); // Get private enum class
            this.phaseValues = phaseClass.getEnumConstants(); // Get enum values
        }

        protected boolean touchingTarget() {
            Vec3 moveTargetPoint = (Vec3) Reflex.getFieldValue(PhantomPet.this, MOVE_TARGET_POINT);
            if (moveTargetPoint == null) return false;

            return moveTargetPoint.distanceToSqr(PhantomPet.this.getX(), PhantomPet.this.getY(), PhantomPet.this.getZ()) < 4.0;
        }

        protected Object getAttackPhase() {
            return Reflex.getFieldValue(PhantomPet.this, ATTACK_FASE);
            // 0 - CIRCLE
            // 1 - SWOOP
        }

//        protected void setAttackPhase(int i) {
//            Reflex.setFieldValue(PhantomPet.this, ATTACK_FASE, phaseValues[i]);
//        }
    }

    final class PathfinderFollowOwner extends PhantomMoveTargetGoal {

        private float distance;
        private float height;
        private float clockWise;
        private float angle;

        private PathfinderFollowOwner() {
        }

        @Override
        public boolean canUse() {
            return PhantomPet.this.getTarget() == null || getAttackPhase() == phaseValues[0];
        }

        @Override
        public void start() {
            this.distance = 5.0f + PhantomPet.this.random.nextFloat() * 10.0f;
            this.height = -4.0f + PhantomPet.this.random.nextFloat() * 9.0f;
            this.clockWise = (PhantomPet.this.random.nextBoolean() ? 1.0f : -1.0f);
            this.selectNext();
        }

        @Override
        public void tick() {
            if (lastTarget != null && lastTarget.isAlive()) {
                setTarget(lastTarget, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
            }

            if (PhantomPet.this.random.nextInt(350) == 0) {
                this.height = -4.0f + PhantomPet.this.random.nextFloat() * 9.0f;
            }

            if (PhantomPet.this.random.nextInt(250) == 0) {
                ++this.distance;
                if (this.distance > 15.0f) {
                    this.distance = 5.0f;
                    this.clockWise = -this.clockWise;
                }
            }

            if (PhantomPet.this.random.nextInt(450) == 0) {
                this.angle = PhantomPet.this.random.nextFloat() * 2.0f * 3.1415927f;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            Vec3 moveTargetPoint = (Vec3) Reflex.getFieldValue(PhantomPet.this, MOVE_TARGET_POINT);
            if (moveTargetPoint == null) return;

            if (moveTargetPoint.y < PhantomPet.this.getY() && !PhantomPet.this.level().isEmptyBlock(PhantomPet.this.blockPosition().below(1))) {
                this.height = Math.max(1.0f, this.height);
                this.selectNext();
            }
            if (moveTargetPoint.y > PhantomPet.this.getY() && !PhantomPet.this.level().isEmptyBlock(PhantomPet.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0f, this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            LivingEntity follow = getTarget();

            BlockPos anchor = (BlockPos) Reflex.getFieldValue(PhantomPet.this, ANCHOR_POINT);
            if (anchor == null) return;

            if (BlockPos.ZERO.equals(anchor)) {
                Reflex.setFieldValue(PhantomPet.this, ANCHOR_POINT, new BlockPos(anchor = PhantomPet.this.blockPosition()));
            }

            Vec3 point;
            if (follow == null || !follow.isAlive()) {
                follow = ((CraftPlayer) PhantomPet.this.getHolder().getOwner()).getHandle();
                point = new Vec3(follow.getX() + Rnd.getDouble(-2, 2), follow.getY() + 2.5, follow.getZ() + Rnd.getDouble(-2, 2));
            }
            else {
                this.angle += this.clockWise * 15.0F * 0.017453292F;
                point = Vec3.atLowerCornerOf(anchor).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
            }
            Reflex.setFieldValue(PhantomPet.this, MOVE_TARGET_POINT, point);
        }
    }
}
