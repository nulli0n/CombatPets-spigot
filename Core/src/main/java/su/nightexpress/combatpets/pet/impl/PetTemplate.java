package su.nightexpress.combatpets.pet.impl;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.wrapper.UniParticle;
import su.nightexpress.nightcore.util.wrapper.UniSound;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PetTemplate extends AbstractFileData<PetsPlugin> implements Template {

    private String      defaultName;
    private EntityType  entityType;
    private String      eggTexture;
    private boolean     canHaveInventory;
    private boolean     canHaveEquipment;
    private boolean     capturable;
    private double      captureEscapeChance;
    private double      captureChance;
    private Set<String> foodCategories;
    private NightSound       eatSound;
    private UniParticle spawnParticle;
    private UniParticle despawnParticle;

    private final Map<ExhaustReason, Double> exhaustModifier;
    private final Map<String, Double>        attributesStart;
    private final Map<String, Double>        attributesPerAspect;

    private final PlaceholderMap placeholderMap;

    public PetTemplate(@NotNull PetsPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.exhaustModifier = new HashMap<>();
        this.attributesStart = new HashMap<>();
        this.attributesPerAspect = new HashMap<>();

        this.placeholderMap = Placeholders.forPet(this);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setEntityType(ConfigValue.create("Entity_Type",
            EntityType.class, EntityType.UNKNOWN,
            "Sets pet mob type.",
            Placeholders.WIKI_PET_TYPES_URL
        ).read(config));

        if (!this.plugin.getPetNMS().canSpawn(this.getEntityType())) {
            this.plugin.warn("Invalid entity type for the '" + this.getFile().getName() + "' pet!");
            return false;
        }

        this.setDefaultName(ConfigValue.create("Default_Name",
            LangAssets.get(this.entityType),
            "Sets default pet name when obtained."
        ).read(config));

        String oldTexture = config.getString("Egg_Texture");
        if (oldTexture != null && !oldTexture.isEmpty()) {
            String url = PetUtils.extractBase64TextureURL(oldTexture);
            config.set("Egg_Skin", url);
            config.remove("Egg_Texture");
        }

        this.setEggTexture(ConfigValue.create("Egg_Skin",
            "e4aacb8a36e227d861ea44366ce3b3510ee7c2722c4ea51b2f2e83749bf124",
            "Texture URL value for pet egg item.",
            "Get some at http://minecraft-heads.com"
        ).read(config));

        this.setInventory(ConfigValue.create("Can_Have_Inventory",
            true,
            "Sets whether or not this pet can have an inventory.",
            "Setting this to 'false' will disable inventory for this pet ignoring it's tier inventory option."
        ).read(config));

        this.setEquipment(ConfigValue.create("Can_Have_Equipment",
            true,
            "Sets whether or not this pet can wear items in his equipment slots.",
            "Setting this to 'false' will disable equipment for this pet ignoring it's tier equipment option."
        ).read(config));

        this.setCapturable(ConfigValue.create("Catchable",
            true,
            "Sets whether or not this pet can be captured by a player.",
            "Setting this to 'false' will disable capture for this pet ignoring it's tier capture options."
        ).read(config));

        this.setCaptureChance(ConfigValue.create("Capture.Chance",
            30D,
            "Sets the capture chance for this type of pet."
        ).read(config));

        this.setCaptureEscapeChance(ConfigValue.create("Capture.Escape_Chance",
            0.5D,
            "Sets the chance to escape capturing for this type of pet."
        ).read(config));

        this.setEatSound(ConfigValue.create("Saturation.Eat_Sound",
            NightSound.of(Sound.ENTITY_GENERIC_EAT),
            "Sets sound to play when pet ates food item."
        ).read(config));

        this.setSpawnParticle(ConfigValue.create("Spawn_Particle",
            UniParticle.of(Particle.CLOUD),
            "Sets particle effect for pet spawn."
        ).read(config));

        this.setDespawnParticle(ConfigValue.create("Despawn_Particle",
            UniParticle.of(Particle.ASH),
            "Sets particle effect for pet despawn."
        ).read(config));

        this.exhaustModifier.putAll(ConfigValue.create("Saturation.Exhaust",
            (cfg, path, def) -> Stream.of(ExhaustReason.values()).collect(Collectors.toMap(reason -> reason, amount -> cfg.getDouble(path + "." + amount.name()))),
            (cfg, path, map) -> map.forEach((reason, amount) -> cfg.set(path + "." + reason.name(), amount)),
            () -> Map.of(ExhaustReason.IDLE, 0.01, ExhaustReason.WALK, 0.02, ExhaustReason.COMBAT, 0.04),
            "Sets amount of saturation to lost on every tick for certain pet state."
        ).read(config));

        this.setFoodCategories(ConfigValue.create("Saturation.FoodCategories",
            new HashSet<>(),
            "List of food categories available to feed this pet.",
            "You can create or edit them in the main plugin config file.",
            "Available categories: [" + String.join(", ", plugin.getPetManager().getFoodCategoryNames()) + "]",
            Placeholders.WIKI_FOOD_URL
        ).read(config));

        this.attributesStart.putAll(ConfigValue.forMap("Attributes.Default",
            (cfg, path, name) -> cfg.getDouble(path + "." + name),
            (cfg, path, map) -> map.forEach((name, value) -> cfg.set(path + "." + name, value)),
            () -> Map.of(
                AttributeRegistry.MAX_HEALTH, 20D,
                AttributeRegistry.ATTACK_DAMAGE, 2D,
                AttributeRegistry.ATTACK_SPEED, 0.5D,
                AttributeRegistry.ARMOR, 0.5D,
                AttributeRegistry.MOVEMENT_SPEED, 0.2D,
                AttributeRegistry.MAX_SATURATION, 15D,
                AttributeRegistry.HEALTH_REGENEATION_FORCE, 0.5D,
                AttributeRegistry.HEALTH_REGENEATION_SPEED, 3.5D
            ),
            "Default (initial) attribute values for this pet.",
            Placeholders.WIKI_ATTRIBUTES_URL
        ).read(config));

        this.attributesPerAspect.putAll(ConfigValue.forMap("Attributes.Per_Aspect",
            (cfg, path, name) -> cfg.getDouble(path + "." + name),
            (cfg, path, map) -> map.forEach((name, value) -> cfg.set(path + "." + name, value)),
            () -> Map.of(
                AttributeRegistry.MAX_HEALTH, 1D,
                AttributeRegistry.ATTACK_DAMAGE, 0.15D,
                AttributeRegistry.ATTACK_SPEED, 0.05D,
                AttributeRegistry.ARMOR, 0.05D,
                AttributeRegistry.MOVEMENT_SPEED, 0.01D,
                AttributeRegistry.MAX_SATURATION, 0.2D,
                AttributeRegistry.HEALTH_REGENEATION_FORCE, 0.05D,
                AttributeRegistry.HEALTH_REGENEATION_SPEED, -0.01D
            ),
            "Attribute values added per each aspect value.",
            Placeholders.WIKI_ATTRIBUTES_URL,
            Placeholders.WIKI_ASPECTS_URL
        ).read(config));

        this.attributesStart.values().removeIf(value -> value == 0D);
        this.attributesPerAspect.values().removeIf(value -> value == 0D);

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Entity_Type", this.getEntityType().name().toLowerCase());
        config.set("Default_Name", this.getDefaultName());
        config.set("Egg_Skin", this.getEggTexture());
        config.set("Can_Have_Inventory", this.canHaveInventory());
        config.set("Can_Have_Equipment", this.canHaveEquipment());
        config.set("Catchable", this.isCapturable());
        this.getSpawnParticle().write(config, "Spawn_Particle");
        this.getDespawnParticle().write(config, "Despawn_Particle");
        config.set("Saturation.Eat_Sound", this.getEatSound());
        config.remove("Saturation.Exhaust");
        this.exhaustModifier.forEach((reason, mod) -> {
            config.set("Saturation.Exhaust." + reason.name(), mod);
        });
        config.set("Saturation.FoodCategories", this.getFoodCategories());
        config.remove("Attributes");
        this.getAttributesStart().forEach((name, value) -> {
            config.set("Attributes.Default." + name, value);
        });
        this.getAttributesPerAspect().forEach((name, value) -> {
            config.set("Attributes.Per_Aspect." + name, value);
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public ItemStack createEgg(@NotNull Tier tier) {
        return this.plugin.getItemManager().createEgg(this, tier);
//        ItemStack item = PetUtils.getRawEggItem(this);
//        ItemUtil.editMeta(item, meta -> {
//            PDCUtil.set(meta, Keys.eggPetId, this.getId());
//            PDCUtil.set(meta, Keys.eggTierId, tier.getId());
//        });
//
//        ItemReplacer.create(item).readMeta()
//            .replace(this.getPlaceholders())
//            .replace(tier.getPlaceholders())
//            .writeMeta();
//
//        return item;
    }

    @Override
    public boolean isFood(@NotNull ItemStack itemStack) {
        FoodItem foodItem = this.plugin.getPetManager().getFoodItem(itemStack);
        return foodItem != null && this.isFood(foodItem);
    }

    @Override
    @NotNull
    public String getDefaultName() {
        return defaultName;
    }

    @Override
    public void setDefaultName(@NotNull String defaultName) {
        this.defaultName = defaultName;
    }

    @Override
    @NotNull
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void setEntityType(@NotNull EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    @NotNull
    public String getEggTexture() {
        return eggTexture;
    }

    @Override
    public void setEggTexture(@NotNull String eggTexture) {
        this.eggTexture = eggTexture;
    }

    @Override
    public boolean canHaveInventory() {
        return this.canHaveInventory;
    }

    @Override
    public void setInventory(boolean inventory) {
        this.canHaveInventory = inventory;
    }

    @Override
    public boolean canHaveEquipment() {
        return this.canHaveEquipment;
    }

    @Override
    public void setEquipment(boolean equipment) {
        this.canHaveEquipment = equipment;
    }

    @Override
    public boolean isCapturable() {
        return capturable;
    }

    @Override
    public void setCapturable(boolean capturable) {
        this.capturable = capturable;
    }

    @Override
    public double getCaptureEscapeChance() {
        return captureEscapeChance;
    }

    @Override
    public void setCaptureEscapeChance(double captureEscapeChance) {
        this.captureEscapeChance = captureEscapeChance;
    }

    @Override
    public double getCaptureChance() {
        return captureChance;
    }

    @Override
    public void setCaptureChance(double captureChance) {
        this.captureChance = captureChance;
    }

    @Override
    @NotNull
    public String getCapturePermission() {
        return Perms.PREFIX_CAPTURE + this.getId();
    }

    @NotNull
    @Override
    public NightSound getEatSound() {
        return eatSound;
    }

    @Override
    public void setEatSound(@NotNull NightSound eatSound) {
        this.eatSound = eatSound;
    }

    @NotNull
    @Override
    public UniParticle getSpawnParticle() {
        return spawnParticle;
    }

    @Override
    public void setSpawnParticle(@NotNull UniParticle spawnParticle) {
        this.spawnParticle = spawnParticle;
    }

    @NotNull
    @Override
    public UniParticle getDespawnParticle() {
        return despawnParticle;
    }

    @Override
    public void setDespawnParticle(@NotNull UniParticle despawnParticle) {
        this.despawnParticle = despawnParticle;
    }

    @NotNull
    @Override
    public Set<String> getFoodCategories() {
        return foodCategories;
    }

    @Override
    public void setFoodCategories(@NotNull Set<String> foodCategories) {
        this.foodCategories = new HashSet<>();
        foodCategories.forEach(name -> this.foodCategories.add(name.toLowerCase()));
    }

    @Override
    public double getExhaustModifier(@NotNull ExhaustReason reason) {
        return this.exhaustModifier.getOrDefault(reason, 0D);
    }

    @Override
    public void setExhaustModifier(@NotNull ExhaustReason reason, double mod) {
        this.exhaustModifier.put(reason, mod);
    }

    @Override
    @NotNull
    public Map<String, Double> getAttributesStart() {
        return this.attributesStart;
    }

    @Override
    @NotNull
    public Map<String, Double> getAttributesPerAspect() {
        return this.attributesPerAspect;
    }
}
