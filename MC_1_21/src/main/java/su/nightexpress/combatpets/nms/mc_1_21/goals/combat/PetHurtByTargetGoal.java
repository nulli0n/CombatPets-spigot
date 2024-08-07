package su.nightexpress.combatpets.nms.mc_1_21.goals.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.api.pet.PetEntity;

import java.util.EnumSet;

public class PetHurtByTargetGoal extends TargetGoal {

    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    private final ActivePet petHolder;

    private int timestampPetLastHurtBy;
    private int timestampOwnerLastHurtBy;
    private int timestampOwnerLastHurt;

    private LivingEntity ownerLastAttacker;
    private LivingEntity ownerLastVictim;

    public PetHurtByTargetGoal(Mob pet) {
        super(pet, true);
        this.petHolder = ((PetEntity) pet).getHolder();
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @NotNull
    private Player getOwner() {
        return ((CraftPlayer) this.petHolder.getOwner()).getHandle();
    }

    public boolean canUse() {
        Player owner = this.getOwner();
        CombatMode combatMode = this.petHolder.getCombatMode();

        if (combatMode != CombatMode.PASSIVE) {
            int petLastHurtByDate = this.mob.getLastHurtByMobTimestamp();
            LivingEntity lastDamager = this.mob.getLastHurtByMob();
            if (mob.tickCount - petLastHurtByDate <= 50) {
                if (petLastHurtByDate != this.timestampPetLastHurtBy && lastDamager != null) {
                    return this.canAttack(lastDamager, HURT_BY_TARGETING);
                }
            }
        }

        if (combatMode == CombatMode.PROTECTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE) {
            this.ownerLastAttacker = owner.getLastHurtByMob();
            int ownerLastHurtByDate = owner.getLastHurtByMobTimestamp();
            if (owner.tickCount - ownerLastHurtByDate <= 50) {
                if (ownerLastHurtByDate != this.timestampOwnerLastHurtBy && this.ownerLastAttacker != null) {
                    return this.canAttack(this.ownerLastAttacker, TargetingConditions.DEFAULT);
                }
            }
        }

        if (combatMode == CombatMode.SUPPORTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE) {
            this.ownerLastVictim = owner.getLastHurtMob();
            int ownerLastHurtDate = owner.getLastHurtMobTimestamp();
            if (owner.tickCount - ownerLastHurtDate <= 50) {
                if (ownerLastHurtDate != this.timestampOwnerLastHurt && this.ownerLastVictim != null) {
                    return this.canAttack(this.ownerLastVictim, TargetingConditions.DEFAULT);
                }
            }
        }

        return false;
    }

    public void start() {
        LivingEntity petLastAttacker = this.mob.getLastHurtByMob();
        if (petLastAttacker != null) {
            this.mob.setTarget(this.mob.getLastHurtByMob(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
            this.targetMob = this.mob.getTarget();
        }
        else if (this.ownerLastAttacker != null) {
            this.mob.setTarget(this.ownerLastAttacker, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
        }
        else if (this.ownerLastVictim != null) {
            this.mob.setTarget(this.ownerLastVictim, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        }

        this.timestampPetLastHurtBy = this.mob.getLastHurtByMobTimestamp();
        this.timestampOwnerLastHurtBy = this.getOwner().getLastHurtByMobTimestamp();
        this.timestampOwnerLastHurt = this.getOwner().getLastHurtMobTimestamp();

        this.unseenMemoryTicks = 300;

        super.start();
    }
}
