package su.nightexpress.combatpets.capture;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.event.capture.PetCaptureStartEvent;
import su.nightexpress.combatpets.capture.config.CaptureConfig;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.combatpets.hook.impl.MythicMobsHook;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class CaptureManager extends AbstractManager<PetsPlugin> {

    private static final String FILE_NAME = "capturing.yml";

    private final Map<UUID, CaptureTask> captureMap;

    public CaptureManager(@NotNull PetsPlugin plugin) {
        super(plugin);
        this.captureMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.addListener(new CaptureListener(this.plugin, this));

        this.addTask(this::tickCaptures, 1L);
    }

    @Override
    protected void onShutdown() {
        this.captureMap.values().forEach(CaptureTask::stop);
        this.captureMap.clear();
    }

    @NotNull
    public FileConfig getConfig() {
        return FileConfig.loadOrExtract(this.plugin, FILE_NAME);
    }

    private void loadConfig() {
        FileConfig config = this.getConfig();
        config.initializeOptions(CaptureConfig.class);
        config.saveChanges();
    }

    public void tickCaptures() {
        this.captureMap.values().forEach(CaptureTask::tick);
        this.captureMap.values().removeIf(Predicate.not(CaptureTask::isRunning));
    }

    public boolean isCapturing(@NotNull Player player) {
        return this.getCaptureProcess(player.getUniqueId()) != null;
    }

    public boolean isBeingCaptured(@NotNull LivingEntity entity) {
        return this.captureMap.values().stream().anyMatch(captureTask -> captureTask.getEntity() == entity);
    }

    @Nullable
    public CaptureTask getCaptureProcess(@NotNull UUID playerId) {
        return this.captureMap.get(playerId);
    }

    public boolean tryCapture(@NotNull Player player, @NotNull LivingEntity entity, @NotNull ItemStack itemStack) {
        if (!PetUtils.isAllowedPetZone(entity.getLocation())) return false;

        String name = entity.getCustomName();
        String localized = name == null ? LangAssets.get(entity.getType()) : name;

        if (!this.canBeCaptured(entity)) {
            Lang.CAPTURE_ERROR_NOT_CAPTURABLE.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_NAME, localized));
            return false;
        }

        Template template = this.plugin.getPetManager().getTemplate(entity);
        if (template == null || !template.isCapturable()) return false;

        if (!player.hasPermission(Perms.CAPTURE) && !player.hasPermission(template.getCapturePermission())) {
            Lang.CAPTURE_ERROR_PERMISSION.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_NAME, localized));
            return false;
        }

        if (!this.isReadyToCapture(entity)) {
            Lang.CAPTURE_ERROR_NOT_READY.message().send(player, replacer -> replacer.replace(Placeholders.GENERIC_NAME, localized));
            return false;
        }

        if (this.startCapture(player, entity, template)) {
            if (CaptureConfig.CAPTURE_CONSUME_ITEM.get()) {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
            return true;
        }

        return false;
    }

    public boolean startCapture(@NotNull Player player, @NotNull LivingEntity entity, @NotNull Template petConfig) {
        if (this.isCapturing(player)) return false;

        Tier tier = this.getTierByWeight();
        if (tier == null) return false;

        PetCaptureStartEvent event = new PetCaptureStartEvent(player, entity, petConfig, tier);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        CaptureTask process = new CaptureTask(plugin, player, entity, petConfig, tier);
        process.start();
        this.captureMap.put(player.getUniqueId(), process);
        return true;
    }

    public void stopCapture(@NotNull UUID playerId) {
        CaptureTask process = this.getCaptureProcess(playerId);
        if (process == null) return;

        process.stop();
        this.captureMap.remove(playerId);
    }

    @Nullable
    public Tier getTierByWeight() {
        Map<Tier, Double> map = new HashMap<>();
        this.plugin.getPetManager().getTiers().forEach(tier -> {
            if (tier.isCapturable()) {
                map.put(tier, tier.getWeight());
            }
        });
        return map.isEmpty() ? null : Rnd.getByWeight(map);
    }

    @NotNull
    public ItemStack createCaptureItem() {
        ItemStack itemStack = CaptureConfig.CAPTURE_ITEM.get().getItemStack();
        PDCUtil.set(itemStack, Keys.captureItem, true);
        return itemStack;
    }

    public boolean isCaptureItem(@NotNull ItemStack itemStack) {
        return PDCUtil.getBoolean(itemStack, Keys.captureItem).orElse(false);
    }

    public boolean isReadyToCapture(@NotNull LivingEntity entity) {
        double maxPercent = CaptureConfig.CAPTURE_HEALTH_PERCENT.get();
        if (maxPercent <= 0D || maxPercent >= 100D) return true;

        double maxHealth = EntityUtil.getAttribute(entity, Attribute.MAX_HEALTH);
        double currentHealth = entity.getHealth();
        double healthPercent = currentHealth / maxHealth * 100D;

        return healthPercent <= maxPercent;
    }

    public boolean canBeCaptured(@NotNull LivingEntity entity) {
        if (this.plugin.getPetManager().isPetEntity(entity)) return false;
        if (CaptureManager.isEscaped(entity)) return false;
        if (entity instanceof Player) return false;
        if (entity instanceof Tameable tameable && tameable.isTamed()) return false;
        if (Plugins.isLoaded(HookId.MYTHIC_MOBS) && MythicMobsHook.isMythicMob(entity)) return false;

        return this.plugin.getPetManager().getTemplate(entity) != null;
    }

    public static void setEscapedFromCatch(@NotNull LivingEntity entity, boolean value) {
        PDCUtil.set(entity, Keys.captureEscaped, value);
        PDCUtil.remove(entity, Keys.captureProgress);
    }

    public static boolean isEscaped(@NotNull LivingEntity entity) {
        return PDCUtil.getBoolean(entity, Keys.captureEscaped).orElse(false);
    }

    public static void saveCaptureProgress(@NotNull LivingEntity entity, int value) {
        PDCUtil.set(entity, Keys.captureProgress, value);
    }

    public static int getSavedCaptureProgress(@NotNull LivingEntity entity) {
        return PDCUtil.getInt(entity, Keys.captureProgress).orElse(0);
    }
}
