package su.nightexpress.combatpets.nms.v1_20;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.nms.PetNMS;
import su.nightexpress.combatpets.nms.v1_20.goals.follow.PetLookAtOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class V1_20 implements PetNMS {

    public V1_20() {
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
        BuiltInRegistries.ATTRIBUTE.stream().forEach(attribute -> {
            this.registerAttribute(entity, attribute);
        });

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();
        ActivePet holder = holderFunction.apply(bukkitEntity);
        PetEntityBridge.addHolder(petEntity, holder);

        petEntity.setGoals();
        mob.goalSelector.addGoal(8, new PetLookAtOwnerGoal(mob));
        mob.goalSelector.addGoal(9, new RandomLookAroundGoal(mob));

        return holder;
    }

    private void setAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute attribute, double value) {
        this.registerAttribute(handle, attribute);

        AttributeInstance instance = handle.getAttribute(attribute);
        if (instance == null) return;

        instance.setBaseValue(value);
    }

    @SuppressWarnings("unchecked")
    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute attribute) {
        AttributeInstance instance = handle.getAttribute(attribute);
        if (instance != null) return;

        AttributeMap attributes = handle.getAttributes();
        AttributeSupplier supplier = (AttributeSupplier) Reflex.getFieldValue(attributes, "d");
        if (supplier == null) return;

        Map<Attribute, AttributeInstance> instances = (Map<Attribute, AttributeInstance>) Reflex.getFieldValue(supplier, "a");
        if (instances == null) return;

        Map<Attribute, AttributeInstance> instances2 = new HashMap<>(instances); // Copy because it's immutable.
        instances2.put(attribute, new AttributeInstance(attribute, insta -> {

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

    public static void hurtArmor(@NotNull net.minecraft.world.entity.LivingEntity entity, DamageSource source, float amount) {
        if (amount <= 0F) return;

        amount /= 4.0F;
        if (amount < 1.0F) {
            amount = 1.0F;
        }

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if ((!source.is(DamageTypeTags.IS_FIRE) || !stack.getItem().isFireResistant()) && stack.getItem() instanceof ArmorItem) {
                stack.hurtAndBreak((int) amount, entity, (mob) -> {
                    mob.broadcastBreakEvent(slot);
                });
            }
        }
    }
}
