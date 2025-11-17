package su.nightexpress.combatpets.wardrobe;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.wardrobe.config.WardrobeConfig;
import su.nightexpress.combatpets.wardrobe.handler.*;
import su.nightexpress.combatpets.wardrobe.listener.WardrobeListener;
import su.nightexpress.combatpets.wardrobe.util.EntityVariant;
import su.nightexpress.combatpets.wardrobe.util.VariantHandler;
import su.nightexpress.combatpets.wardrobe.util.VariantRegistry;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.StringUtil;

public class WardrobeManager extends AbstractManager<PetsPlugin> {

    public static final String FILE_NAME = "accessories.yml";

    public WardrobeManager(@NotNull PetsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        FileConfig config = this.getConfig();

        this.loadConfig(config);
        this.loadVariants(config);

        config.saveChanges();

        this.addListener(new WardrobeListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        VariantRegistry.clear();
    }

    @NotNull
    public FileConfig getConfig() {
        return FileConfig.loadOrExtract(this.plugin, FILE_NAME);
    }

    private void loadConfig(@NotNull FileConfig config) {
        config.initializeOptions(WardrobeConfig.class);
    }

    private void loadVariants(@NotNull FileConfig config) {
        this.register("age", new AgeVariantHandler(), config);
        this.register("size", new SizeVariantHandler(), config);
        this.register("creeper_state", new CreeperPowerVariantHandler(), config);
        this.register("fox_type", new FoxTypeVariantHandler(), config);
        this.register("llama_color", new LlamaColorVariantHandler(), config);
        this.register("sheep_color", new SheepColorVariantHandler(), config);
        this.register("horse_style", new HorseStyleVariantHandler(), config);
        this.register("horse_color", new HorseColorVariantHandler(), config);
        this.register("shear_style", new SheepShearVariantHandler(), config);
        this.register("rabbit_type", new RabbitVariantHandler(), config);
        // TODO More varaints
        // Cat type, Mooshroom type, Villager profession (+ zombie)
    }

    public <T> void register(@NotNull String name, @NotNull VariantHandler<T> handler, @NotNull FileConfig config) {
        EntityVariant<T> variant = new EntityVariant<>(name, handler);

        String path = "Variants." + variant.getName();

        boolean enabled = ConfigValue.create(path + ".Enabled", true).read(config);
        if (!enabled) return;

        String displayName = ConfigValue.create(path + ".DisplayName", StringUtil.capitalizeUnderscored(variant.getName())).read(config);
        variant.setDisplayName(displayName);

        ItemStack icon = ConfigValue.create(path + ".Icon", new ItemStack(Material.MAP)).read(config);
        variant.setIcon(icon);

        VariantRegistry.register(variant);
        this.plugin.info("Registered '" + variant.getName() + "' pet accessories.");
    }

    public boolean applyAccessory(@NotNull Player player, @NotNull LivingEntity entity, @NotNull ItemStack item, @NotNull EntityVariant<?> variant, @NotNull String value) {
        ActivePet petHolder = this.plugin.getPetManager().getPetByMob(entity);
        if (petHolder == null) return false;

        if (!petHolder.isOwner(player)) {
            Lang.ACCESSORY_APPLY_ERROR_NOT_YOURS.message().send(player);
            return false;
        }

        if (variant.getHandler().alreadyHas(entity, value)) {
            Lang.ACCESSORY_APPLY_ERROR_ALREADY_HAS.message().send(player);
            return false;
        }

        if (!variant.getHandler().apply(entity, value)) {
            Lang.ACCESSORY_APPLY_ERROR_WRONG_TYPE.message().send(player);
            return false;
        }

        item.setAmount(item.getAmount() - 1);

        WardrobeConfig.WARDROBE_ACCESSORY_APPLY_EFFECT.get().play(entity.getEyeLocation(), 0.35, 0.05, 40);
        WardrobeConfig.WARDROBE_ACCESSORY_APPLY_SOUND.get().play(entity.getEyeLocation());

        return true;
    }

    // Used to store entity accessories in pet egg obtained from capturing.
    public void storeAccessoryData(@NotNull LivingEntity entity, @NotNull ItemStack itemStack) {
        ItemUtil.editMeta(itemStack, meta -> {
            PetWardrobe.of(entity).getAccessories().forEach((key, data) -> {

                EntityVariant<?> variant = VariantRegistry.getVariant(key);
                if (variant == null) return;

                PDCUtil.set(meta, variant.getKey(), data);
            });
        });
    }

    // Used to restore entity accessories to PetData when player claims pet egg.
    @NotNull
    public PetWardrobe readAccessoryData(@NotNull ItemStack itemStack) {
        PetWardrobe wardrobe = new PetWardrobe();

        VariantRegistry.getVariants().forEach(variant -> {
            String data = PDCUtil.getString(itemStack, variant.getKey()).orElse(null);
            if (data == null) return;

            wardrobe.addAccessory(variant.getName(), data);
        });

        return wardrobe;
    }

    @NotNull
    public ItemStack getItem(@NotNull EntityVariant<?> variant, @NotNull String value) {
        ItemStack item = new ItemStack(WardrobeConfig.WARDROBE_ACCESSORY_ITEM.get());
        ItemReplacer.replace(item, str -> str
            .replace(Placeholders.GENERIC_TYPE, variant.getDisplayName())
            .replace(Placeholders.GENERIC_NAME, variant.getHandler().getLocalized(value)));

        PDCUtil.set(item, Keys.accessoryType, variant.getName());
        PDCUtil.set(item, Keys.accessoryValue, value.toLowerCase());

        return item;
    }

    @Nullable
    public EntityVariant<?> getType(@NotNull ItemStack item) {
        String type = PDCUtil.getString(item, Keys.accessoryType).orElse(null);
        return type == null ? null : VariantRegistry.getVariant(type);
    }

    @Nullable
    public String getValue(@NotNull ItemStack item) {
        return PDCUtil.getString(item, Keys.accessoryValue).orElse(null);
    }
}
