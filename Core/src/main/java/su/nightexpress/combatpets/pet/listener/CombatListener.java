package su.nightexpress.combatpets.pet.listener;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.manager.AbstractListener;

public class CombatListener extends AbstractListener<PetsPlugin> {

    private final PetManager petManager;

    public CombatListener(@NotNull PetsPlugin plugin, @NotNull PetManager petManager) {
        super(plugin);
        this.petManager = petManager;
    }

    //@SuppressWarnings({"deprecation"})
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPetDamageInUpdate(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ActivePet activePet = this.petManager.getPetByMob(victim);
        if (activePet == null) return;

//        DamageSource source = event.getDamageSource();
//        if (!DamageTypeTags.BYPASSES_ARMOR.isTagged(source.getDamageType())) {
//            float amount = (float) (event.getDamage() + event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) + event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT));
//            PetUtils.hurtArmor(victim, source, amount);
//        }

        activePet.onIncomingDamage();

        this.plugin.runTask(task -> {
            activePet.updateName();
            activePet.updateHealthBar();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPetDamageInFriendly(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof LivingEntity damager)) return;

        if (!this.petManager.canDamage(damager, victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPetDamageOutAdjust(EntityDamageByEntityEvent event) {
        if (!Config.PET_ATTACK_DAMAGE_FOR_PROJECTILES.get()) return;

        // Non-hand attacks only.
        Entity causingEntity = event.getDamageSource().getCausingEntity();
        Entity directEntity = event.getDamageSource().getDirectEntity();
        if (!(causingEntity instanceof LivingEntity damager) || causingEntity == directEntity) return;

        ActivePet activePet = this.petManager.getPetByMob(damager);
        if (activePet == null) return;

        double origin = event.getDamage();
        double mod = Config.PET_ORIGINAL_DAMAGE_FROM_PROJECTILES.get();
        double add = origin * mod;
        double atkDamage = activePet.getAttackDamage() + add;

        event.setDamage(atkDamage);
    }
}
