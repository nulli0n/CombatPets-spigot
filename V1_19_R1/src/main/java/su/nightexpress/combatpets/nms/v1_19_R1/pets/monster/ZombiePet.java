package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;

import java.util.UUID;

public class ZombiePet extends Zombie implements PetEntity {

    public ZombiePet(@NotNull ServerLevel world) {
        super(EntityType.ZOMBIE, world);
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetHurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGroundGoal(this));
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1D, false));
        //this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, getHolder().getOwner().getUniqueId());
    }

	/*@Override
	public Brain<Zombie> getBrain() {
		return (Brain<Zombie>) super.getBrain();
	}

	@Override
	protected Brain.Provider<Zombie> brainProvider() {
		return PetBrain.brainProvider(this);
	}

	@Override
	protected Brain<Zombie> makeBrain(Dynamic<?> dynamic) {
		return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
	}

	public Brain<Zombie> refreshBrain(@NotNull ZombiePet pet, @NotNull Brain<Zombie> behaviorcontroller) {
		System.out.println("refresh brain");

		behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of((BehaviorControl<? super Mob>) new LookAtTargetSink(45, 90), (BehaviorControl<? super Mob>) new MoveToTargetSink(), (StopBeingAngryIfTargetDead.create())));
		behaviorcontroller.addActivity(Activity.IDLE, 10, ImmutableList.of(new RunOne<>(ImmutableList.of(Pair.of(SetEntityLookTarget.create(e -> {
			return e != null && e.isAlive() && !e.getUUID().equals(this.getHolder().getOwner().getUniqueId());
		}, 8.0F), 1))), FollowPetOwner.create()));
		behaviorcontroller.addActivity(Activity.FIGHT, 10, ImmutableList.of(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), MeleeAttack.create(20)));
		behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
		behaviorcontroller.setDefaultActivity(Activity.IDLE);
		behaviorcontroller.useDefaultActivity();


		//behaviorcontroller.stopAll(worldserver, this);
		this.brain = behaviorcontroller;//.copyWithoutBehaviors();
		return behaviorcontroller;
	}

	@Override
	protected void customServerAiStep() {
		//this.level.getProfiler().push("petBrain");
		this.getBrain().tick((ServerLevel)this.level, this);
		//this.level.getProfiler().pop();
		super.customServerAiStep();
	}*/

    @Override
    public boolean isUnderWaterConverting() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public void startUnderWaterConversion(int i) {

    }

    @Override
    protected void doUnderWaterConversion() {

    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }
}
