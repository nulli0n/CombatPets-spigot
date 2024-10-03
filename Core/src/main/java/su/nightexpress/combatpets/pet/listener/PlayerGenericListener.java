package su.nightexpress.combatpets.pet.listener;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.nightcore.manager.AbstractListener;

public class PlayerGenericListener extends AbstractListener<PetsPlugin> {

    private final PetManager petManager;

    public PlayerGenericListener(@NotNull PetsPlugin plugin, @NotNull PetManager petManager) {
        super(plugin);
        this.petManager = petManager;
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPetClaimEgg(PlayerInteractEvent event) {
//        ItemStack egg = event.getItem();
//        if (egg == null || egg.getType().isAir()) return;
//        if (event.useItemInHand() == Event.Result.DENY) return;
//
//        Template template = this.petManager.getTemplate(egg);
//        if (template == null) return;
//
//        Tier tier = this.petManager.getTierByItem(egg).orElse(null);
//        if (tier == null) return;
//
//        event.setUseInteractedBlock(Event.Result.DENY);
//        event.setUseItemInHand(Event.Result.DENY);
//
//        Player player = event.getPlayer();
//        if (this.petManager.tryClaimPet(player, tier, template)) {
//            egg.setAmount(egg.getAmount() - 1);
//        }
//    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPetInteract2(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;

        if (this.plugin.getPetManager().tryInteract(event.getPlayer(), entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPetInteract3(PlayerArmorStandManipulateEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (this.plugin.getPetManager().tryInteract(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        ActivePet activePet = this.petManager.getPlayerPet(player);
        if (activePet == null) return;

        this.plugin.getPetNMS().sneak(activePet.getEntity(), event.isSneaking());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        ActivePet activePet = this.petManager.getPlayerPet(player);
        if (activePet == null) return;

        if (from.getWorld() != to.getWorld()) {
            this.petManager.removePet(activePet);
            this.plugin.runTask(task -> this.petManager.spawnPet(player, activePet.getTier(), activePet.getTemplate()));
            return;
        }

        this.plugin.runTaskLater(task -> activePet.moveToOwner(), 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.petManager.despawnPet(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.petManager.despawnPet(event.getPlayer());
    }
}

