package su.nightexpress.combatpets;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.capture.CaptureManager;
import su.nightexpress.combatpets.data.UserManager;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.level.LevelingManager;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.combatpets.wardrobe.WardrobeManager;

public class PetAPI {

    public static PetsPlugin plugin;

    static void setup(@NotNull PetsPlugin plugin) {
        PetAPI.plugin = plugin;
    }

    static void shutdown() {
        plugin = null;
    }

    @NotNull
    public static UserManager getUserManager() {
        return plugin.getUserManager();
    }

    @NotNull
    public static PetManager getPetManager() {
        return plugin.getPetManager();
    }

    @Nullable
    public static CaptureManager getCaptureManager() {
        return plugin.getCaptureManager();
    }

    @Nullable
    public static LevelingManager getLevelingManager() {
        return plugin.getLevelingManager();
    }

    @Nullable
    public static WardrobeManager getWardrobeManager() {
        return plugin.getWardrobeManager();
    }

    @Nullable
    public static ShopManager getShopManager() {
        return plugin.getShopManager();
    }

    @NotNull
    public static PetUser getPlayerData(@NotNull Player player) {
        return plugin.getUserManager().getOrFetch(player);
    }
}
