package su.nightexpress.combatpets.pet.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
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
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class CollectionMenu extends ConfigMenu<PetsPlugin> implements AutoFilled<PetData>, Linked<Tier> {

    private static final String FILE_NAME = "pet_collection.yml";
    private static final String STATUS = "%status%";

    private final ViewLink<Tier> link;
    private final ItemHandler    returnHandler;

    private String       petName;
    private List<String> petLore;
    private int[]        petSlots;

    private List<String> petStatusDeadAuto;
    private List<String> petStatusDeadManual;
    private List<String> petStatusActive;
    private List<String> petStatusInactive;

    public CollectionMenu(@NotNull PetsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> plugin.getPetManager().openTierCollection(viewer.getPlayer()));
        }));

        this.load();

        this.getItems().forEach(PetUtils::applyMenuPlaceholders);
    }

    @NotNull
    @Override
    public ViewLink<Tier> getLink() {
        return link;
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
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<PetData> autoFill) {
        Player player = viewer.getPlayer();
        Tier tier = this.getLink(player);
        if (tier == null) return;

        autoFill.setSlots(this.petSlots);
        autoFill.setItems(this.plugin.getUserManager().getOrFetch(player).getPets(tier).stream()
            .sorted(Comparator.comparing(data -> data.getTemplate().getId())).toList());
        autoFill.setItemCreator(petData -> {
            ActivePet petHolder = PetEntityBridge.getByPlayer(player);

            List<String> status = new ArrayList<>();

            if (petData.isDead()) {
                if (petData.isAutoRevivable()) {
                    status.addAll(this.petStatusDeadAuto);
                }
                else {
                    status.addAll(this.petStatusDeadManual);
                }
                status.replaceAll(str -> str
                    .replace(GENERIC_TIME, TimeUtil.formatDuration(petData.getReviveDate()))
                    .replace(GENERIC_COST, NumberUtil.format(petData.getTier().getReviveCost()))
                );
            }
            else {
                if (petHolder == null || petHolder.getTemplate() != petData.getTemplate() || petHolder.getTier() != petData.getTier()) {
                    status.addAll(this.petStatusInactive);
                }
                else {
                    status.addAll(this.petStatusActive);
                }
            }

            ItemStack item = ItemUtil.getSkinHead(petData.getTemplate().getEggTexture());
            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.petName)
                .setLore(this.petLore)
                .replace(STATUS, status)
                .replace(petData.getPlaceholders())
                .replace(petData.getTier().getPlaceholders())
                .replace(petData.getTemplate().getPlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(petData -> (viewer1, event) -> {
            ActivePet holder = PetEntityBridge.getByPlayer(player);

            if (event.isLeftClick()) {
                if (petData.isDead()) {
                    this.runNextTick(() -> plugin.getPetManager().openReviveMenu(player, petData));
                    return;
                }

                if (holder != null) {
                    this.plugin.getPetManager().despawnPet(player);
                    if (holder.getTemplate() == petData.getTemplate()) {
                        this.runNextTick(() -> this.flush(viewer));
                        return;
                    }
                }
                this.plugin.getPetManager().spawnPet(player, petData);
                this.runNextTick(player::closeInventory);
                return;
            }

            if (event.getClick() == ClickType.DROP && Config.PET_RELEASE_ALLOWED.get()) {
                if (holder != null) {
                    if (holder.getTemplate() == petData.getTemplate() && holder.getTier() == petData.getTier()) {
                        //plugin.getMessage(Lang.PET_RELEASE_ERROR_ACTIVE).send(player);
                        return;
                    }
                }
                this.runNextTick(() -> plugin.getPetManager().openReleaseMenu(player, petData));
            }
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Pet Collection"), MenuSize.CHEST_54);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(filler).setSlots(0,1,2,3,4,5,6,7,8,9,18,27,36,45,17,26,35,44,46,47,48,49,50,51,52,53));

        ItemStack backItem = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(backItem, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_BACK.getName());
        });
        list.add(new MenuItem(backItem).setSlots(49).setPriority(10).setHandler(this.returnHandler));

        ItemStack prevPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(prevPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_PREVIOUS_PAGE.getName());
        });
        list.add(new MenuItem(prevPage).setSlots(45).setPriority(10).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(CoreLang.MENU_ICON_NEXT_PAGE.getName());
        });
        list.add(new MenuItem(nextPage).setSlots(53).setPriority(10).setHandler(ItemHandler.forNextPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.petName = ConfigValue.create("Pet.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(PET_NAME))
        ).read(cfg);

        this.petLore = ConfigValue.create("Pet.Lore", Lists.newList(
            "",
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Level: ") + PET_LEVEL),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP: ") + PET_XP + LIGHT_GRAY.enclose("/") + PET_REQUIRED_XP),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Saturation: ") + PET_SATURATION + LIGHT_GRAY.enclose("/") + PET_MAX_SATURATION),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Food: ") + PET_FOOD),
            "",
            LIGHT_YELLOW.enclose(BOLD.enclose("ATTRIBUTES")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Damage: ") + PET_ATTRIBUTE.apply(AttributeRegistry.ATTACK_DAMAGE)),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Attack Speed: ") + PET_ATTRIBUTE.apply(AttributeRegistry.ATTACK_SPEED) + LIGHT_GRAY.enclose("/ sec.")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Health: ") + PET_ATTRIBUTE.apply(AttributeRegistry.MAX_HEALTH)),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Regen: ") + PET_ATTRIBUTE.apply(AttributeRegistry.HEALTH_REGENEATION_FORCE) + LIGHT_GRAY.enclose(" x ") + PET_ATTRIBUTE.apply(AttributeRegistry.HEALTH_REGENEATION_SPEED) + LIGHT_GRAY.enclose(" / sec.")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Defense: ") + PET_ATTRIBUTE.apply(AttributeRegistry.ARMOR)),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Speed: W: ") + PET_ATTRIBUTE.apply(AttributeRegistry.MOVEMENT_SPEED) + LIGHT_GRAY.enclose(" / F: ") + PET_ATTRIBUTE.apply(AttributeRegistry.FLYING_SPEED)),
