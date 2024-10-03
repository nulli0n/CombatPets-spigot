package su.nightexpress.combatpets.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.random.Rnd;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PetUtils {

    @Nullable
    public static String extractBase64TextureURL(@NotNull String headTexture) {
        try {
            byte[] decoded = Base64.getDecoder().decode(headTexture);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(decodedStr);

            String url = element.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            return url.substring(ItemUtil.TEXTURES_HOST.length());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static long createTimestamp(long seconds) {
        return seconds < 0 ? -1L : System.currentTimeMillis() + seconds * 1000L;
    }

    @NotNull
    public static ItemStack getRawEggItem(@NotNull Template template) {
        ItemStack item = new ItemStack(Config.PET_EGG_ITEM.get());
        if (!template.getEggTexture().isEmpty()) {
            ItemUtil.setHeadSkin(item, template.getEggTexture());
        }
        return item;
    }

    @NotNull
    public static ItemStack getRawMysteryEgg(@NotNull Template template) {
        return new ItemStack(Config.ITEM_MYSTERY_EGG.get());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    public static LivingEntity getEventDamager(@NotNull EntityDamageByEntityEvent event) {
        if (Version.isAtLeast(Version.MC_1_20_6)) {
            DamageSource source = event.getDamageSource();
            return source.getCausingEntity() instanceof LivingEntity damager ? damager : null;
        }
        return getParent(event.getDamager());
    }

    @Nullable
    public static LivingEntity getParent(@NotNull Entity entity) {
        LivingEntity parent = null;
        if (entity instanceof LivingEntity living) {
            parent = living;
        }
        else if (entity instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity living) {
            parent = living;
        }
        else if (entity instanceof EvokerFangs fangs && fangs.getOwner() != null) {
            parent = fangs.getOwner();
        }
        return parent;
    }

    public static void setAttribute(@NotNull LivingEntity entity, @NotNull Stat attribute, double value) {
        Attribute mirror = attribute.getVanillaMirror();
        if (mirror != null) {
            AttributeInstance instance = entity.getAttribute(mirror);
            if (instance == null) return;

            instance.setBaseValue(value);
        }
        else {
            PDCUtil.set(entity, attribute.getKey(), value);
        }
    }

    public static double getAttribute(@NotNull LivingEntity entity, @NotNull Stat attribute) {
        Attribute mirror = attribute.getVanillaMirror();
        if (mirror != null) {
            return EntityUtil.getAttribute(entity, mirror);
        }

        return PDCUtil.getDouble(entity, attribute.getKey()).orElse(0D);
    }

    @NotNull
    public static String getPetKey(@NotNull Tier tier, @NotNull Template config) {
        return (config.getId() + "_" + tier.getId()).toLowerCase();
    }

    public static boolean isAllowedPetZone(@NotNull Location location) {
        World world = location.getWorld();
        if (world != null) {
            return !Config.PET_DISABLED_WORLDS.get().contains(world.getName().toLowerCase());
        }
        return true;
    }

    public static void populateDefaultEquipment(@NotNull PetData petData) {
        EntityType type = petData.getTemplate().getEntityType();

        if (type == EntityType.SKELETON || type == EntityType.STRAY || (Version.isAtLeast(Version.MC_1_21) && type.name().equalsIgnoreCase("bogged"))) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.BOW));
        }
        else if (type == EntityType.WITHER_SKELETON) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.STONE_SWORD));
        }
        else if (type == EntityType.PIGLIN) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Rnd.RANDOM.nextFloat() < 0.5D ? Material.CROSSBOW : Material.GOLDEN_SWORD));
            maybeEquipment(petData, EquipmentSlot.HEAD, new ItemStack(Material.GOLDEN_HELMET), 0.1);
            maybeEquipment(petData, EquipmentSlot.CHEST, new ItemStack(Material.GOLDEN_CHESTPLATE), 0.15);
            maybeEquipment(petData, EquipmentSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS), 0.1);
            maybeEquipment(petData, EquipmentSlot.FEET, new ItemStack(Material.GOLDEN_BOOTS), 0.1);
        }
        else if (type == EntityType.PIGLIN_BRUTE) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_AXE));
        }
        else if (type == EntityType.ZOMBIFIED_PIGLIN) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_SWORD));
        }
        else if (type == EntityType.PILLAGER) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.CROSSBOW));
        }
        else if (type == EntityType.VINDICATOR) {
            petData.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE));
        }
    }

    private static void maybeEquipment(@NotNull PetData petData, @NotNull EquipmentSlot slot, @NotNull ItemStack item, double chance) {
        if (Rnd.RANDOM.nextFloat() < chance) {
            petData.setEquipment(slot, item);
        }
    }
}
