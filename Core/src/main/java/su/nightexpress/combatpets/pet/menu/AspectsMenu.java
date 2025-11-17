package su.nightexpress.combatpets.pet.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.pet.AttributeRegistry;
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
import su.nightexpress.nightcore.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class AspectsMenu extends ConfigMenu<PetsPlugin> implements AutoFilled<Aspect> {

    public static final String FILE_NAME = "pet_aspects.yml";

    private static final String ATTRIBUTES = "%attributes%";
    private static final String ACTION = "%action%";

    private final ItemHandler returnHandler;
    private final ItemHandler reallocateHandler;

    private String       aspectName;
    private List<String> aspectLore;
    private int[]        aspectSlots;

    private List<String> aspectAttributes;
    private List<String> aspectActionUpgrade;
    private List<String> aspectActionNoPoints;
    private List<String> aspectActionMaxLevel;

    public AspectsMenu(@NotNull PetsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getPetManager().openPetMenu(viewer.getPlayer()));
        }));

        this.addHandler(this.reallocateHandler = new ItemHandler("reallocate_points", (viewer, event) -> {
            Player player = viewer.getPlayer();
            ActivePet petHolder = PetEntityBridge.getByPlayer(player);
            if (petHolder == null) return;

            petHolder.reallocateAspects();
            petHolder.update();

            this.runNextTick(() -> this.flush(viewer));
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            PetUtils.applyMenuPlaceholders(menuItem);

            if (menuItem.getHandler() == this.reallocateHandler) {
                menuItem.getOptions().setVisibilityPolicy(viewer -> Config.PET_REALLOCATE_ASPECTS.get());
            }
        });
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
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Aspect> autoFill) {
        Player player = viewer.getPlayer();
        ActivePet petHolder = PetEntityBridge.getByPlayer(player);
        if (petHolder == null) return;

        autoFill.setSlots(this.aspectSlots);
        autoFill.setItems(this.plugin.getPetManager().getAspects().stream().sorted(Comparator.comparing(Aspect::getName)).toList());
        autoFill.setItemCreator(aspect -> {
            ItemStack item = new ItemStack(aspect.getIcon());

            List<String> action = new ArrayList<>();
            List<String> attributes = new ArrayList<>();

            int value = petHolder.getAspectValue(aspect);
            int maxValue = petHolder.getTier().getAspectMax(aspect);

            if (petHolder.getAspectPoints() <= 0) {
                action.addAll(this.aspectActionNoPoints);
            }
            else {
                if (value < maxValue) {
                    action.addAll(this.aspectActionUpgrade);
                }
                else action.addAll(this.aspectActionMaxLevel);
            }

            for (String line : this.aspectAttributes) {
                if (line.contains(GENERIC_NAME)) {
                    for (String name : aspect.getAttributes()) {
                        Stat attribute = AttributeRegistry.getById(name);
                        if (attribute == null) continue;

                        double perAspect = petHolder.getTemplate().getAttributePerAspect(attribute);
                        if (perAspect == 0D) continue;

                        double total = petHolder.getAttribute(attribute);//petHolder.getAttributeValue(attribute);

                        attributes.add(line
                            .replace(GENERIC_NAME, attribute.getDisplayName())
                            .replace(GENERIC_VALUE, NumberUtil.format(perAspect))
                            .replace(GENERIC_TOTAL, NumberUtil.format(total))
                        );
                    }
                    continue;
                }
                attributes.add(line);
            }

            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.aspectName)
                .setLore(this.aspectLore)
                .replace(ATTRIBUTES, attributes)
                .replace(ACTION, action)
                .replace(GENERIC_VALUE, NumberUtil.format(value))
                .replace(GENERIC_MAX, NumberUtil.format(maxValue))
                .replace(aspect.getPlaceholders())
                .replace(petHolder.getPlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(aspect -> (viewer1, event) -> {
            ActivePet holder = PetEntityBridge.getByPlayer(viewer.getPlayer());
            if (holder == null) return;

            int aspectValue = holder.getAspectValue(aspect);

            int aspectPoints = holder.getAspectPoints();
            if (aspectPoints <= 0 || aspectValue >= holder.getTier().getAspectMax(aspect)) return;

            holder.setAspectValue(aspect, aspectValue + 1);
            holder.setAspectPoints(holder.getAspectPoints() - 1);

            holder.update();
            this.runNextTick(() -> this.flush(viewer));
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Pet Aspects"), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack reallocate = ItemUtil.getSkinHead("5b1ef2a4829a11fd903b5e31088662a8c56e471bb48643c0d9f95006d1820210");
        ItemUtil.editMeta(reallocate, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Reallocate Points")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Resets all aspect values to 0"),
                LIGHT_GRAY.enclose("and returns aspect points."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("reallocate") + ".")
            ));
        });
        list.add(new MenuItem(reallocate).setSlots(4).setPriority(10).setHandler(this.reallocateHandler));


        ItemStack backItem = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(backItem, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_BACK.getName());
        });
        list.add(new MenuItem(backItem).setSlots(40).setPriority(10).setHandler(this.returnHandler));

        ItemStack prevPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_PREVIOUS_PAGE.getName());
        });
        list.add(new MenuItem(prevPage).setSlots(36).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_NEXT_PAGE.getName());
        });
        list.add(new MenuItem(nextPage).setSlots(44).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.aspectName = ConfigValue.create("Aspect.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(ASPECT_NAME))
        ).read(cfg);

        this.aspectLore = ConfigValue.create("Aspect.Lore", Lists.newList(
            DARK_GRAY.enclose("1 Point"),
            "",
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + GENERIC_VALUE + LIGHT_GRAY.enclose("/") + GENERIC_MAX),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Balance: ") + PET_ASPECT_POINTS + " Points"),
            "",
            ATTRIBUTES,
            "",
            ACTION
        )).read(cfg);

        this.aspectAttributes = ConfigValue.create("Aspect.Attributes", Lists.newList(
            LIGHT_YELLOW.enclose(BOLD.enclose("Affected Attributes:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(GENERIC_NAME + ":") + " +" + GENERIC_VALUE + " " + LIGHT_GRAY.enclose("(" + WHITE.enclose(GENERIC_TOTAL) + ")"))
        )).read(cfg);

        this.aspectActionUpgrade = ConfigValue.create("Aspect.Action.Upgrade", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("upgrade") + ".")
        )).read(cfg);

        this.aspectActionNoPoints = ConfigValue.create("Aspect.Action.NoPoints", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("✘") + " You don't have " + LIGHT_RED.enclose("aspect points") + ".")
        )).read(cfg);

        this.aspectActionMaxLevel = ConfigValue.create("Aspect.Action.MaxValue", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " Aspect is at max. value.")
        )).read(cfg);

        this.aspectSlots = ConfigValue.create("Aspect.Slots", new int[]{19, 21, 23, 25}).read(cfg);
    }
}
