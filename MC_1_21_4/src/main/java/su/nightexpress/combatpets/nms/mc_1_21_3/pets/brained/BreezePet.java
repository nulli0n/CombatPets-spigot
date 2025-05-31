package su.nightexpress.combatpets.nms.mc_1_21_3.pets.brained;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetCoreBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetFightBehaviors;
import su.nightexpress.combatpets.nms.mc_1_21_3.brain.behavior.PetIdleBehaviors;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.*;

public class BreezePet extends Breeze implements PetEntity {

    public BreezePet(@NotNull ServerLevel world) {
        super(EntityType.BREEZE, world);
    }

    @Override
    public void setGoals() {

    }

    @Override
    protected Brain.Provider<Breeze> brainProvider() {
        return PetBrain.brainProvider(this,
            Lists.newList(
                MemoryModuleType.BREEZE_JUMP_COOLDOWN,
                MemoryModuleType.BREEZE_JUMP_INHALING,
                MemoryModuleType.BREEZE_JUMP_TARGET,
                MemoryModuleType.BREEZE_LEAVING_WATER,
                MemoryModuleType.BREEZE_SHOOT,
                MemoryModuleType.BREEZE_SHOOT_CHARGING,
                MemoryModuleType.BREEZE_SHOOT_COOLDOWN,
                MemoryModuleType.BREEZE_SHOOT_RECOVERING
            )
        );
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Breeze> refreshBrain(@NotNull Breeze pet, @NotNull Brain<Breeze> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            PetCoreBehaviors.lookAtTarget(),
            PetCoreBehaviors.moveToTarget(),
            PetCoreBehaviors.swim(),
            PetFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            new RunOne<>(ImmutableList.of(Pair.of(PetIdleBehaviors.lookAtOwner(), 1))),
            //new BreezeAi.SlideToTargetSink(20, 40),
            PetIdleBehaviors.followOwner(),
            PetFightBehaviors.autoTargetAndAttack())
        );


        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            PetFightBehaviors.stopAttackIfTargetInvalid(pet),
            //PetFightBehaviors.reachTargetWhenOutOfRange(),
            new Shoot(),
            new LongJump()
        ));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("breezeBrain");
        this.getBrain().tick(level, this);
        filler.popPush("breezeActivityUpdate");
        PetAI.updateActivity(this, this.brain);
        filler.pop();
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return true;
    }

    public boolean isInvulnerableTo(ServerLevel worldserver, DamageSource damagesource) {
        return this.isRemoved()
            || this.isInvulnerable() && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damagesource.isCreativePlayer()
            || damagesource.is(DamageTypeTags.IS_FIRE) && this.fireImmune()
            || damagesource.is(DamageTypeTags.IS_FALL) && this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        return false;
    }

    private static class Shoot extends Behavior<Breeze> {

        private static final int ATTACK_RANGE_MAX_SQRT = 256;
        private static final int UNCERTAINTY_BASE = 5;
        private static final int UNCERTAINTY_MULTIPLIER = 4;
        private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
        private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
        private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
        private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

        public Shoot() {
            super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.BREEZE_SHOOT_COOLDOWN, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.BREEZE_SHOOT_CHARGING, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.BREEZE_SHOOT_RECOVERING, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.BREEZE_SHOOT, MemoryStatus.VALUE_PRESENT
            ),
                SHOOT_INITIAL_DELAY_TICKS + 1 + SHOOT_RECOVER_DELAY_TICKS
            );
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, Breeze breeze) {
            if (breeze.getPose() != Pose.STANDING) return false;

            LivingEntity target = breeze.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (target == null) return false;

            double distance = breeze.position().distanceToSqr(target.position());
            boolean inRange = /*distance > ATTACK_RANGE_MIN_SQRT && */distance < ATTACK_RANGE_MAX_SQRT;
            if (!inRange) {
                breeze.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
                return false;
            }

            return true;
        }

        @Override
        protected boolean canStillUse(ServerLevel level, Breeze breeze, long value) {
            return breeze.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && breeze.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_SHOOT);
        }

        @Override
        protected void start(ServerLevel level, Breeze breeze, long value) {
            breeze.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {
                breeze.setPose(Pose.SHOOTING);
            });
            breeze.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_CHARGING, Unit.INSTANCE, SHOOT_INITIAL_DELAY_TICKS);
            breeze.playSound(SoundEvents.BREEZE_INHALE, 1.0F, 1.0F);
        }

        @Override
        protected void stop(ServerLevel level, Breeze breeze, long value) {
            if (breeze.getPose() == Pose.SHOOTING) {
                breeze.setPose(Pose.STANDING);
            }

            breeze.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_COOLDOWN, Unit.INSTANCE, SHOOT_COOLDOWN_TICKS);
            breeze.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
        }

        @Override
        protected void tick(ServerLevel level, Breeze breeze, long value) {
            Brain<Breeze> brain = breeze.getBrain();
            LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (target == null) return;

            breeze.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

            boolean hasShootCharge = brain.getMemory(MemoryModuleType.BREEZE_SHOOT_CHARGING).isPresent();
            boolean hasShootRecover = brain.getMemory(MemoryModuleType.BREEZE_SHOOT_RECOVERING).isPresent();
            if (hasShootCharge || hasShootRecover) return;

            brain.setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_RECOVERING, Unit.INSTANCE, SHOOT_RECOVER_DELAY_TICKS);

            Vec3 viewVector = breeze.getViewVector(1.0F);
            Vec3 targetPosition = target.position().subtract(breeze.position()).normalize();
            boolean facingTarget = viewVector.dot(targetPosition) > 0.5;
            if (!facingTarget) return;

            double x = target.getX() - breeze.getX();
            double y = target.getY(target.isPassenger() ? 0.8 : 0.3) - breeze.getY(0.5);
            double z = target.getZ() - breeze.getZ();
            BreezeWindCharge windCharge = new BreezeWindCharge(breeze, level);
            breeze.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
            windCharge.shoot(x, y, z, PROJECTILE_MOVEMENT_SCALE, (float) (UNCERTAINTY_BASE - level.getDifficulty().getId() * UNCERTAINTY_MULTIPLIER));
            level.addFreshEntity(windCharge);
        }
    }

    private static class LongJump extends Behavior<Breeze> {

        private static final int           REQUIRED_AIR_BLOCKS_ABOVE     = 4;
        private static final double        MAX_LINE_OF_SIGHT_TEST_RANGE  = 50.0;
        private static final int           JUMP_COOLDOWN_TICKS           = 10;
        private static final int           JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
        private static final int           INHALING_DURATION_TICKS       = Math.round(10.0F);
        private static final float         MAX_JUMP_VELOCITY             = 1.4F;
        private static final List<Integer> ALLOWED_ANGLES                = Lists.newList(40, 55, 60, 75, 80);

        public LongJump() {
            super(Map.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.BREEZE_JUMP_COOLDOWN, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.REGISTERED,
                MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.BREEZE_SHOOT, MemoryStatus.VALUE_ABSENT
            ),
                200
            );
        }

        protected boolean checkExtraStartConditions(ServerLevel level, Breeze breeze) {
            if (!breeze.onGround() && !breeze.isInWater()) return false;
            if (breeze.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.VALUE_PRESENT)) return true;

            LivingEntity target = breeze.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (target == null) return false;
            if (tooCloseForJump(breeze, target)) return false;
            if (!canJumpFromCurrentPosition(level, breeze)) return false;

            BlockPos pos = snapToSurface(breeze, randomPointBehindTarget(target, breeze.getRandom()));
            if (pos == null) return false;
            if (!hasLineOfSight(breeze, pos.getCenter()) && !hasLineOfSight(breeze, pos.above(REQUIRED_AIR_BLOCKS_ABOVE).getCenter())) return false;

            breeze.getBrain().setMemory(MemoryModuleType.BREEZE_JUMP_TARGET, pos);
            return true;
        }

        protected boolean canStillUse(ServerLevel level, Breeze breeze, long value) {
            return breeze.getPose() != Pose.STANDING && !breeze.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_JUMP_COOLDOWN);
        }

        protected void start(ServerLevel level, Breeze breeze, long value) {
            if (breeze.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.VALUE_ABSENT)) {
                breeze.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_INHALING, Unit.INSTANCE, INHALING_DURATION_TICKS);
            }

            breeze.setPose(Pose.INHALING);
            breeze.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).ifPresent((pos) -> {
                breeze.lookAt(EntityAnchorArgument.Anchor.EYES, pos.getCenter());
            });
        }

        protected void tick(ServerLevel level, Breeze breeze, long value) {
            if (finishedInhaling(breeze)) {
                Vec3 jumpTarget = breeze.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).flatMap((pos) -> {
                    return calculateOptimalJumpVector(breeze, Vec3.atBottomCenterOf(pos));
                }).orElse(null);
                if (jumpTarget == null) {
                    breeze.setPose(Pose.STANDING);
                    return;
                }

                breeze.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.0F);
                breeze.setPose(Pose.LONG_JUMPING);
                breeze.setYRot(breeze.yBodyRot);
                breeze.setDiscardFriction(true);
                breeze.setDeltaMovement(jumpTarget);
            }
            else if (finishedJumping(breeze)) {
                breeze.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
                breeze.setPose(Pose.STANDING);
                breeze.setDiscardFriction(false);
                boolean hurted = breeze.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
                breeze.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, hurted ? JUMP_COOLDOWN_WHEN_HURT_TICKS : JUMP_COOLDOWN_TICKS);
                breeze.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 100L);
            }
        }

        protected void stop(ServerLevel level, Breeze breeze, long value) {
            if (breeze.getPose() == Pose.LONG_JUMPING || breeze.getPose() == Pose.INHALING) {
                breeze.setPose(Pose.STANDING);
            }

            breeze.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_TARGET);
            breeze.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_INHALING);
        }

        private static boolean finishedInhaling(Breeze breeze) {
            return breeze.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_INHALING).isEmpty() && breeze.getPose() == Pose.INHALING;
        }

        private static boolean finishedJumping(Breeze breeze) {
            return breeze.getPose() == Pose.LONG_JUMPING && breeze.onGround();
        }

        private static Vec3 randomPointBehindTarget(LivingEntity entity, RandomSource randomSource) {
            float var3 = entity.yHeadRot + 180.0F + (float)randomSource.nextGaussian() * 90.0F / 2.0F;
            float var4 = Mth.lerp(randomSource.nextFloat(), 4.0F, 8.0F);
            Vec3 var5 = Vec3.directionFromRotation(0.0F, var3).scale(var4);
            return entity.position().add(var5);
        }

        @Nullable
        private static BlockPos snapToSurface(LivingEntity entity, Vec3 vector) {
            ClipContext clipContext = new ClipContext(vector, vector.relative(Direction.DOWN, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
            HitResult hitResult = entity.level().clip(clipContext);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                return BlockPos.containing(hitResult.getLocation()).above();
            }

            ClipContext var4 = new ClipContext(vector, vector.relative(Direction.UP, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
            HitResult var5 = entity.level().clip(var4);
            return var5.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitResult.getLocation()).above() : null;
        }

        public static boolean hasLineOfSight(Breeze breeze, Vec3 vector) {
            Vec3 position = new Vec3(breeze.getX(), breeze.getY(), breeze.getZ());
            if (vector.distanceTo(position) > MAX_LINE_OF_SIGHT_TEST_RANGE) {
                return false;
            }

            return breeze.level().clip(new ClipContext(position, vector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, breeze)).getType() == HitResult.Type.MISS;
        }

        private static boolean tooCloseForJump(Breeze breeze, LivingEntity target) {
            return target.distanceTo(breeze) - 4.0F <= 0.0F;
        }

        private static boolean canJumpFromCurrentPosition(ServerLevel level, Breeze breeze) {
            BlockPos position = breeze.blockPosition();

            for (int step = 1; step <= REQUIRED_AIR_BLOCKS_ABOVE; ++step) {
                BlockPos relative = position.relative(Direction.UP, step);
                if (!level.getBlockState(relative).isAir() && !level.getFluidState(relative).is(FluidTags.WATER)) {
                    return false;
                }
            }

            return true;
        }

        private static Optional<Vec3> calculateOptimalJumpVector(Breeze breeze, Vec3 vector) {
            Set<Integer> angles = new HashSet<>(ALLOWED_ANGLES);

            Optional<Vec3> optional = Optional.empty();
            while (optional.isEmpty() && !angles.isEmpty()) {
                int angle = Rnd.get(angles);
                optional = LongJumpUtil.calculateJumpVectorForAngle(breeze, vector, MAX_JUMP_VELOCITY, angle, false);
                angles.remove(angle);
            }

            return optional;
        }
    }
}
