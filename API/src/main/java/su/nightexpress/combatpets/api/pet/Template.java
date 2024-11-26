package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface Template extends Placeholder {

    @NotNull File getFile();

    @NotNull FileConfig getConfig();

    boolean load();

    @NotNull ItemStack createEgg(@NotNull Tier tier);

    boolean isFood(@NotNull ItemStack itemStack);

    default boolean isFood(@NotNull FoodItem foodItem) {
        return this.getFoodCategories().contains(foodItem.getCategory().getId());
    }

    @NotNull String getId();

    @NotNull String getDefaultName();

    void setDefaultName(@NotNull String defaultName);

    @NotNull EntityType getEntityType();

    void setEntityType(@NotNull EntityType type);

    @NotNull String getEggTexture();

    void setEggTexture(@NotNull String eggTexture);

    boolean canHaveInventory();

    void setInventory(boolean inventory);

    boolean canHaveEquipment();

    void setEquipment(boolean equipment);



    boolean isCapturable();

    void setCapturable(boolean capturable);

    double getCaptureEscapeChance();

    void setCaptureEscapeChance(double catchingEscapeChance);

    double getCaptureChance();

    void setCaptureChance(double catchingChance);

    @NotNull String getCapturePermission();



    @NotNull NightSound getEatSound();

    void setEatSound(@NotNull NightSound eatSound);

    @NotNull UniParticle getSpawnParticle();

    void setSpawnParticle(@NotNull UniParticle spawnParticle);

    @NotNull UniParticle getDespawnParticle();

    void setDespawnParticle(@NotNull UniParticle despawnParticle);

    @NotNull Set<String> getFoodCategories();

    void setFoodCategories(@NotNull Set<String> foodCategories);



    double getExhaustModifier(@NotNull ExhaustReason reason);

    void setExhaustModifier(@NotNull ExhaustReason reason, double mod);

    @NotNull Map<String, Double> getAttributesStart();

    @NotNull Map<String, Double> getAttributesPerAspect();

    default boolean hasAttribute(@NotNull Stat attribute) {
        return this.getAttributesStart().containsKey(attribute.getId());
    }

    default double getAttributeStart(@NotNull Stat attribute) {
        return this.getAttributeStart(attribute.getId());
    }

    default double getAttributePerAspect(@NotNull Stat attribute) {
        return this.getAttributePerAspect(attribute.getId());
    }

    default double getAttributeStart(@NotNull String attribute) {
        return this.getAttributesStart().getOrDefault(attribute.toLowerCase(), 0D);
    }

    default double getAttributePerAspect(@NotNull String attribute) {
        return this.getAttributesPerAspect().getOrDefault(attribute.toLowerCase(), 0D);
    }
}
