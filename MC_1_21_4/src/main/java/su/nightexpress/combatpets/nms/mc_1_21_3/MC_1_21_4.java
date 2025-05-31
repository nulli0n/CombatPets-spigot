package su.nightexpress.combatpets.nms.mc_1_21_3;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.damage.CraftDamageSource;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.nms.PetNMS;
import su.nightexpress.combatpets.nms.mc_1_21_3.goals.follow.PetLookAtOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MC_1_21_4 implements PetNMS {

    public MC_1_21_4() {
        EntityInjector.register();
    }

    @Override
    @NotNull
    public Set<EntityType> getSupportedEntities() {
        return EntityInjector.getTypes();
    }

    @Override
    @NotNull
    public ActivePet spawnPet(@NotNull Template config, @NotNull Location location, @NotNull Function<LivingEntity, ActivePet> holderFunction) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            throw new IllegalStateException("World can not be null!");
        }

        EntityType entityType = config.getEntityType();
        ServerLevel level = ((CraftWorld) bukkitWorld).getHandle();

        net.minecraft.world.entity.LivingEntity entity = EntityInjector.spawn(entityType, level, location);
        if (!(entity instanceof Mob mob) || !(entity instanceof PetEntity petEntity)) {
            throw new IllegalStateException("Can not create pet entity from '" + entityType.name() + "'!");
        }

        mob.goalSelector.getAvailableGoals().clear();
        mob.targetSelector.getAvailableGoals().clear();

        // Register all attributes. They will be updated with actual values on spawn.
        BuiltInRegistries.ATTRIBUTE.keySet().forEach(resourceLocation -> {
            Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.get(resourceLocation).orElse(null);
            if (holder == null) return;

            this.registerAttribute(entity, holder);
        });

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();
        ActivePet holder = holderFunction.apply(bukkitEntity);
        PetEntityBridge.addHolder(petEntity, holder);

        petEntity.setGoals();
        mob.goalSelector.addGoal(8, new PetLookAtOwnerGoal(mob));
        mob.goalSelector.addGoal(9, new RandomLookAroundGoal(mob));

        return holder;
    }

    @Override
    public void setSaddle(@NotNull LivingEntity entity) {
        net.minecraft.world.entity.LivingEntity nmsEntity = ((CraftLivingEntity)entity).getHandle();
        if (nmsEntity instanceof AbstractHorse horse) {
            horse.equipSaddle(Items.SADDLE.getDefaultInstance(), null);
        }
    }

//    private void setAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Holder<Attribute> holder, double value) {
//        this.registerAttribute(handle, holder);
//
//        AttributeInstance instance = handle.getAttribute(holder);
//        if (instance == null) return;
//
//        instance.setBaseValue(value);
//    }

    @SuppressWarnings("unchecked")
    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Holder<Attribute> holder) {
        AttributeInstance instance = handle.getAttribute(holder);
        if (instance != null) return;

        AttributeMap attributes = handle.getAttributes();
        AttributeSupplier supplier = (AttributeSupplier) Reflex.getFieldValue(attributes, "e");
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

//    @SuppressWarnings("UnstableApiUsage")
//    @Override
//    public void damageItem(@NotNull org.bukkit.inventory.EquipmentSlot bukkitSlot,
//                           @NotNull LivingEntity bukkitEntity,
//                           @NotNull org.bukkit.damage.DamageSource bukkitSource,
//                           int damage) {
//
//        EquipmentSlot slot = CraftEquipmentSlot.getNMS(bukkitSlot);
//        DamageSource source = ((CraftDamageSource)bukkitSource).getHandle();
//        net.minecraft.world.entity.LivingEntity entity = ((CraftLivingEntity)bukkitEntity).getHandle();
//
//        ItemStack stack = entity.getItemBySlot(slot);
//
//        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
//        if (equippable != null && equippable.damageOnHurt() && stack.isDamageableItem() && stack.canBeHurtBy(source)) {
//            stack.hurtAndBreak(damage, entity, slot);
//        }
//    }

//    public static void hurtArmor(@NotNull net.minecraft.world.entity.LivingEntity entity, DamageSource source, float amount) {
//        if (amount <= 0F) return;
//
//        int damage = (int) Math.max(1.0F, amount / 4.0F);
//        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
//            ItemStack stack = entity.getItemBySlot(slot);
//
//            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
//            if (equippable != null && equippable.damageOnHurt() && stack.isDamageableItem() && stack.canBeHurtBy(source)) {
//                stack.hurtAndBreak(damage, entity, slot);
//            }
//        }
//    }
}
