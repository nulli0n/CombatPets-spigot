package su.nightexpress.combatpets.shop.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.combatpets.shop.data.EggPrice;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.ItemOptions;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class EggConfirmMenu extends ConfigMenu<PetsPlugin> implements Linked<EggConfirmMenu.BuyInfo> {

    public static final String FILE_NAME = "shop_buy_confirm.yml";

    private final ViewLink<BuyInfo> link;

    private final ShopManager shopManager;
    private final ItemHandler acceptHandler;
    private final ItemHandler declineHandler;

    private String       iconName;
    private List<String> iconLore;
    private int          iconSlot;

    public record BuyInfo(@NotNull Template template, @NotNull Tier tier) {}

    public EggConfirmMenu(@NotNull PetsPlugin plugin, @NotNull ShopManager shopManager) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.shopManager = shopManager;
        this.link = new ViewLink<>();

        this.addHandler(this.acceptHandler = new ItemHandler("accept", (viewer, event) -> {
            Player player = viewer.getPlayer();

            BuyInfo buyInfo = this.getLink(player);
            if (buyInfo == null) return;

            Template template = buyInfo.template;
            Tier tier = buyInfo.tier;

            EggPrice price = this.shopManager.getEggPrice(tier, template);
            if (price == null) return;

            Currency currency = price.getCurrency();
            double cost = price.getPrice();

            double balance = currency.getBalance(player);
            if (balance < cost) {
                Lang.SHOP_PURCHASE_ERROR_NOT_ENOUGH_FUNDS.message().send(player, replacer -> replacer
                    .replace(GENERIC_PRICE, currency.format(cost))
                    .replace(template.replacePlaceholders())
                    .replace(tier.replacePlaceholders())
                );
            }
            else {
                currency.take(player, cost);
                Players.addItem(player, template.createEgg(tier));

                Lang.SHOP_PURCHASE_SUCCESS.message().send(player, replacer -> replacer
                    .replace(GENERIC_PRICE, currency.format(cost))
                    .replace(template.replacePlaceholders())
                    .replace(tier.replacePlaceholders())
                );
            }

            this.runNextTick(player::closeInventory);
        }));

        this.addHandler(this.declineHandler = new ItemHandler("cancel", (viewer, event) -> {
            Player player = viewer.getPlayer();
            BuyInfo buyInfo = this.getLink(player);

            this.runNextTick(() -> this.shopManager.openEggsMenu(player, buyInfo.tier));
        }));

        this.load();

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
            BuyInfo buyInfo = this.getLink(viewer);
            EggPrice price = this.shopManager.getEggPrice(buyInfo.tier, buyInfo.template);

            ItemReplacer.create(itemStack).readMeta()
                .replace(buyInfo.template.getPlaceholders())
                .replace(buyInfo.tier.getPlaceholders())
                .replace(GENERIC_PRICE, price == null ? String.valueOf(0) : price.getCurrency().format(price.getPrice()))
                .writeMeta();
        }));
    }

    @NotNull
    @Override
    public ViewLink<BuyInfo> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Player player = viewer.getPlayer();
        BuyInfo buyInfo = this.getLink(player);
        Template template = buyInfo.template;
        Tier tier = buyInfo.tier;

        ItemStack item = PetUtils.getRawEggItem(template);
        ItemReplacer.create(item).hideFlags().trimmed()
            .setDisplayName(this.iconName)
            .setLore(this.iconLore)
            .replace(template.getPlaceholders())
            .replace(tier.getPlaceholders())
            .writeMeta();

        MenuItem menuItem = new MenuItem(item);
        menuItem.setOptions(ItemOptions.personalWeak(player));
        menuItem.setSlots(this.iconSlot);
        menuItem.setPriority(Integer.MAX_VALUE);
        this.addItem(menuItem);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Are you sure?"), MenuSize.CHEST_9);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack acceptItem = ItemUtil.getSkinHead(SKIN_CHECK_MARK);
        ItemUtil.editMeta(acceptItem, meta -> {
            meta.setDisplayName(LIGHT_GREEN.enclose(BOLD.enclose("Yes")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Yes, I'm sure!"),
                "",
                LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("[▶]") + " Click to " + LIGHT_GREEN.enclose("purchase") + " for " + LIGHT_GREEN.enclose(GENERIC_PRICE) + ".")
            ));
        });
        list.add(new MenuItem(acceptItem).setPriority(10).setSlots(8).setHandler(this.acceptHandler));

        ItemStack denyItem = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(denyItem, meta -> {
            meta.setDisplayName(LIGHT_RED.enclose(BOLD.enclose("No")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("No, I changed my mind.")
            ));
        });
        list.add(new MenuItem(denyItem).setPriority(10).setSlots(0).setHandler(this.declineHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.iconName = ConfigValue.create("PetIcon.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose("Purchase: ")) + WHITE.enclose(TEMPLATE_DEFAULT_NAME) + " " + LIGHT_GRAY.enclose("(" + TIER_NAME + ")")
        ).read(cfg);

        this.iconLore = ConfigValue.create("PetIcon.Lore", Lists.newList(

        )).read(cfg);

        this.iconSlot = ConfigValue.create("PetIcon.Slot", 4).read(cfg);
    }
}
