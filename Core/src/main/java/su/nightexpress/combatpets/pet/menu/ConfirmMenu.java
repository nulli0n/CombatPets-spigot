package su.nightexpress.combatpets.pet.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.ItemOptions;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;

import java.util.List;

public abstract class ConfirmMenu extends ConfigMenu<PetsPlugin> implements Linked<PetData> {

    private final ViewLink<PetData> link;

    protected final ItemHandler acceptHandler;
    protected final ItemHandler declineHandler;

    protected String       iconName;
    protected List<String> iconLore;
    protected int          iconSlot;

    public ConfirmMenu(@NotNull PetsPlugin plugin, @NotNull FileConfig config) {
        super(plugin, config);
        this.link = new ViewLink<>();

        this.addHandler(this.acceptHandler = new ItemHandler("confirmation_accept", (viewer, event) -> {
            this.handle(viewer, true);
        }));

        this.addHandler(this.declineHandler = new ItemHandler("confirmation_decline", (viewer, event) -> {
            this.handle(viewer, false);
        }));

        this.load();

        this.getItems().forEach(PetUtils::applyMenuPlaceholders);
    }

    @NotNull
    @Override
    public ViewLink<PetData> getLink() {
        return link;
    }

    private void handle(@NotNull MenuViewer viewer, boolean result) {
        Player player = viewer.getPlayer();
        PetData petData = this.getLink(viewer);
        if (petData == null) {
            player.closeInventory();
            this.runNextTick(player::closeInventory);
            return;
        }

        if (result) {
            this.onConfirm(viewer, petData);
        }
        else this.onDecline(viewer, petData);
    }

    protected abstract void onConfirm(@NotNull MenuViewer viewer, @NotNull PetData petData);

    protected abstract void onDecline(@NotNull MenuViewer viewer, @NotNull PetData petData);

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        PetUtils.applyMenuPlaceholders(viewer, options);

        PetData petData = this.getLink(viewer);
        if (petData == null) return;

        ItemStack item = PetUtils.getRawEggItem(petData.getTemplate());
        ItemReplacer.create(item).hideFlags().trimmed()
            .setDisplayName(this.iconName)
            .setLore(this.iconLore)
            .replace(petData.getPlaceholders())
            .replace(petData.getTier().getPlaceholders())
            .writeMeta();

        Player player = viewer.getPlayer();
        MenuItem menuItem = new MenuItem(item);
        menuItem.setOptions(ItemOptions.personalWeak(player));
        menuItem.setSlots(this.iconSlot);
        menuItem.setPriority(Integer.MAX_VALUE);
        this.addItem(menuItem);
    }
}
