package su.nightexpress.combatpets.pet.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.util.PetUtils;
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
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class TiersMenu extends ConfigMenu<PetsPlugin> implements AutoFilled<Tier> {

    private static final String FILE_NAME = "pet_tier_collection.yml";

    private String       tierName;
    private List<String> tierLore;
    private int[]        tierSlots;

    public TiersMenu(@NotNull PetsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));

        this.load();

        this.getItems().forEach(PetUtils::applyMenuPlaceholders);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        PetUtils.applyMenuPlaceholders(viewer, options);
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Tier> autoFill) {
        autoFill.setSlots(this.tierSlots);
        autoFill.setItems(this.plugin.getPetManager().getTiers().stream().sorted(Comparator.comparingDouble(Tier::getWeight).reversed()).toList());
        autoFill.setItemCreator(tier -> {
            PetUser user = plugin.getUserManager().getOrFetch(viewer.getPlayer());

            ItemStack item = tier.getIcon();
            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.tierName)
                .setLore(this.tierLore)
                .replace(tier.getPlaceholders())
                .replace(GENERIC_AMOUNT, NumberUtil.format(user.getPets(tier).size()))
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(tier -> (viewer1, event) -> {
            this.runNextTick(() -> this.plugin.getPetManager().openPetsCollection(viewer1.getPlayer(), tier));
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Pet Collection (Tiers)"), MenuSize.CHEST_36);
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
            LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + LIGHT_GRAY.enclose("pets.")
        )).read(cfg);

        this.tierSlots = ConfigValue.create("Tiers.Slots", new int[]{10, 12, 14, 16}).read(cfg);
    }
}
