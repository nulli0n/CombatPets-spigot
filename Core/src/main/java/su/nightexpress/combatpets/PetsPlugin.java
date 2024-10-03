package su.nightexpress.combatpets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.capture.CaptureManager;
import su.nightexpress.combatpets.command.impl.BaseCommands;
import su.nightexpress.combatpets.command.impl.AspectPointsCommands;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.currency.CurrencyManager;
import su.nightexpress.combatpets.item.ItemManager;
import su.nightexpress.combatpets.level.LevelingManager;
import su.nightexpress.combatpets.wardrobe.WardrobeManager;
import su.nightexpress.combatpets.data.DataHandler;
import su.nightexpress.combatpets.data.UserManager;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.hook.impl.PlaceholderHook;
import su.nightexpress.combatpets.nms.PetNMS;
import su.nightexpress.combatpets.nms.mc_1_21.MC_1_21;
import su.nightexpress.combatpets.nms.v1_19_R1.V1_19_R1;
import su.nightexpress.combatpets.nms.v1_20.V1_20;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;

public class PetsPlugin extends NightDataPlugin<PetUser> implements ImprovedCommands {

    private DataHandler dataHandler;
    private UserManager userManager;

    private CurrencyManager currencyManager;
    private ItemManager itemManager;
    private PetManager      petManager;
    private LevelingManager levelingManager;
    private CaptureManager  captureManager;
    private WardrobeManager wardrobeManager;
    private ShopManager     shopManager;

    private PetNMS petNMS;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Pets", new String[]{"pets", "pet", "compatpets"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    public void enable() {
        PetAPI.setup(this);

        if (!this.setupNMS()) {
            this.error("Unsupported server version!");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        Keys.load(this);

        this.loadCommands();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();

        this.itemManager = new ItemManager(this);
        this.itemManager.setup();

        this.petManager = new PetManager(this);
        this.petManager.setup();

        if (Config.isLevelingEnabled()) {
            this.levelingManager = new LevelingManager(this);
            this.levelingManager.setup();
        }

        if (Config.isCapturingEnabled()) {
            this.captureManager = new CaptureManager(this);
            this.captureManager.setup();
        }

        if (Config.isWardrobeEnabled()) {
            this.wardrobeManager = new WardrobeManager(this);
            this.wardrobeManager.setup();
        }

        if (Config.isShopEnabled()) {
            this.loadShop();
        }

        this.loadHooks();
    }

    private boolean loadShop() {
        if (!this.currencyManager.hasCurrency()) {
            this.warn("No currencies are available. Shop will be disabled.");
            this.warn(Placeholders.WIKI_CURRENCY_URL);
            return false;
        }

        this.shopManager = new ShopManager(this);
        this.shopManager.setup();
        return true;
    }

    private void loadHooks() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }

        if (this.levelingManager != null) this.levelingManager.shutdown();
        if (this.shopManager != null) this.shopManager.shutdown();
        if (this.captureManager != null) this.captureManager.shutdown();
        if (this.petManager != null) this.petManager.shutdown();
        if (this.wardrobeManager != null) this.wardrobeManager.shutdown();
        if (this.currencyManager != null) this.currencyManager.shutdown();
        if (this.itemManager != null) this.itemManager.shutdown();

        PetAPI.shutdown();
    }

    private void loadCommands() {
        BaseCommands.load(this);
        AspectPointsCommands.load(this);
    }

    private boolean setupNMS() {
        switch (Version.getCurrent()) {
            case V1_19_R3 -> this.petNMS = new V1_19_R1();
            case V1_20_R3 -> this.petNMS = new V1_20();
            case MC_1_21 -> this.petNMS = new MC_1_21();
        }
        return this.petNMS != null;
    }

    @Override
    @NotNull
    public DataHandler getData() {
        return this.dataHandler;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public PetNMS getPetNMS() {
        return this.petNMS;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    @NotNull
    public ItemManager getItemManager() {
        return itemManager;
    }

    @NotNull
    public PetManager getPetManager() {
        return this.petManager;
    }

    @Nullable
    public LevelingManager getLevelingManager() {
        return levelingManager;
    }

    @Nullable
    public CaptureManager getCaptureManager() {
        return captureManager;
    }

    @Nullable
    public WardrobeManager getWardrobeManager() {
        return this.wardrobeManager;
    }

    @Nullable
    public ShopManager getShopManager() {
        return shopManager;
    }
}
