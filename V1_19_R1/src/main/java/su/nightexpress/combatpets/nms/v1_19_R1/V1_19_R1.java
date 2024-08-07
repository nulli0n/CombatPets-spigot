package su.nightexpress.combatpets.nms.v1_19_R1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
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
import su.nightexpress.combatpets.nms.v1_19_R1.goals.follow.PetLookAtOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class V1_19_R1 implements PetNMS {

    public V1_19_R1() {
        EntityInjector.setup();
    }

    @Override
    @NotNull
    public Set<EntityType> getSupportedEntities() {
        return EntityInjector.PET_TYPES.keySet();
    }

    @Override
    public void setLeashedTo(@NotNull LivingEntity entity, @Nullable Entity holder) {
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
        if (!(craftEntity.getHandle() instanceof Mob mob)) return;

        mob.setLeashedTo(holder == null ? null : ((CraftEntity)holder).getHandle(), true);
    }

    @Override
    @NotNull
    public ActivePet spawnPet(@NotNull Template config, @NotNull Location location, @NotNull Function<LivingEntity, ActivePet> holderFunction) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            throw new IllegalStateException("World can not be null!");
        }

        ServerLevel level = ((CraftWorld) bukkitWorld).getHandle();

        net.minecraft.world.entity.LivingEntity entity = EntityInjector.spawnEntity(config.getEntityType(), level, location);
        if (entity == null) throw new IllegalStateException("Could not spawn " + config.getEntityType().name() + " pet!");
        if (!(entity instanceof Mob mob)) {
            throw new IllegalStateException("Spawned entity '" + config.getEntityType().name() + "' is NULL or not a Mob instance!");
        }
        if (!(entity instanceof PetEntity petEntity)) {
            throw new IllegalStateException("Spawned entity is not a PetEntity!");
        }

        mob.goalSelector.getAvailableGoals().clear();
        mob.targetSelector.getAvailableGoals().clear();

        // Register default attributes
        // They will be updated with actual values on spawn.
        this.setAttribute(entity, Attributes.ATTACK_DAMAGE, 1D);
        this.setAttribute(entity, Attributes.FOLLOW_RANGE, 32D);
        this.setAttribute(entity, Attributes.FLYING_SPEED, 0.1D);

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();
        ActivePet holder = holderFunction.apply(bukkitEntity);
        PetEntityBridge.addHolder(petEntity, holder);
        petEntity.setGoals();

        mob.goalSelector.addGoal(8, new PetLookAtOwnerGoal(mob));

        return holder;
    }

    private void setAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute attribute, double value) {
        this.registerAttribute(handle, attribute);

        AttributeInstance instance = handle.getAttribute(attribute);
        if (instance == null) {
            //System.out.println("Could not create attribute instance: " + attribute.getDescriptionId());
            return;
        }
        instance.setBaseValue(value);
    }

    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute att) {
        AttributeInstance instance = handle.getAttribute(att);

        if (instance == null) {
            // Hardcode to register missing entity's attributes.
            AttributeSupplier provider = (AttributeSupplier) Reflex.getFieldValue(handle.getAttributes(), "d");
            if (provider == null) return;

            @SuppressWarnings("unchecked")
            Map<Attribute, AttributeInstance> aMap = (Map<Attribute, AttributeInstance>) Reflex.getFieldValue(provider, "a");
            if (aMap == null) return;

            Map<Attribute, AttributeInstance> aMap2 = new HashMap<>(aMap);
            aMap2.put(att, new AttributeInstance(att, var1 -> {

            }));
            Reflex.setFieldValue(provider, "a", aMap2);
            //System.out.println("Injected Attribute: " + att.getDescriptionId());
        }
    }

    @Override
    public void sneak(@NotNull LivingEntity entity, boolean value) {
        Mob mob = (Mob) ((CraftLivingEntity) entity).getHandle();
        mob.setShiftKeyDown(value);
    }

    @Override
    public boolean hasNavigationPath(@NotNull LivingEntity entity) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        if (!(livingEntity instanceof Mob mob)) return false;

        return mob.getNavigation().isInProgress();
    }
}