//                "",
//                LIGHT_YELLOW.enclose(BOLD.enclose("ASPECTS")),
//                LIGHT_YELLOW.enclose("▪ #ddeceeStrength: %pet_aspect_strength%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeVitality: %pet_aspect_vitality%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeDefense: %pet_aspect_defense%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeDexterity: %pet_aspect_dexterity%")
                "",
                STATUS
        )).read(cfg);

        this.petStatusDeadAuto = ConfigValue.create("Pet.Status.Dead_Auto", Lists.newList(
            LIGHT_GRAY.enclose("Status: " + LIGHT_RED.enclose(BOLD.enclose("Dead"))),
            LIGHT_GRAY.enclose("Ressurection in: " + LIGHT_RED.enclose(GENERIC_TIME)),
            "",
            LIGHT_RED.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to revive it for " + LIGHT_RED.enclose("$" + GENERIC_COST) + ".")
        )).read(cfg);

        this.petStatusDeadManual = ConfigValue.create("Pet.Status.Dead_Manual", Lists.newList(
            LIGHT_GRAY.enclose("Status: " + LIGHT_RED.enclose(BOLD.enclose("Dead"))),
            "",
            LIGHT_RED.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to revive it for " + LIGHT_RED.enclose("$" + GENERIC_COST) + ".")
        )).read(cfg);

        this.petStatusActive = ConfigValue.create("Pet.Status.Active", Lists.newList(
            LIGHT_GRAY.enclose("Status: " + LIGHT_GREEN.enclose(BOLD.enclose("Summoned"))),
            "",
            LIGHT_GREEN.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_GREEN.enclose("despawn") + ".")
        )).read(cfg);

        this.petStatusInactive = ConfigValue.create("Pet.Status.Inactive", Lists.newList(
            LIGHT_GRAY.enclose("Status: " + LIGHT_YELLOW.enclose(BOLD.enclose("Idle"))),
            "",
            LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Left-Click to " + LIGHT_YELLOW.enclose("summon") + "."),
            LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("[Q/Drop] key to " + LIGHT_YELLOW.enclose("release") + ".")
        )).read(cfg);

        this.petSlots = ConfigValue.create("Pets.Slots",
            new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43}
        ).read(cfg);
    }
}
