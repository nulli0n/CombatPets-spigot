package su.nightexpress.combatpets.nms.mc_1_21_10.brain.behavior.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class MoveToTarget extends Behavior<Mob> {

    private static final int      MAX_COOLDOWN_BEFORE_RETRYING = 40;

    private int      remainingCooldown;
    @Nullable
    private Path     path;
    @Nullable
    private BlockPos lastTargetPos;
    private float    speedModifier;

    public MoveToTarget() {
        this(150, 250);
    }

    public MoveToTarget(int minDuration, int maxDuration) {
        super(
            ImmutableMap.of(
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED,
                MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT
            ),
            minDuration, maxDuration
        );
    }

    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob) {
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        }
        else {
            Brain<?> brain = mob.getBrain();
            WalkTarget walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean reachedTarget = this.reachedTarget(mob, walkTarget);
            if (!reachedTarget && this.tryComputePath(mob, walkTarget, level.getGameTime())) {
                this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
                return true;
            }
            else {
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                if (reachedTarget) {
                    brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }
                return false;
            }
        }
    }

    protected boolean canStillUse(ServerLevel level, Mob mob, long var2) {
        if (this.path != null && this.lastTargetPos != null) {
            Optional<WalkTarget> walkTarget = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            PathNavigation navigation = mob.getNavigation();
            return !navigation.isDone() && walkTarget.isPresent() && !this.reachedTarget(mob, walkTarget.get());
        }
        else {
            mob.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
            mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            return false;
        }
    }

    protected void stop(ServerLevel level, Mob mob, long var2) {
        if (mob.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(mob, mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && mob.getNavigation().isStuck()) {
            this.remainingCooldown = level.getRandom().nextInt(MAX_COOLDOWN_BEFORE_RETRYING);
        }
        mob.getNavigation().stop();
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        mob.getBrain().eraseMemory(MemoryModuleType.PATH);
        this.path = null;
    }

    protected void start(ServerLevel var0, Mob var1, long var2) {
        var1.getBrain().setMemory(MemoryModuleType.PATH, this.path);
        var1.getNavigation().moveTo(this.path, this.speedModifier);
    }

    protected void tick(ServerLevel var0, Mob var1, long var2) {
        Path var4 = var1.getNavigation().getPath();
        Brain<?> var5 = var1.getBrain();
        if (this.path != var4) {
            this.path = var4;
            var5.setMemory(MemoryModuleType.PATH, var4);
        }

        if (var4 != null && this.lastTargetPos != null) {
            WalkTarget var6 = var5.getMemory(MemoryModuleType.WALK_TARGET).get();
            if (var6.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(var1, var6, var0.getGameTime())) {
                this.lastTargetPos = var6.getTarget().currentBlockPosition();
                this.start(var0, var1, var2);
            }

        }
    }

    private boolean tryComputePath(Mob var0, WalkTarget var1, long var2) {
        BlockPos var4 = var1.getTarget().currentBlockPosition();
        this.path = var0.getNavigation().createPath(var4, 0);
        this.speedModifier = var1.getSpeedModifier();
        Brain<?> var5 = var0.getBrain();
        if (this.reachedTarget(var0, var1)) {
            var5.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        else {
            boolean var6 = this.path != null && this.path.canReach();
            if (var6) {
                var5.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }
            else if (!var5.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                var5.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, var2);
            }

            if (this.path != null) {
                return true;
            }

            Vec3 var7 = DefaultRandomPos.getPosTowards((PathfinderMob) var0, 10, 7, Vec3.atBottomCenterOf(var4), 1.5707963705062866D);
            if (var7 != null) {
                this.path = var0.getNavigation().createPath(var7.x, var7.y, var7.z, 0);
                return this.path != null;
            }
        }

        return false;
    }

    private boolean reachedTarget(Mob var0, WalkTarget var1) {
        return var1.getTarget().currentBlockPosition().distManhattan(var0.blockPosition()) <= var1.getCloseEnoughDist();
    }
}
