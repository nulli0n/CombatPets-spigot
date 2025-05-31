package su.nightexpress.combatpets.nms.mc_1_21_5.pets.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetAI;
import su.nightexpress.combatpets.nms.mc_1_21_5.brain.PetBrain;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.lang.reflect.Method;

public class CreeperPet extends Creeper implements PetEntity {

    private int explodeCooldown = 0;

    public CreeperPet(@NotNull ServerLevel world) {
        super(EntityType.CREEPER, world);
        this.maxSwell = 60;
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new PetMeleeAttackGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, PetAI.ARMOR_SLOTS);
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
                Reflex.setFieldValue(this, "bJ", this.swell);

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
        if (this.level().isClientSide) return;

        Level.ExplosionInteraction interaction = Level.ExplosionInteraction.NONE;

        float f = this.isPowered() ? 2.0f : 1.0f;

        ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), (float) this.explosionRadius * f, false);
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            //this.aX = true; // Makes mob 'invulnerable', stop fires damage events
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), event.getRadius(), event.getFire(), interaction);
            this.dead = false;
            //this.die();
            Method m = Reflex.getMethod(this.getClass(), "gB");
            if (m != null) Reflex.invokeMethod(m, this);

            LivingEntity li = (LivingEntity) this.getBukkitEntity();
            li.setVelocity(li.getEyeLocation().add(1, 1, 1).getDirection().multiply(-1.5));
        }
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        PetBrain.thunderHit(this, level, bolt);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean canDropMobsSkull() {
        return false;
    }
}
