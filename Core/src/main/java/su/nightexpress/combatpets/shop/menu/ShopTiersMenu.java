package su.nightexpress.combatpets.shop.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class ShopTiersMenu extends ConfigMenu<PetsPlugin> implements AutoFilled<Tier> {

    private static final String FILE_NAME = "shop_pet_tiers.yml";

    private final ShopManager shopManager;

    private String       tierName;
    private List<String> tierLore;
    private int[]        tierSlots;

    public ShopTiersMenu(@NotNull PetsPlugin plugin, @NotNull ShopManager shopManager) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.shopManager = shopManager;

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Tier> autoFill) {
        autoFill.setSlots(this.tierSlots);
        autoFill.setItems(this.plugin.getPetManager().getTiers().stream()
            .filter(this.shopManager::isEggBuyable)
            .sorted(Comparator.comparingDouble(Tier::getWeight).reversed())
            .toList()
        );
        autoFill.setItemCreator(tier -> {
            ItemStack item = tier.getIcon();
            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.tierName)
                .setLore(this.tierLore)
                .replace(tier.getPlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(tier -> (viewer1, event) -> {
            this.runNextTick(() -> this.shopManager.openEggsMenu(viewer.getPlayer(), tier));
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Select a tier..."), MenuSize.CHEST_36);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack backItem = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(backItem, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_EXIT.getName());
        });
        list.add(new MenuItem(backItem).setSlots(31).setPriority(10).setHandler(ItemHandler.forClose(this)));

        ItemStack prevPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_PREVIOUS_PAGE.getName());
        });
        list.add(new MenuItem(prevPage).setSlots(27).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_NEXT_PAGE.getName());
        });
        list.add(new MenuItem(nextPage).setSlots(35).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.tierName = ConfigValue.create("Tiers.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(TIER_NAME))
        ).read(cfg);

        this.tierLore = ConfigValue.create("Tiers.Lore", Lists.newList(
            LIGHT_GRAY.enclose("Click to purchase " + LIGHT_YELLOW.enclose(TIER_NAME) + " pet eggs.")
        )).read(cfg);

        this.tierSlots = ConfigValue.create("Tiers.Slots", new int[]{10,12,14,16}).read(cfg);
    }
}
