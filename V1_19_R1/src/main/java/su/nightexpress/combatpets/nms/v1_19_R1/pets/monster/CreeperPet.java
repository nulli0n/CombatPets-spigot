package su.nightexpress.combatpets.nms.v1_19_R1.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetAttackMeleeGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.combat.PetHurtByTargetGoal;
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetFollowOwnerGroundGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Method;
import java.util.UUID;

public class CreeperPet extends Creeper implements PetEntity {

    private int explodeCooldown = 0;

    public CreeperPet(@NotNull ServerLevel world) {
        super(EntityType.CREEPER, world);
        this.maxSwell = 60;
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
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new PetAttackMeleeGoal(this));
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            net.minecraft.world.entity.LivingEntity target = this.getTarget();
            if (target == null || !target.isAlive()) {
                this.setSwellDir(-1);
                this.swell = 0;
                this.setTarget(null);
            }

            if (this.explodeCooldown-- <= 0) {
                Reflex.setFieldValue(this, "e", this.swell);

                if (this.isIgnited()) {
                    this.setSwellDir(1);
                }

                int count = this.getSwellDir();
                if (count > 0 && this.swell == 0) {
                    this.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f);
                    this.gameEvent(GameEvent.PRIME_FUSE);
                }
                this.swell += count;
                if (this.swell < 0) {
                    this.swell = 0;
                }
                if (this.swell >= this.maxSwell) {
                    //if (this.explodeCooldown <= 0) {
                    this.explodeCreeper();
                    this.setSwellDir(-1);
                    this.explodeCooldown = 60;
                    //}
                    this.swell = 0;
                }
            }
        }
        super.tick();
    }

    @Override
    public void explodeCreeper() {
        if (this.level.isClientSide) return;

        Level.ExplosionInteraction interaction = Level.ExplosionInteraction.NONE;

        float f = this.isPowered() ? 2.0f : 1.0f;

        ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), (float) this.explosionRadius * f, false);
        this.level.getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            //this.aX = true; // Makes mob 'invulnerable', stop fires damage events
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), event.getRadius(), event.getFire(), interaction);
            this.dead = false;
            //this.die();
            Method m = Reflex.getMethod(this.getClass(), "fE");
            if (m != null) Reflex.invokeMethod(m, this);

            LivingEntity li = (LivingEntity) this.getBukkitEntity();
            li.setVelocity(li.getEyeLocation().add(1, 1, 1).getDirection().multiply(-1.5));
        }
    }

    @Override
    public void thunderHit(ServerLevel worldserver, LightningBolt entitylightning) {

    }

    @Override
    protected InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean canDropMobsSkull() {
        return false;
    }
}
