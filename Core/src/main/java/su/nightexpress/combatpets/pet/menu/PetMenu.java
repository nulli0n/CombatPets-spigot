package su.nightexpress.combatpets.pet.menu;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class PetMenu extends ConfigMenu<PetsPlugin> {

    public static final String FILE_NAME = "pet_overview.yml";

    private final ItemHandler despawnHandler;
    private final ItemHandler renameHandler;
    private final ItemHandler inventoryHandler;
    private final ItemHandler equipmentHandler;
    private final ItemHandler aspectsHandler;
    private final ItemHandler combatModeHandler;
    private final ItemHandler silentHandler;
    //private final ItemHandler rideHandler;

    public PetMenu(@NotNull PetsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        
        this.addHandler(this.despawnHandler = new ItemHandler("pet_return", (viewer, event) -> {
            plugin.getPetManager().despawnPet(viewer.getPlayer());
            plugin.runTask(task -> viewer.getPlayer().closeInventory());
        }));

        this.addHandler(this.renameHandler = new ItemHandler("pet_rename", (viewer, event) -> {
            Player player = viewer.getPlayer();
            this.runNextTick(player::closeInventory);
            this.plugin.getPetManager().startRename(player);
        }));


        this.addHandler(this.inventoryHandler = new ItemHandler("pet_inventory", (viewer, event) -> {
            ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
            if (petHolder == null) return;

            this.runNextTick(() -> viewer.getPlayer().openInventory(petHolder.getInventory()));
        }));


        this.addHandler(this.equipmentHandler = new ItemHandler("pet_equipment", (viewer, event) -> {
            ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
            if (petHolder == null) return;
            if (!petHolder.getTier().hasEquipment()) return;

            if (event.isLeftClick()) {
                petHolder.setEquipmentUnlocked(!petHolder.isEquipmentUnlocked());
                this.runNextTick(() -> this.flush(viewer));
            }
            else if (event.isRightClick()) {
                LivingEntity entity = petHolder.getEntity();
                EntityEquipment equipment = entity.getEquipment();
                if (equipment == null) return;

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack wear = equipment.getItem(slot);
                    if (wear.getType().isAir()) continue;

                    Players.addItem(viewer.getPlayer(), wear);
                    equipment.setItem(slot, null);
                }

                petHolder.saveData();
                PetUser user = plugin.getUserManager().getOrFetch(viewer.getPlayer());
                this.plugin.getUserManager().save(user);
            }
        }));


        this.addHandler(this.aspectsHandler = new ItemHandler("pet_aspects", (viewer, event) -> {
            this.runNextTick(() -> plugin.getPetManager().openAspectsMenu(viewer.getPlayer()));
        }));


        this.addHandler(this.combatModeHandler = new ItemHandler("combat_mode", (viewer, event) -> {
            ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
            if (petHolder == null) return;

            petHolder.toggleCombatMode();
            this.runNextTick(() -> this.flush(viewer));
        }));


        this.addHandler(this.silentHandler = new ItemHandler("silent", (viewer, event) -> {
            ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
            if (petHolder == null) return;

            petHolder.setSilent(!petHolder.isSilent());
            this.runNextTick(() -> this.flush(viewer));
        }));


//        this.addHandler(this.rideHandler = new ItemHandler("pet_ride", (viewer, event) -> {
//            plugin.getPetManager().ridePet(viewer.getPlayer());
//            this.runNextTick(() -> viewer.getPlayer().closeInventory());
//        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            ItemHandler handler = menuItem.getHandler();
            
            menuItem.getOptions().setVisibilityPolicy(viewer -> {
                ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
                if (petHolder == null) return false;

                /*if (handler == this.rideHandler) {
                    return petHolder.getConfig().isRideable();
                }
                else */if (handler == this.inventoryHandler) {
                    return petHolder.getTier().hasInventory() && petHolder.getTemplate().canHaveInventory();
                }
                else if (handler == this.equipmentHandler) {
                    return petHolder.getTier().hasEquipment();
                }
                return true;
            });

            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                ActivePet petHolder = PetEntityBridge.getByPlayer(viewer.getPlayer());
                if (petHolder == null) return;

                ItemReplacer.create(item).readMeta().hideFlags().trimmed()
                    .replace(petHolder.getPlaceholders())
                    .replace(petHolder.getTier().getPlaceholders())
                    .replace(petHolder.getTemplate().getPlaceholders())
                    .writeMeta();
            });

            PetUtils.applyMenuPlaceholders(menuItem);
        });
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        PetUtils.applyMenuPlaceholders(viewer, options);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
        
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Pet Menu"), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack statsItem = ItemUtil.getSkinHead("15b52a5ba47b487a4ea723ccf404b33ac9ed80428c626c099ebee4bb7e6f6363");
        ItemUtil.editMeta(statsItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("STATS")));
            meta.setLore(Lists.newList(
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
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Speed: W: ") + PET_ATTRIBUTE.apply(AttributeRegistry.MOVEMENT_SPEED) + LIGHT_GRAY.enclose(" / F: ") + PET_ATTRIBUTE.apply(AttributeRegistry.FLYING_SPEED))
//                "",
//                LIGHT_YELLOW.enclose(BOLD.enclose("ASPECTS")),
//                LIGHT_YELLOW.enclose("▪ #ddeceeStrength: %pet_aspect_strength%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeVitality: %pet_aspect_vitality%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeDefense: %pet_aspect_defense%"),
//                LIGHT_YELLOW.enclose("▪ #ddeceeDexterity: %pet_aspect_dexterity%")
            ));
        });
        list.add(new MenuItem(statsItem).setPriority(10).setSlots(4));

        ItemStack despawnItem = ItemUtil.getSkinHead("15f1adb58db6e2e54a84739b2c79ddd4014b85f76511df41a9278d7151f6cdbf");
        ItemUtil.editMeta(despawnItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Return to Collection")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Return pet to your collection.")
            ));
        });
        list.add(new MenuItem(despawnItem).setPriority(10).setSlots(40).setHandler(this.despawnHandler));

        ItemStack silentItem = ItemUtil.getSkinHead("5159ea5fbc4e98a9b603854bbd4f1b07aefcd4df051b3fa6158bbf959be44413");
        ItemUtil.editMeta(silentItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Silent Mode")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Status: ") + PET_SILENT),
                "",
                LIGHT_GRAY.enclose("Disables pet ambient sounds."),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(silentItem).setPriority(10).setSlots(19).setHandler(this.silentHandler));

