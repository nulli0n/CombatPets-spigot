package su.nightexpress.combatpets.pet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.*;
import su.nightexpress.combatpets.api.pet.event.generic.PetReleaseEvent;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.combatpets.pet.impl.*;
import su.nightexpress.combatpets.pet.listener.CombatListener;
import su.nightexpress.combatpets.pet.listener.LevelledMobsListener;
import su.nightexpress.combatpets.pet.listener.PetGenericListener;
import su.nightexpress.combatpets.pet.listener.PlayerGenericListener;
import su.nightexpress.combatpets.pet.menu.*;
import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PetManager extends AbstractManager<PetsPlugin> {

    private final Map<String, Tier>     tierMap;
    private final Map<String, Template> templateMap;
    private final Map<String, Aspect>   aspectMap;
    private final Map<String, FoodCategory> foodMap;

    private TiersMenu      tiersMenu;
    private CollectionMenu collectionMenu;
    private PetMenu        petMenu;
    private AspectsMenu    aspectsMenu;
    private ReleaseMenu    releaseMenu;
    private ReviveMenu     reviveMenu;

    public PetManager(@NotNull PetsPlugin plugin) {
        super(plugin);
        this.tierMap = new HashMap<>();
        this.templateMap = new HashMap<>();
        this.aspectMap = new HashMap<>();
        this.foodMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadAttributes();
        this.loadAspects();
        this.loadFood();
        this.loadTiers();
        this.loadPets();
        this.loadUI();

        this.addListener(new PetGenericListener(this.plugin, this));
        this.addListener(new CombatListener(this.plugin, this));
        this.addListener(new PlayerGenericListener(this.plugin, this));
        if (Plugins.isInstalled(HookId.LEVELLED_MOBS)) {
            this.addListener(new LevelledMobsListener(this.plugin));
        }

        this.addTask(this.plugin.createTask(this::tickPets).setSecondsInterval(1));
        this.addTask(this.plugin.createTask(this::regeneratePets).setTicksInterval(5L));
    }

    @Override
    protected void onShutdown() {
        PetManager.getActivePets().forEach(holder -> {
            this.removePet(holder);
            this.plugin.getDataHandler().saveUser(plugin.getUserManager().getOrFetch(holder.getOwner()));
        });

        if (this.collectionMenu != null) this.collectionMenu.clear();
        if (this.petMenu != null) this.petMenu.clear();
        if (this.aspectsMenu != null) this.aspectsMenu.clear();
        if (this.releaseMenu != null) this.releaseMenu.clear();
        if (this.reviveMenu != null) this.reviveMenu.clear();
        if (this.tiersMenu != null) this.tiersMenu.clear();

        this.tierMap.clear();
        this.templateMap.clear();
        this.aspectMap.clear();
        this.foodMap.clear();

        AttributeRegistry.clear();
    }

    private void loadTiers() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_TIERS);
        if (!dir.exists() && dir.mkdirs()) {
            PetCreator.createTiers(this.plugin);
        }

        for (File file : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_TIERS)) {
            Tier tier = new PetTier(this.plugin, file);
            if (tier.load()) {
                this.tierMap.put(tier.getId(), tier);
            }
            else this.plugin.warn("Tier not loaded: '" + file.getName() + "' !");
        }

        this.plugin.info("Loaded " + this.tierMap.size() + " pet tiers!");
    }

    private void loadPets() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_PETS);
        if (!dir.exists() && dir.mkdirs()) {
            PetCreator.createConfigs(this.plugin);
        }

        for (File config : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_PETS)) {
            Template petConfig = new PetTemplate(this.plugin, config);
            if (petConfig.load()) {
                this.templateMap.put(petConfig.getId(), petConfig);
            }
            else this.plugin.warn("Pet not loaded: '" + config.getName() + "' !");
        }

        this.plugin.info("Loaded " + this.templateMap.size() + " pet configs!");
    }

    private void loadFood() {
        FileConfig config = this.plugin.getConfig();
        if (!config.contains("Food")) {
            PetCreator.getDefaultFoods().forEach(category -> category.write(config, "Food." + category.getId()));
        }

        config.getSection("Food").forEach(sId -> {
            PetFoodCategory category = PetFoodCategory.read(config, "Food." + sId, sId);
            this.foodMap.put(category.getId(), category);
        });

        this.plugin.info("Loaded " + this.foodMap.size() + " food categories.");
    }

    private void loadAspects() {
        FileConfig config = this.plugin.getConfig();
        if (!config.contains("Aspects")) {
            PetCreator.getDefaultAspects().forEach(aspect -> aspect.write(config, "Aspects." + aspect.getId()));
        }

        config.getSection("Aspects").forEach(sId -> {
            Aspect aspect = PetAspect.read(config, "Aspects." + sId, sId);
            this.aspectMap.put(aspect.getId(), aspect);
        });

        this.plugin.info("Loaded " + this.aspectMap.size() + " aspects.");
    }

    private void loadAttributes() {
        this.loadAttribute(AttributeRegistry.ARMOR, Attribute.ARMOR);
        this.loadAttribute(AttributeRegistry.ATTACK_DAMAGE, Attribute.ATTACK_DAMAGE);
        this.loadAttribute(AttributeRegistry.ATTACK_KNOCKBACK, Attribute.ATTACK_KNOCKBACK);
        this.loadAttribute(AttributeRegistry.ATTACK_SPEED, Attribute.ATTACK_SPEED);
        this.loadAttribute(AttributeRegistry.FLYING_SPEED, Attribute.FLYING_SPEED);
        this.loadAttribute(AttributeRegistry.HEALTH_REGENEATION_FORCE);
        this.loadAttribute(AttributeRegistry.HEALTH_REGENEATION_SPEED);
        this.loadAttribute(AttributeRegistry.HORSE_JUMP_STRENGTH, Attribute.JUMP_STRENGTH);
        this.loadAttribute(AttributeRegistry.KNOCKBACK_RESISTANCE, Attribute.KNOCKBACK_RESISTANCE);
        this.loadAttribute(AttributeRegistry.MAX_HEALTH, Attribute.MAX_HEALTH);
        this.loadAttribute(AttributeRegistry.MAX_SATURATION);
        this.loadAttribute(AttributeRegistry.MOVEMENT_SPEED, Attribute.MOVEMENT_SPEED);
            //this.loadAttribute(AttributeRegistry.ATTACK_REACH, Attribute.valueOf("GENERIC_ATTACK_REACH"));
        this.loadAttribute(AttributeRegistry.BURNING_TIME, Attribute.BURNING_TIME);
        this.loadAttribute(AttributeRegistry.EXPLOSION_KNOCKBACK_RESISTANCE, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE);
        this.loadAttribute(AttributeRegistry.FALL_DAMAGE_MULTIPLIER, Attribute.FALL_DAMAGE_MULTIPLIER);
        this.loadAttribute(AttributeRegistry.GRAVITY, Attribute.GRAVITY);
        this.loadAttribute(AttributeRegistry.MOVEMENT_EFFICIENCY, Attribute.MOVEMENT_EFFICIENCY);
        this.loadAttribute(AttributeRegistry.SAFE_FALL_DISTANCE, Attribute.SAFE_FALL_DISTANCE);
        this.loadAttribute(AttributeRegistry.SCALE, Attribute.SCALE);
        this.loadAttribute(AttributeRegistry.STEP_HEIGHT, Attribute.STEP_HEIGHT);
        this.loadAttribute(AttributeRegistry.WATER_MOVEMENT_EFFICIENCY, Attribute.WATER_MOVEMENT_EFFICIENCY);
    }

    private void loadAttribute(@NotNull String name) {
        NamespacedKey key = new NamespacedKey(this.plugin, "attribute." + name.toLowerCase());
        this.loadAttribute(new PetAttribute(name, key));
    }

    private void loadAttribute(@NotNull String name, @NotNull Attribute vanillaMirror) {
        this.loadAttribute(new PetAttribute(name, vanillaMirror));
    }

    private void loadAttribute(@NotNull PetAttribute attribute) {
        FileConfig config = this.plugin.getConfig();

        attribute.read(config, "Attributes." + attribute.getId());

        AttributeRegistry.register(attribute);
        this.plugin.info("Registered '" + attribute.getId() + "' pet attribute.");
    }

    private void loadUI() {
        this.collectionMenu = new CollectionMenu(this.plugin);
        this.tiersMenu = new TiersMenu(this.plugin);
        this.petMenu = new PetMenu(this.plugin);
        this.aspectsMenu = new AspectsMenu(this.plugin);
        this.releaseMenu = new ReleaseMenu(this.plugin);
        this.reviveMenu = new ReviveMenu(this.plugin);
    }

    public void regeneratePets() {
        getActivePets().forEach(holder -> {
            holder.doRegenerate(EntityRegainHealthEvent.RegainReason.REGEN);
        });
    }

    public void tickPets() {
        getActivePets().forEach(ActivePet::tickPet);
    }

    @NotNull
    public Map<String, Tier> getTierMap() {
        return this.tierMap;
    }

    @NotNull
    public Set<Tier> getTiers() {
        return new HashSet<>(this.tierMap.values());
    }

    @NotNull
    public List<String> getTierIds() {
        return new ArrayList<>(this.tierMap.keySet());
    }

    @Nullable
    public Tier getTier(@NotNull String id) {
        return this.tierMap.get(id.toLowerCase());
    }

    @NotNull
    public Tier getTierByWeight() {
        Map<Tier, Double> map = new HashMap<>();
        this.getTiers().forEach(tier -> {
            map.put(tier, tier.getWeight());
        });
        return Rnd.getByWeight(map);
    }

    @NotNull
    public Map<String, Template> getTemplateMap() {
        return this.templateMap;
    }

    @NotNull
    public Set<Template> getTemplates() {
        return new HashSet<>(this.templateMap.values());
    }

    @NotNull
    public List<String> getTemplateIds() {
        return new ArrayList<>(this.templateMap.keySet());
    }

    @Nullable
    public Template getTemplate(@NotNull String id) {
        return this.templateMap.get(id.toLowerCase());
    }

    @Nullable
    public Template getTemplate(@NotNull LivingEntity entity) {
        return this.getTemplates().stream()
            .filter(config -> config.getEntityType() == entity.getType()).findAny().orElse(null);
    }

    @NotNull
    public Set<Aspect> getAspects() {
        return new HashSet<>(this.aspectMap.values());
    }

    @Nullable
    public Aspect getAspect(@NotNull String id) {
        return this.aspectMap.get(id.toLowerCase());
    }

    @Nullable
    public FoodItem getFoodItem(@NotNull String categoryName, @NotNull String itemName) {
        FoodCategory category = this.getFoodCategory(categoryName);
        if (category == null) return null;

        return category.getItem(itemName.toLowerCase());
    }

    @Nullable
    public FoodItem getFoodItem(@NotNull ItemStack itemStack) {
        return this.getFoodCategories().stream().map(category -> category.getItem(itemStack)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Nullable
    public FoodCategory getFoodCategory(@NotNull String id) {
        return this.foodMap.get(id.toLowerCase());
    }

    @NotNull
    public Set<FoodCategory> getFoodCategories() {
        return new HashSet<>(this.foodMap.values());
    }

    @NotNull
    public Set<FoodItem> getFoodItems() {
        return this.getFoodCategories().stream().flatMap(category -> category.getItems().stream()).collect(Collectors.toSet());
    }

    @NotNull
    public List<String> getFoodCategoryNames() {
        return new ArrayList<>(this.foodMap.keySet());
    }

    @NotNull
    public static Collection<ActivePet> getActivePets() {
        return PetEntityBridge.getAll();
    }

    @Nullable
    public ActivePet getPlayerPet(@NotNull Player player) {
        return PetEntityBridge.getByPlayer(player);
    }

    @Nullable
    public ActivePet getPetByMob(@NotNull LivingEntity entity) {
        return PetEntityBridge.getByMob(entity);
    }

    public boolean isPetEntity(@NotNull LivingEntity entity) {
        return PetEntityBridge.isPet(entity);
    }

    public boolean hasActivePet(@NotNull Player player) {
        return this.getPlayerPet(player) != null;
    }


    public void openTierCollection(@NotNull Player player) {
        if (!plugin.getUserManager().getOrFetch(player).isLoaded()) return;

        this.tiersMenu.open(player);
    }

    public void openPetsCollection(@NotNull Player player, @NotNull Tier tier) {
        if (!plugin.getUserManager().getOrFetch(player).isLoaded()) return;

        this.collectionMenu.open(player, tier);
    }

    public void openOverviewMenu(@NotNull Player player) {
        this.petMenu.open(player);
    }

    public void openAspectsMenu(@NotNull Player player) {
        this.aspectsMenu.open(player);
    }

    public void openReleaseMenu(@NotNull Player player, @NotNull PetData data) {
        this.releaseMenu.open(player, data);
    }

    public void openReviveMenu(@NotNull Player player, @NotNull PetData data) {
        this.reviveMenu.open(player, data);
    }

    public boolean openPetMenu(@NotNull Player player) {
        ActivePet petHolder = getPlayerPet(player);
        if (petHolder == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        petHolder.openMenu();
        return true;
    }

    public static int getPetsLimit(@NotNull Player player) {
        return Config.PET_AMOUNT_PER_RANK.get().getGreatestOrNegative(player);
    }

    public boolean spawnPet(@NotNull Player player, @NotNull Tier tier, @NotNull Template config) {
        PetUser user = plugin.getUserManager().getOrFetch(player);
        PetData petData = user.getPet(config, tier);
        if (petData == null) return false;

        return this.spawnPet(player, petData);
    }

    public boolean spawnPet(@NotNull Player player, @NotNull PetData petData) {
        if (this.hasActivePet(player)) {
            Lang.PET_SPAWN_ERROR_ALREADY.message().send(player);
            return false;
        }
        if (petData.isDead()) {
            Lang.PET_SPAWN_ERROR_DEAD.message().send(player);
            return false;
        }
        if (!PetUtils.isAllowedPetZone(player.getLocation())) {
            Lang.PET_ERROR_BAD_WORLD.message().send(player);
            return false;
        }

        Location location = player.getLocation().clone();
        Vector direction = location.getDirection();
        location.add(0, 1, 0).add(direction.multiply(1.5));
        Block block = location.getBlock();
        if (!block.isEmpty() && block.getType().isSolid()) {
            Lang.PET_SPAWN_ERROR_BAD_PLACE.message().send(player);
            return false;
        }

        location.getChunk(); // Force load chunk before pet spawn.

        Template template = petData.getTemplate();
        ActivePet pet = plugin.getPetNMS().spawnPet(template, location, entity -> new PetInstance(this.plugin, player, entity, petData));
        pet.handleSpawn();

        return true;
    }

    public void handleDeath(@NotNull ActivePet holder) {
        Player player = holder.getOwner();
        PetUser user = this.plugin.getUserManager().getOrFetch(player);

        holder.handleDeath();

        this.plugin.getUserManager().save(user);
    }

    public void despawnPet(@NotNull Player player) {
        ActivePet holder = getPlayerPet(player);
        if (holder == null) return;

        this.despawnPet(holder);
    }

    public void despawnPet(@NotNull ActivePet holder) {
        this.removePetAndSave(holder);
        Lang.PET_DESPAWN_DEFAULT.message().send(holder.getOwner());
    }

    public void removePet(@NotNull ActivePet holder) {
        holder.remove();
    }

    public void removePetAndSave(@NotNull ActivePet holder) {
        this.removePet(holder);
        PetUser user = this.plugin.getUserManager().getOrFetch(holder.getOwner());
        this.plugin.getUserManager().save(user);
    }

    @NotNull
    public PetData addToCollection(@NotNull PetUser user, @NotNull Tier tier, @NotNull Template template) {
        PetData petData = PetData.create(template, tier);
        user.addPet(petData);
        this.plugin.getUserManager().save(user);

        return petData;
    }

    public void removeFromCollection(@NotNull PetUser user, @NotNull Tier tier, @NotNull Template template) {
        Player player = user.getPlayer();
        ActivePet activePet = player == null ? null : this.getPlayerPet(player);
        if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
            this.removePet(activePet);
        }
        user.removePet(template, tier);
        this.plugin.getUserManager().save(user);
    }

    @Nullable
    public PetData tryClaimPet(@NotNull Player player, @NotNull Tier tier, @NotNull Template config) {
        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        if (!user.isLoaded()) return null;

        if (user.hasPet(config, tier)) {
            Lang.PET_CLAIM_ERROR_ALREADY_HAVE.message().send(player);
            return null;
        }

        int petLimit = PetManager.getPetsLimit(player);
        int petCollected = user.getPets(tier).size();
        if (petLimit > 0 && petCollected >= petLimit) {
            Lang.PET_CLAIM_ERROR_REACHED_LIMIT.message().send(player, replacer -> replacer
                .replace(tier.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, petLimit)
            );
            return null;
        }

        Lang.PET_CLAIM_SUCCESS.message().send(player);
        return this.addToCollection(user, tier, config);
    }

    public boolean revivePet(@NotNull Player player, @NotNull PetData petData) {
        Tier tier = petData.getTier();

        Currency currency = EconomyBridge.getCurrency(tier.getReviveCurrency());
        if (currency == null) return false;

        double cost = tier.getReviveCost();

        if (cost > 0) {
            if (currency.getBalance(player) < cost) {
                Lang.PET_REVIVE_ERROR_NOT_ENOUGH_FUNDS.message().send(player, replacer -> replacer
                    .replace(Placeholders.PET_NAME, petData.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(cost))
                );
                return false;
            }
            currency.take(player, cost);
        }

        petData.revive();

        Lang.PET_REVIVE_SUCCESS.message().send(player, replacer -> replacer
            .replace(Placeholders.PET_NAME, petData.getName())
            .replace(Placeholders.GENERIC_AMOUNT, currency.format(cost))
        );
        return true;
    }

    public boolean releasePet(@NotNull Player player, @NotNull PetData petData) {
        if (!Config.PET_RELEASE_ALLOWED.get() && !player.hasPermission(Perms.BYPASS_RELEASE_DISABLED)) {
            CoreLang.ERROR_NO_PERMISSION.withPrefix(this.plugin).send(player);
            return false;
        }

        if (Config.PET_RELEASE_DISABLED_WORLDS.get().contains(player.getWorld().getName())) {
            Lang.PET_RELEASE_ERROR_BAD_WORLD.message().send(player);
            return false;
        }

        Template template = petData.getTemplate();
        Tier tier = petData.getTier();

        PetReleaseEvent event = new PetReleaseEvent(player, tier, template);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (Config.PET_RELEASE_NATURAL.get()) {
            Location location = player.getLocation();
            Block against = location.getBlock();
            Block placed = against.getRelative(BlockFace.UP);
            ItemStack item = new ItemStack(Material.STONE);
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(placed, placed.getState(), against, item, player, true, EquipmentSlot.HAND);
            plugin.getPluginManager().callEvent(placeEvent);
            if (placeEvent.isCancelled()) {
                Lang.PET_RELEASE_ERROR_PROTECTED_AREA.message().send(player);
                return false;
            }

            if (PetUtils.isAllowedPetZone(location) && petData.isAlive()) {
                Entity entity = player.getWorld().spawnEntity(location, template.getEntityType());
                if (entity instanceof LivingEntity livingEntity) {
                    petData.getWardrobe().dressUp(livingEntity);
                }
                entity.setVelocity(location.getDirection().multiply(3));

            }
        }

        this.removeFromCollection(plugin.getUserManager().getOrFetch(player), tier, template);
        Lang.PET_RELEASE_SUCCESS.message().send(player, replacer -> replacer.replace(petData.replacePlaceholders()));
        return true;
    }

    public boolean tryInteract(@NotNull Player player, @NotNull LivingEntity entity) {
        ActivePet petHolder = this.getPetByMob(entity);
        if (petHolder == null) return false;

        if (!petHolder.isOwner(player)) {
            Lang.PET_ERROR_NOT_YOUR.message().send(player);
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        FoodItem foodItem = this.getFoodItem(handItem);
        if (foodItem != null && petHolder.getTemplate().isFood(foodItem) && petHolder.doFeed(foodItem)) {
            handItem.setAmount(handItem.getAmount() - 1);
            return true;
        }

        if (handItem.getType() == Material.NAME_TAG && Config.PET_NAME_RENAME_ALLOW_NAMETAGS.get()) {
            ItemMeta meta = handItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                if (this.tryRename(player, meta.getDisplayName())) {
                    handItem.setAmount(handItem.getAmount() - 1);
                }
            }
            return true;
        }

        if (petHolder.isEquipmentUnlocked() && !handItem.getType().isAir()) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                EquipmentSlot slot = EquipmentSlot.HAND;
                if (ItemUtil.isHelmet(handItem)) slot = EquipmentSlot.HEAD;
                else if (ItemUtil.isChestplate(handItem)) slot = EquipmentSlot.CHEST;
                else if (ItemUtil.isLeggings(handItem)) slot = EquipmentSlot.LEGS;
                else if (ItemUtil.isBoots(handItem)) slot = EquipmentSlot.FEET;
                else if (handItem.getType() == Material.SHIELD) slot = EquipmentSlot.OFF_HAND;

                if (equipment.getItem(slot).getType().isAir()) {
                    equipment.setItem(slot, new ItemStack(handItem));
                    handItem.setAmount(0);

                    petHolder.saveData();
                    PetUser user = this.plugin.getUserManager().getOrFetch(player);
                    this.plugin.getUserManager().save(user);
                    NightSound.of(Sound.ITEM_ARMOR_EQUIP_LEATHER).play(entity.getLocation());
                }
                return true;
            }
        }

        boolean isRideable = entity instanceof AbstractHorse;
        boolean needSneak = Config.PET_SNEAK_TO_OPEN_MENU.get();
        if (isRideable) needSneak = !needSneak;

        boolean isMenu = needSneak == player.isSneaking();

        if (isMenu) {
            petHolder.openMenu();
        }
        else if (entity instanceof AbstractHorse horse) {
            horse.addPassenger(player);
        }
        return true;
    }

    public boolean startRename(@NotNull Player player) {
        if (!this.hasActivePet(player)) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        boolean nametagRequired = Config.PET_NAME_RENAME_MENU_REQUIRES_NAMETAG.get();
        if (nametagRequired) {
            if (Players.countItem(player, Material.NAME_TAG) < 1) {
                Lang.PET_RENAME_ERROR_NO_NAMETAG.message().send(player);
                return false;
            }
            Players.takeItem(player, Material.NAME_TAG, 1);
        }

        Lang.PET_RENAME_PROMPT.message().send(player);
        Dialog.create(player, (dialog, input) -> {
            this.plugin.runTask(task -> {
                if (!this.tryRename(player, input.getText()) && nametagRequired) {
                    Players.addItem(player, new ItemStack(Material.NAME_TAG));
                }
            });
            return true;
        });

        return true;
    }

    public boolean tryRename(@NotNull Player player, @NotNull String name) {
        ActivePet holder = this.getPlayerPet(player);
        if (holder == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        String rawName = NightMessage.stripAll(name);
        if (rawName.isEmpty()) return false;

        if (!player.hasPermission(Perms.BYPASS_NAME_WORDS)) {
            if (Config.PET_NAME_BLOCKED_WORDS.get().stream().anyMatch(rawName::contains)) {
                Lang.PET_RENAME_ERROR_FORBIDDEN.message().send(player);
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_NAME_LENGTH)) {
            int max = Config.PET_NAME_LENGTH_MAX.get();
            if (rawName.length() > max) {
                Lang.PET_RENAME_ERROR_TOO_LONG.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_AMOUNT, max));
                return false;
            }

            int min = Config.PET_NAME_LENGTH_MIN.get();
            if (rawName.length() < min) {
                Lang.PET_RENAME_ERROR_TOO_SHORT.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_AMOUNT, min));
                return false;
            }
        }

        holder.setName(name);
        Lang.PET_RENAME_SUCCESS.message().send(player, replacer -> replacer.replace(Placeholders.PET_NAME, holder.getName()));
        return true;
    }

    public boolean canDamage(@NotNull LivingEntity damager, @NotNull LivingEntity victim) {
        ActivePet damagerPet = this.getPetByMob(damager);
        if (damagerPet != null) {
            return this.canBeDamaged(damagerPet, victim);
        }

        ActivePet victimPet = this.getPetByMob(victim);
        if (victimPet != null) {
            return this.canBeDamaged(victimPet, damager);
        }

        return true;
    }

    public boolean canBeDamaged(@NotNull ActivePet pet, @NotNull LivingEntity entity) {
        if (entity == pet.getOwner()) return false;
        if (entity == pet.getEntity()) return true;
        if (Config.PET_PVP_ALLOWED.get()) return true;

        return (!(entity instanceof Player) && !this.isPetEntity(entity));
    }
}
