package su.nightexpress.combatpets.pet.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPetDamageInUpdate(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ActivePet activePet = this.petManager.getPetByMob(victim);
        if (activePet == null) return;

        this.plugin.runTask(task -> {
            activePet.updateHealthBar();
            activePet.updateName();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPetDamageInFriendly(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ActivePet petVictim = this.petManager.getPetByMob(victim);
        if (petVictim == null) return;

        LivingEntity damager = PetUtils.getEventDamager(event);
        if (damager == null) return;

        // If damager is pet owner, or other player, but pvp is disabled, prevent damage.
        if (damager == petVictim.getOwner() || (damager instanceof Player && !Config.PET_PVP_ALLOWED.get())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPetDamageOutAdjust(EntityDamageByEntityEvent event) {
        if (!Config.PET_ATTACK_DAMAGE_FOR_PROJECTILES.get()) return;

        // Non-hand attacks only.
        LivingEntity damager = PetUtils.getEventDamager(event);
        if (damager == null || damager == event.getDamager()) return;

        ActivePet activePet = this.petManager.getPetByMob(damager);
        if (activePet == null) return;

        if (event.getEntity() instanceof Player player) {
            if (activePet.getOwner() == player || !Config.PET_PVP_ALLOWED.get()) {
                event.setCancelled(true);
                return;
            }
        }

        double origin = event.getDamage();
        double mod = Config.PET_ORIGINAL_DAMAGE_FROM_PROJECTILES.get();
        double add = origin * mod;
        double atkDamage = activePet.getAttackDamage() + add;

        event.setDamage(atkDamage);
    }
}
