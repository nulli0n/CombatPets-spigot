package su.nightexpress.combatpets.nms.mc_1_21_5;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R4.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.nms.PetNMS;
import su.nightexpress.combatpets.nms.mc_1_21_5.goals.follow.PetLookAtOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MC_1_21_5 implements PetNMS {

    public MC_1_21_5() {
        EntityInjector.register();
    }

    @Override
    @NotNull
    public Set<EntityType> getSupportedEntities() {
        return EntityInjector.getTypes();
    }

    @Override
    @NotNull
    public ActivePet spawnPet(@NotNull Template template, @NotNull Location location, @NotNull Function<LivingEntity, ActivePet> holderFunction) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            throw new IllegalStateException("World can not be null!");
        }

        EntityType type = template.getEntityType();
        ServerLevel level = ((CraftWorld) bukkitWorld).getHandle();
        Mob mob = EntityInjector.spawn(type, level);
        PetEntity petEntity = (PetEntity) mob;

        mob.goalSelector.getAvailableGoals().clear();
        mob.targetSelector.getAvailableGoals().clear();

        // Register all attributes. They will be updated with actual values on spawn.
        BuiltInRegistries.ATTRIBUTE.keySet().forEach(resourceLocation -> {
            Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.get(resourceLocation).orElse(null);
            if (holder == null) return;

            this.registerAttribute(mob, holder);
        });

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();
        ActivePet holder = holderFunction.apply(bukkitEntity);
        PetEntityBridge.addHolder(petEntity, holder);

        petEntity.setGoals();
        mob.goalSelector.addGoal(8, new PetLookAtOwnerGoal(mob));
        mob.goalSelector.addGoal(9, new RandomLookAroundGoal(mob));

        level.addFreshEntity(mob, null);
        mob.snapTo(location.getX(), location.getY(), location.getZ());

        return holder;
    }

    @Override
    public void setSaddle(@NotNull LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        equipment.setItem(CraftEquipmentSlot.getSlot(EquipmentSlot.SADDLE), new org.bukkit.inventory.ItemStack(Material.SADDLE));
    }

    @SuppressWarnings("unchecked")
    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Holder<Attribute> holder) {
        AttributeInstance instance = handle.getAttribute(holder);
        if (instance != null) return;

        AttributeMap attributes = handle.getAttributes();
        AttributeSupplier supplier = (AttributeSupplier) Reflex.getFieldValue(attributes, "d");
        if (supplier == null) return;

        Map<Holder<Attribute>, AttributeInstance> instances = (Map<Holder<Attribute>, AttributeInstance>) Reflex.getFieldValue(supplier, "a");
        if (instances == null) return;

        Map<Holder<Attribute>, AttributeInstance> instances2 = new HashMap<>(instances); // Copy because it's immutable.
        instances2.put(holder, new AttributeInstance(holder, insta -> {

        }));
        Reflex.setFieldValue(supplier, "a", instances2);
    }

    @Override
    public void sneak(@NotNull LivingEntity entity, boolean value) {
        CraftLivingEntity craftEntity = ((CraftLivingEntity) entity);
        craftEntity.getHandle().setShiftKeyDown(value);
    }

    @Override
    public boolean hasNavigationPath(@NotNull LivingEntity entity) {
        CraftLivingEntity craftEntity = ((CraftLivingEntity) entity);

        net.minecraft.world.entity.LivingEntity nmsEntity = craftEntity.getHandle();
        if (!(nmsEntity instanceof Mob mob)) return false;

        return mob.getNavigation().isInProgress();
    }

    @Override
    public void setLeashedTo(@NotNull LivingEntity entity, @Nullable Entity holder) {
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
        if (!(craftEntity.getHandle() instanceof Mob mob)) return;

        mob.setLeashedTo(holder == null ? null : ((CraftEntity)holder).getHandle(), true);
    }

//    @Override
//    public void damageItem(@NotNull org.bukkit.inventory.EquipmentSlot[] bukkitSlots,
//                           @NotNull LivingEntity bukkitEntity,
//                           @NotNull org.bukkit.damage.DamageSource bukkitSource,
//                           int damage) {
//
//        DamageSource source = ((CraftDamageSource)bukkitSource).getHandle();
//        net.minecraft.world.entity.LivingEntity entity = ((CraftLivingEntity)bukkitEntity).getHandle();
//
//        for (org.bukkit.inventory.EquipmentSlot bukkitSlot : bukkitSlots) {
//            EquipmentSlot slot = CraftEquipmentSlot.getNMS(bukkitSlot);
//            ItemStack stack = entity.getItemBySlot(slot);
//            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
//
//            if (equippable != null && equippable.damageOnHurt() && stack.isDamageableItem() && stack.canBeHurtBy(source)) {
//                stack.hurtAndBreak(damage, entity, slot);
//            }
//        }
//    }
}
