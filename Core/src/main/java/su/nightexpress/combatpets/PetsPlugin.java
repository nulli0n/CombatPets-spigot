package su.nightexpress.combatpets;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.capture.CaptureManager;
import su.nightexpress.combatpets.command.impl.AspectPointsCommands;
import su.nightexpress.combatpets.command.impl.BaseCommands;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.DataHandler;
import su.nightexpress.combatpets.data.UserManager;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.combatpets.hook.impl.PlaceholderHook;
import su.nightexpress.combatpets.item.ItemManager;
import su.nightexpress.combatpets.level.LevelingManager;
import su.nightexpress.combatpets.nms.PetNMS;
import su.nightexpress.combatpets.nms.mc_1_21_3.MC_1_21_4;
import su.nightexpress.combatpets.nms.mc_1_21_5.MC_1_21_5;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.combatpets.wardrobe.WardrobeManager;
import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;

public class PetsPlugin extends NightDataPlugin<PetUser> implements ImprovedCommands {

    private DataHandler dataHandler;
    private UserManager userManager;

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
        return PluginDetails.create("Pets", new String[]{"pets", "pet", "combatpets"})
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
        if (!PetUtils.hasEconomyBridge()) {
            this.error("You must install " + HookId.ECONOMY_BRIDGE + " to use shop features!");
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
        if (this.itemManager != null) this.itemManager.shutdown();

        PetAPI.shutdown();
    }

    private void loadCommands() {
        BaseCommands.load(this);
        AspectPointsCommands.load(this);
    }

    private boolean setupNMS() {
        switch (Version.getCurrent()) {
            case MC_1_21_4 -> this.petNMS = new MC_1_21_4();
            case MC_1_21_5 -> this.petNMS = new MC_1_21_5();
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
    public ItemManager getItemManager() {
        return itemManager;
    }

    @NotNull
    public PetManager getPetManager() {
        return this.petManager;
    }

    public LevelingManager getLevelingManager() {
        return this.levelingManager;
    }

    public CaptureManager getCaptureManager() {
        return this.captureManager;
    }

    public WardrobeManager getWardrobeManager() {
        return this.wardrobeManager;
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }
}
