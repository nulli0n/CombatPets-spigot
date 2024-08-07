package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

import java.util.Map;

public interface Tier extends Placeholder {

    @NotNull FileConfig getConfig();

    boolean load();

    @NotNull String getId();

    @NotNull String getName();

    void setName(@NotNull String name);

    @NotNull ItemStack getIcon();

    void setIcon(@NotNull ItemStack icon);

    @NotNull String getNameFormat();

    void setNameFormat(@NotNull String nameFormat);

    double getWeight();

    void setWeight(double weight);

    //@NotNull IPetCustomizer getDefaultCustomizer();

    boolean isCapturable();

    void setCapturable(boolean capturable);

    boolean hasInventory();

    void setHasInventory(boolean hasInventory);

    int getInventorySize();

    void setInventorySize(int inventorySize);

    boolean hasEquipment();

    void setHasEquipment(boolean hasEquipment);

    int getAutoRespawnTime();

    void setAutoRespawnTime(int autoRespawnTime);

    @NotNull String getReviveCurrency();

    void setReviveCurrency(@NotNull String currency);

    double getReviveCost();

    void setReviveCost(double reviveCost);

    double getInventoryDropChance();

    void setInventoryDropChance(double inventoryDropChance);

    double getEquipmentDropChance();

    void setEquipmentDropChance(double equipmentDropChance);



    int getInitialXP();

    void setInitialXP(int initialXP);

    double getXPModifier();

    void setXPModifier(double xpModifier);

    int getMaxLevel();

    void setMaxLevel(int maxLevel);

    int getRequiredXP(int level);

    int getAspectPointsPerLevel();

    void setAspectPointsPerLevel(int aspectPointsPerLevel);




    int getStartAspectPoints();

    void setStartAspectPoints(int aspectPoints);

    @NotNull Map<String, Integer> getAspectsMax();

    default int getAspectMax(@NotNull Aspect aspect) {
        return this.getAspectMax(aspect.getId());
    }

    default int getAspectMax(@NotNull String id) {
        return this.getAspectsMax().getOrDefault(id.toLowerCase(), 0);
    }
}
