package su.nightexpress.combatpets.pet.listener;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.event.generic.PetDeathEvent;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.Arrays;
import java.util.stream.Stream;

public class PetGenericListener extends AbstractListener<PetsPlugin> {

    private final PetManager petManager;

    public PetGenericListener(@NotNull PetsPlugin plugin, @NotNull PetManager petManager) {
        super(plugin);
        this.petManager = petManager;
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onLazyPetsLoad(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        PetUser user = plugin.getUserManager().getUserData(player);
//
//        this.plugin.info("Start loading pets for " + player.getName());
//        this.plugin.runTaskAsync(task -> {
//            this.plugin.getData().loadPets(user);
//            this.plugin.info("Pets loaded for " + player.getName());
//        });
//    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPetDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ActivePet activePet = this.petManager.getPetByMob(entity);
        if (activePet == null) return;

        PetDeathEvent deathEvent = new PetDeathEvent(activePet, event);
        deathEvent.setPermanentDeath(Config.PET_PERMANENT_DEATH.get());
        event.getDrops().clear();
        event.setDroppedExp(0);

        if (Config.PET_DROP_INVENTORY.get() && Rnd.chance(activePet.getTier().getInventoryDropChance())) {
            deathEvent.setDropInventory(true);
        }
        if (Config.PET_DROP_EQUIPMENT.get() && Rnd.chance(activePet.getTier().getEquipmentDropChance())) {
            deathEvent.setDropEquipment(true);
        }

        plugin.getPetManager().handleDeath(activePet);
        plugin.getPluginManager().callEvent(deathEvent);

        if (deathEvent.isDropInventory()) {
            event.getDrops().addAll(Arrays.asList(activePet.getInventory().getContents()));
            activePet.getInventory().clear();
        }

        if (deathEvent.isDropEquipment()) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                event.getDrops().addAll(Arrays.asList(equipment.getArmorContents()));
                equipment.clear();
            }
        }

        if (deathEvent.isPermanentDeath()) {
            Player owner = activePet.getOwner();
            PetUser user = plugin.getUserManager().getOrFetch(owner);
            this.petManager.removeFromCollection(user, activePet.getTier(), activePet.getTemplate());
        }
    }

    // Make pets to use arrows with potion effects when shooting.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPetShootBowCustomArrows(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        ActivePet activePet = this.petManager.getPetByMob(entity);
        if (activePet == null || !activePet.getTier().hasInventory()) return;

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null || !ItemUtil.isBow(equipment.getItemInMainHand())) return;

        ItemStack has = equipment.getItemInOffHand();
        Material hasType = has.getType();
        if (hasType != Material.TIPPED_ARROW && hasType != Material.SPECTRAL_ARROW) {
            Stream.of(activePet.getInventory().getContents()).filter(item -> {
                Material type = item == null ? null : item.getType();
                return type == Material.TIPPED_ARROW || type == Material.SPECTRAL_ARROW;
            }).findFirst().ifPresent(arrow -> {
                activePet.getInventory().remove(arrow);
                equipment.setItemInOffHand(arrow);
            });
        }
    }

    // Make pets to use arrows with potion effects when shooting.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPetShootBowCustomArrows(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        ActivePet activePet = this.petManager.getPetByMob(entity);
        if (activePet == null || !activePet.getTier().hasInventory()) return;

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        ItemStack has = equipment.getItemInOffHand();
        Material hasType = has.getType();
        if (hasType == Material.TIPPED_ARROW || hasType == Material.SPECTRAL_ARROW) {
            has.setAmount(has.getAmount() - 1);
            equipment.setItemInOffHand(has);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetVanillaSplit(SlimeSplitEvent event) {
        event.setCancelled(this.petManager.isPetEntity(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetVanillaPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            event.setCancelled(this.petManager.isPetEntity(livingEntity));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetVanillaSnow(EntityBlockFormEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            event.setCancelled(this.petManager.isPetEntity(livingEntity));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetVanillaShear(PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            event.setCancelled(this.petManager.isPetEntity(livingEntity));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetVanillaSunburn(EntityCombustEvent event) {
        if (event instanceof EntityCombustByEntityEvent || event instanceof EntityCombustByBlockEvent) return;

        if (event.getEntity() instanceof LivingEntity entity) {
            event.setCancelled(this.petManager.isPetEntity(entity));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetGriefExplode(EntityExplodeEvent event) {
        LivingEntity entity = PetUtils.getParent(event.getEntity());
        if (entity == null) return;

        event.setCancelled(this.petManager.isPetEntity(entity));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetGriefIgnite(BlockIgniteEvent event) {
        if (event.getIgnitingEntity() == null) return;

        LivingEntity entity = PetUtils.getParent(event.getIgnitingEntity());
        if (entity == null) return;

        event.setCancelled(this.petManager.isPetEntity(entity));
    }
}
