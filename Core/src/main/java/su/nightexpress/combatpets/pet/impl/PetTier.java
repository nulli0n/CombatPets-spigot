package su.nightexpress.combatpets.pet.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.currency.CurrencyId;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class PetTier extends AbstractFileData<PetsPlugin> implements Tier {

    private String    name;
    private ItemStack icon;
    private String    nameFormat;
    private double    weight;
    private boolean   capturable;
    private boolean   hasInventory;
    private int       inventorySize;
    private boolean   hasEquipment;
    private int       autoRespawnTime;
    private String    reviveCurrency;
    private double    reviveCost;
    private double    inventoryDropChance;
    private double    equipmentDropChance;
    private int       initialXP;
    private double    xpFactor;
    private int       maxLevel;
    private int       startAspectPoints;
    private int       aspectPointsPerLevel;

    private final Map<String, Integer>      aspectsMax;
    private final TreeMap<Integer, Integer> xpMap;
    private final PlaceholderMap            placeholderMap;

    public PetTier(@NotNull PetsPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.aspectsMax = new HashMap<>();
        this.xpMap = new TreeMap<>();
        this.placeholderMap = Placeholders.forTier(this);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.name = ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Sets tier display name."
        ).read(config);

        this.icon = ConfigValue.create("Icon", new ItemStack(Material.SILVERFISH_SPAWN_EGG),
            "Sets tier icon.",
            "Available options: " + WIKI_ITEMS_URL
        ).read(config);

        this.setNameFormat(ConfigValue.create("Name_Format",
            WHITE.enclose(PET_NAME) + " " + GRAY.enclose("Lv. ") + LIGHT_GREEN.enclose(PET_LEVEL),
            "Sets name format for pets of this tier.",
            "You can use 'Pet' and 'Tier' placeholders: " + WIKI_PLACEHOLDERS_URL
        ).read(config));

        this.setWeight(ConfigValue.create("Weight",
            25D,
            "Sets tier weight value.",
            "Weight determines the chance for the tier to be selected on capturing and tier display order in GUIs."
        ).read(config));

        this.hasInventory = ConfigValue.create("Inventory.Enabled",
            true,
            "When 'true', allows pets with this tier to have an inventory."
        ).read(config);

        this.inventorySize = ConfigValue.create("Inventory.Size",
            9,
            "Sets the inventory size. Allowed values: [9, 18, 27, 36, 45, 54]"
        ).read(config);

        this.hasEquipment = ConfigValue.create("Equipment.Enabled",
            false,
            "When enabled, allows pets with this tier to have an equipment (pets can wear armor and weapon)."
        ).read(config);

        this.autoRespawnTime = ConfigValue.create("Death.Auto_Respawn_Time",
            600,
            "Sets auto-respawn time (in seconds) for dead pets with this tier.",
            "Set this to -1 to disable auto-respawn."
        ).read(config);

        this.setReviveCurrency(ConfigValue.create("Death.Revive_Currency",
            CurrencyId.VAULT,
            "Sets currency for pet revive.",
            "Available currencies: [" + String.join(", ", EconomyBridge.getCurrencyIds()) + "]"
        ).read(config));

        this.setReviveCost(ConfigValue.create("Death.Revive_Cost",
            2000D,
            "Sets price for a player to (force) revive a pet with this tier."
        ).read(config));

        this.inventoryDropChance = ConfigValue.create("Death.Inventory_Drop_Chance",
            50D,
            "Sets chance for the entire pet's inventory drop on death.",
            "Make sure you have enabled pet inventory drop in config.yml."
        ).read(config);

        this.equipmentDropChance = ConfigValue.create("Death.Equipment_Drop_Chance",
            25D,
            "Sets chance for the entire pet's equipment drop on death.",
            "Make sure you have enabled pet equipment drop in config.yml."
        ).read(config);

        this.capturable = ConfigValue.create("Catching.Enabled",
            false,
            "When 'true', allows to capture pets with this tier."
        ).read(config);

        this.initialXP = ConfigValue.create("Leveling.Start_Exp",
            500,
            "Sets initial XP amount required for next level."
        ).read(config);

        this.setXPModifier(ConfigValue.create("Leveling.XPModifier",
            1.093,
            "Sets modifier for XP amount required for next level."
        ).read(config));

        this.maxLevel = ConfigValue.create("Leveling.Max_Level",
            30,
            "Sets the max. level for pets with this tier."
        ).read(config);

        this.aspectPointsPerLevel = ConfigValue.create("Leveling.Aspect_Points_Per_Level",
            1,
            "How much aspect points a pet will receive on level up?"
        ).read(config);

        this.setStartAspectPoints(ConfigValue.create("Leveling.Start_Aspect_Points",
            0,
            "Sets how many Aspect Points a pet with this tier will have when obtained."
        ).read(config));

        if (this.getMaxLevel() > 0) {
            for (int level = 1; level < (this.getMaxLevel() + 1); level++) {
                double previousXP = this.xpMap.getOrDefault(level - 1, this.getInitialXP());
                int xp = (int) (previousXP * this.xpFactor);
                this.xpMap.put(level, xp);
            }
        }


        for (String aspectId : config.getSection("Aspects.Max")) {
            Aspect aspect = this.plugin.getPetManager().getAspect(aspectId);
            if (aspect == null) continue;

            this.aspectsMax.put(aspect.getId(), config.getInt("Aspects.Max." + aspectId));
        }

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.getName());
        config.setItem("Icon", this.getIcon());
        config.set("Name_Format", this.getNameFormat());
        config.set("Weight", this.getWeight());
        config.set("Inventory.Enabled", this.hasInventory());
        config.set("Inventory.Size", this.getInventorySize());
        config.set("Equipment.Enabled", this.hasEquipment());
        config.set("Death.Auto_Respawn_Time", this.getAutoRespawnTime());
        config.set("Death.Revive_Cost", this.getReviveCost());
        config.set("Death.Inventory_Drop_Chance", this.getInventoryDropChance());
        config.set("Death.Equipment_Drop_Chance", this.getEquipmentDropChance());
        config.set("Catching.Enabled", this.isCapturable());
        config.set("Leveling.Start_Exp", this.getInitialXP());
        config.set("Leveling.XPModifier", this.getXPModifier());
        config.set("Leveling.Max_Level", this.getMaxLevel());
        config.set("Leveling.Aspect_Points_Per_Level", this.aspectPointsPerLevel);
        config.set("Leveling.Start_Aspect_Points", this.startAspectPoints);
        config.remove("Aspects.Max");
        this.getAspectsMax().forEach((aspect, value) -> {
            config.set("Aspects.Max." + aspect, value);
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    @Override
    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    @Override
    public String getNameFormat() {
        return nameFormat;
    }

    @Override
    public void setNameFormat(@NotNull String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean isCapturable() {
        return this.capturable;
    }

    @Override
    public void setCapturable(boolean capturable) {
        this.capturable = capturable;
    }

    @Override
    public boolean hasInventory() {
        return this.hasInventory && this.getInventorySize() > 0 && this.getInventorySize() % 9 == 0;
    }

    @Override
    public void setHasInventory(boolean hasInventory) {
        this.hasInventory = hasInventory;
    }

    @Override
    public int getInventorySize() {
        return inventorySize;
    }

    @Override
    public void setInventorySize(int inventorySize) {
        this.inventorySize = inventorySize;
    }

    @Override
    public boolean hasEquipment() {
        return this.hasEquipment;
    }

    @Override
    public void setHasEquipment(boolean hasEquipment) {
        this.hasEquipment = hasEquipment;
    }

    @Override
    public int getAutoRespawnTime() {
        return this.autoRespawnTime;
    }

    @Override
    public void setAutoRespawnTime(int autoRespawnTime) {
        this.autoRespawnTime = autoRespawnTime;
    }

    @NotNull
    @Override
    public String getReviveCurrency() {
        return reviveCurrency;
    }

    @Override
    public void setReviveCurrency(@NotNull String reviveCurrency) {
        this.reviveCurrency = reviveCurrency;
    }

    @Override
    public double getReviveCost() {
        return this.reviveCost;
    }

    @Override
    public void setReviveCost(double reviveCost) {
        this.reviveCost = Math.max(0, reviveCost);
    }

    @Override
    public double getInventoryDropChance() {
        return inventoryDropChance;
    }

    @Override
    public void setInventoryDropChance(double inventoryDropChance) {
        this.inventoryDropChance = Math.max(0, inventoryDropChance);
    }

    @Override
    public double getEquipmentDropChance() {
        return equipmentDropChance;
    }

    @Override
    public void setEquipmentDropChance(double equipmentDropChance) {
        this.equipmentDropChance = Math.max(0, equipmentDropChance);
    }

    @Override
    public int getInitialXP() {
        return this.initialXP;
    }

    @Override
    public void setInitialXP(int initialXP) {
        this.initialXP = Math.max(1, initialXP);
    }

    @Override
    public double getXPModifier() {
        return this.xpFactor;
    }

    @Override
    public void setXPModifier(double xpModifier) {
        this.xpFactor = Math.max(1, xpModifier);
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = Math.max(1, maxLevel);
    }

    @Override
    public int getRequiredXP(int level) {
        Map.Entry<Integer, Integer> entry = this.xpMap.floorEntry(level);
        return entry != null ? entry.getValue() : this.getInitialXP();
    }

    @Override
    public int getAspectPointsPerLevel() {
        return this.aspectPointsPerLevel;
    }

    @Override
    public void setAspectPointsPerLevel(int aspectPointsPerLevel) {
        this.aspectPointsPerLevel = Math.max(0, aspectPointsPerLevel);
    }

    @Override
    public int getStartAspectPoints() {
        return startAspectPoints;
    }

    @Override
    public void setStartAspectPoints(int startAspectPoints) {
        this.startAspectPoints = Math.max(0, startAspectPoints);
    }

    @Override
    @NotNull
    public Map<String, Integer> getAspectsMax() {
        return this.aspectsMax;
    }
}