//        ItemStack rideItem = ItemUtil.getSkinHead("");
//        ItemUtil.editMeta(rideItem, meta -> {
//            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Ride")));
//            meta.setLore(Lists.newList(
//                LIGHT_GRAY.enclose("Ride your pet!")
//            ));
//        });
//        list.add(new MenuItem(rideItem).setPriority(10).setSlots(10).setHandler(this.rideHandler));

        ItemStack combatItem = ItemUtil.getSkinHead("509dedccbde876c98bc002bfeedb8a8ad4640128f45e479dd7287d9f80663075");
        ItemUtil.editMeta(combatItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Combat Mode")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + PET_COMBAT_MODE),
                "",
                LIGHT_YELLOW.enclose(BOLD.enclose("Passive: ")) + LIGHT_GRAY.enclose("Never attacks."),
                "",
                LIGHT_YELLOW.enclose(BOLD.enclose("Protective: ")) + LIGHT_GRAY.enclose("Defends owner when attacked."),
                "",
                LIGHT_YELLOW.enclose(BOLD.enclose("Supportive: ")) + LIGHT_GRAY.enclose("Supports owner's attacks."),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(combatItem).setPriority(10).setSlots(23).setHandler(this.combatModeHandler));

        ItemStack renameItem = ItemUtil.getSkinHead("8ff88b122ff92513c6a27b7f67cb3fea97439e078821d6861b74332a2396");
        ItemUtil.editMeta(renameItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Rename")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + PET_NAME),
                "",
                LIGHT_GRAY.enclose("Give new name to your pet."),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_YELLOW.enclose("rename") + ".")
            ));
        });
        list.add(new MenuItem(renameItem).setPriority(10).setSlots(21).setHandler(this.renameHandler));

        ItemStack aspectsItem = ItemUtil.getSkinHead("b62651879d870499da50e34036800ddffd52f3e4e1993c5fc0fc825d03446d8b");
        ItemUtil.editMeta(aspectsItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Aspects")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Aspect Points: ") + PET_ASPECT_POINTS),
                "",
                LIGHT_GRAY.enclose("Improve your pet by " + LIGHT_YELLOW.enclose("upgrading")),
                LIGHT_GRAY.enclose("certain its aspects!"),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_YELLOW.enclose("open") + ".")
            ));
        });
        list.add(new MenuItem(aspectsItem).setPriority(10).setSlots(25).setHandler(this.aspectsHandler));

        ItemStack equipItem = ItemUtil.getSkinHead("d1d2b7dd66ffd86ad4709927b175e83f1a9e10fbc864b2390403708f39d8efd8");
        ItemUtil.editMeta(equipItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Equipment")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Unlocked: ") + PET_EQUIPMENT_UNLOCKED),
                "",
                LIGHT_GRAY.enclose("When " + LIGHT_YELLOW.enclose("unlocked") + ", right-click the pet"),
                LIGHT_GRAY.enclose("with item in hand to equip it."),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Left-Click to " + LIGHT_YELLOW.enclose("toggle") + "."),
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Right-Click to " + LIGHT_YELLOW.enclose("unequip all") + ".")
            ));
        });
        list.add(new MenuItem(equipItem).setPriority(10).setSlots(2).setHandler(this.equipmentHandler));

        ItemStack inventoryItem = ItemUtil.getSkinHead("c390eede381bb8447f7d72e15b56347683e02c17c9b8fc6becd726f0a52c7fc1");
        ItemUtil.editMeta(inventoryItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Inventory")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Storage: ") + PET_INVENTORY_FILLED + LIGHT_GRAY.enclose("/") + TIER_INVENTORY_SIZE),
                "",
                LIGHT_GRAY.enclose("You're making me carry"),
                LIGHT_GRAY.enclose("the heavy stuff, aren't you?"),
                "",
                LIGHT_YELLOW.enclose("[▶] ") + LIGHT_GRAY.enclose("Click to " + LIGHT_YELLOW.enclose("open") + ".")
            ));
        });
        list.add(new MenuItem(inventoryItem).setPriority(10).setSlots(6).setHandler(this.inventoryHandler));


        
        return list;
    }

    @Override
    protected void loadAdditional() {

    }
}
