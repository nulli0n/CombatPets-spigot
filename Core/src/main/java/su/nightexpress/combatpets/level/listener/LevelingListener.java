package su.nightexpress.combatpets.level.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.level.LevelingConfig;
import su.nightexpress.combatpets.level.LevelingManager;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.EntityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelingListener extends AbstractListener<PetsPlugin> {

    private final LevelingManager manager;
    private final Map<UUID, Map<UUID, Double>> damageMap;

    public LevelingListener(@NotNull PetsPlugin plugin, @NotNull LevelingManager manager) {
        super(plugin);
        this.manager = manager;
        this.damageMap = new HashMap<>();
    }

    @NotNull
    private Map<UUID, Double> getDealtDamageMap(@NotNull LivingEntity victim) {
        return this.damageMap.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>());
    }

    private double getDealtDamage(@NotNull LivingEntity victim, @NotNull LivingEntity damager) {
        return this.getDealtDamageMap(victim).getOrDefault(damager.getUniqueId(), 0D);
    }

    private void addDealtDamage(@NotNull LivingEntity victim, @NotNull LivingEntity damager, double damage) {
        this.getDealtDamageMap(victim).put(damager.getUniqueId(), this.getDealtDamage(victim, damager) + damage);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXPAbuseSpawnReason(CreatureSpawnEvent event) {
        if (LevelingConfig.DISABLE_XP_BY_SPAWN_REASON.get().contains(event.getSpawnReason())) {
            this.manager.setDropXP(event.getEntity(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGainHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof LivingEntity damager)) return;
        if (!this.manager.shouldDropXP(victim)) return;
        if (this.manager.isDisabledWorld(victim.getWorld())) return;

        double damage = event.getFinalDamage();
        if (damage <= 0D) return;

        ActivePet activePet = this.plugin.getPetManager().getPetByMob(damager);
        if (activePet == null) return;

        if (damage > victim.getHealth()) damage = victim.getHealth();

        this.addDealtDamage(victim, damager, damage);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExpGainKill(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!this.manager.shouldDropXP(victim)) return;
        if (this.manager.isDisabledWorld(victim.getWorld())) return;

        Map<UUID, Double> damagers = this.getDealtDamageMap(victim);
        if (damagers.isEmpty()) return;

        double totalHealth = EntityUtil.getAttribute(victim, Attribute.MAX_HEALTH);

        damagers.forEach((damagerId, damageDealt) -> {
            Entity entity = this.plugin.getServer().getEntity(damagerId);
            if (!(entity instanceof LivingEntity damager)) return;

            ActivePet pet = this.plugin.getPetManager().getPetByMob(damager);
            if (pet == null) return;

            double damagePercent = Math.min(1D, damageDealt / totalHealth);
            if (damagePercent <= 0) return;

            int xpReward = this.manager.rewardXP(pet, victim, damagePercent, event.getDroppedExp());

            if (!this.manager.useCustomXPTable()) {
                event.setDroppedExp(Math.max(0, event.getDroppedExp() - xpReward));
            }
        });
    }
}
