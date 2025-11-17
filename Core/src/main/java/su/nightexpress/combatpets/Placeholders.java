package su.nightexpress.combatpets;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.pet.impl.PetAspect;
import su.nightexpress.combatpets.pet.impl.PetInstance;
import su.nightexpress.combatpets.pet.impl.PetTemplate;
import su.nightexpress.combatpets.pet.impl.PetTier;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    public static final String WIKI_URL              = "https://nightexpress.gitbook.io/combatpets";
    public static final String WIKI_CURRENCY_URL     = WIKI_URL + "features/multi-currency";
    public static final String WIKI_PET_TYPES_URL    = WIKI_URL + "features/pet-types";
    public static final String WIKI_ATTRIBUTES_URL   = WIKI_URL + "features/pet-attributes";
    public static final String WIKI_ASPECTS_URL      = WIKI_URL + "features/pet-aspects";
    public static final String WIKI_FOOD_URL         = WIKI_URL + "features/pet-food";
    public static final String WIKI_PLACEHOLDERS_URL = WIKI_URL + "utility/placeholders";

    public static final String GENERIC_SUCCESS = "%success%";
    public static final String GENERIC_FAILURE = "%failure%";
    public static final String GENERIC_MAX     = "%max%";
    public static final String GENERIC_EXP     = "%exp%";
    public static final String GENERIC_LEVEL   = "%level%";
    public static final String GENERIC_AMOUNT  = "%amount%";
    public static final String GENERIC_ITEM    = "%item%";
    public static final String GENERIC_TIME    = "%time%";
    public static final String GENERIC_COST    = "%cost%";
    public static final String GENERIC_PRICE   = "%price%";
    public static final String GENERIC_TOTAL   = "%total%";
    public static final String GENERIC_TYPE    = "%type%";

    public static final String CURRENCY_ID   = "%currency_id%";
    public static final String CURRENCY_NAME = "%currency_name%";

    public static final String ASPECT_NAME  = "%aspect_name%";

    public static final Function<String, String> PET_ATTRIBUTE              = name -> "%pet_attribute_" + name + "%";
    public static final Function<String, String> PET_CONFIG_ATTRIBUTE_START = name -> "%pet_config_attribute_start_" + name + "%";
    public static final Function<String, String> PET_ASPECT                 = name -> "%pet_aspect_" + name + "%";

    public static final String PET_NAME               = "%pet_name%";
    public static final String PET_SILENT             = "%pet_silent%";
    public static final String PET_IS_DEAD            = "%pet_is_dead%";
    public static final String PET_LEVEL              = "%pet_level%";
    public static final String PET_HEALTH             = "%pet_health%";
    public static final String PET_MAX_HEALTH         = "%pet_max_health%";
    public static final String PET_SATURATION         = "%pet_saturation%";
    public static final String PET_MAX_SATURATION     = "%pet_max_saturation%";
    public static final String PET_FOOD               = "%pet_food%";
    public static final String PET_XP                 = "%pet_exp%";
    public static final String PET_REQUIRED_XP        = "%pet_max_exp%";
    public static final String PET_ASPECT_POINTS      = "%pet_aspect_points%";
    public static final String PET_COMBAT_MODE        = "%pet_combat_mode%";
    public static final String PET_EQUIPMENT_UNLOCKED = "%pet_equipment_unlocked%";
    public static final String PET_INVENTORY_FILLED   = "%pet_inventory_filled%";
    public static final String PET_OWNER_NAME         = "%pet_owner_name%";

    public static final String TEMPLATE_DEFAULT_NAME = "%pet_config_default_name%";
    public static final String TEMPLATE_ID           = "%pet_config_id%";
    public static final String TEMPLATE_NAME         = "%pet_config_name%";

    public static final String TIER_ID                    = "%pet_tier_id%";
    public static final String TIER_NAME                  = "%pet_tier_name%";
    public static final String TIER_INVENTORY_HAS         = "%pet_tier_inventory_has%";
    public static final String TIER_INVENTORY_SIZE        = "%pet_tier_inventory_size%";
    public static final String TIER_EQUIPMENT_HAS         = "%pet_tier_equipment_has%";
    public static final String TIER_DEATH_REVIVE_COOLDOWN = "%pet_tier_death_revive_cooldown%";
    public static final String TIER_DEATH_REVIVE_COST     = "%pet_tier_death_revive_cost%";

    @NotNull
    public static PlaceholderMap forData(@NotNull PetData data) {
        PlaceholderMap placeholderMap = new PlaceholderMap()
            .add(PET_NAME, data::getName)
            .add(PET_SILENT, () -> CoreLang.STATE_ENABLED_DISALBED.get(data.isSilent()))
            .add(PET_HEALTH, () -> NumberUtil.format(data.getHealth()))
            .add(PET_IS_DEAD, () -> CoreLang.STATE_YES_NO.get(data.isDead()))
            .add(PET_FOOD, () -> {
                if (data.getConfig().getFoodCategories().isEmpty()) return CoreLang.OTHER_NONE.text();

                return data.getConfig().getFoodCategories().stream()
                        .map(name -> PetAPI.getPetManager().getFoodCategory(name))
                        .filter(Objects::nonNull)
                        .map(FoodCategory::getName)
                        .collect(Collectors.joining(", "));
                }
            )
            .add(PET_SATURATION, () -> NumberUtil.format(data.getFoodLevel()))
            .add(PET_MAX_SATURATION, () -> {
                double maxSaturation = data.getAttributeValue(AttributeRegistry.MAX_SATURATION);
                return maxSaturation <= 0D ? CoreLang.OTHER_INFINITY.text() : NumberUtil.format(maxSaturation);
            })
            .add(PET_LEVEL, () -> String.valueOf(data.getLevel()))
            .add(PET_XP, () -> NumberUtil.format(data.getXP()))
            .add(PET_REQUIRED_XP, () -> NumberUtil.format(data.getRequiredXP()))
            .add(PET_ASPECT_POINTS, () -> NumberUtil.format(data.getAspectPoints()))
            .add(PET_COMBAT_MODE, () -> Lang.COMBAT_MODE.getLocalized(data.getCombatMode()));

        for (String aspect : data.getTier().getAspectsMax().keySet()) {
            placeholderMap.add(PET_ASPECT.apply(aspect), () -> NumberUtil.format(data.getAspect(aspect)));
        }

        for (Stat attribute : AttributeRegistry.values()) {
            placeholderMap.add(PET_ATTRIBUTE.apply(attribute.getId()), () -> NumberUtil.format(data.getAttributeValue(attribute)));
        }

        return placeholderMap;
    }

    @NotNull
    public static PlaceholderMap forHolder(@NotNull PetInstance instance) {

//        for (IPetAttribute attribute : AttributeRegistry.values()) {
//            placeholderMap.add(PET_ATTRIBUTE.apply(attribute.getId()), () -> NumberUtil.format(holder.getAttribute(attribute)));
//        }

        return new PlaceholderMap()
            .add(PET_HEALTH, () -> NumberUtil.format(instance.getEntity().getHealth()))
            .add(PET_MAX_HEALTH, () -> NumberUtil.format(instance.getMaxHealth()))
            .add(PET_OWNER_NAME, () -> instance.getOwner().getName())
            .add(PET_EQUIPMENT_UNLOCKED, () -> CoreLang.STATE_YES_NO.get(instance.isEquipmentUnlocked()))
            .add(PET_INVENTORY_FILLED, () -> String.valueOf(Stream.of(instance.getInventory().getContents()).filter(i -> i != null && !i.getType().isAir()).count()))
            .add(instance.getData().getPlaceholders());
    }

    @NotNull
    public static PlaceholderMap forTier(@NotNull PetTier tier) {
        return new PlaceholderMap()
            .add(TIER_ID, tier::getId)
            .add(TIER_NAME, tier::getName)
            .add(TIER_INVENTORY_HAS, () -> CoreLang.STATE_YES_NO.get(tier.hasInventory()))
            .add(TIER_INVENTORY_SIZE, () -> String.valueOf(tier.getInventorySize()))
            .add(TIER_EQUIPMENT_HAS, () -> CoreLang.STATE_YES_NO.get(tier.hasEquipment()))
            .add(TIER_DEATH_REVIVE_COOLDOWN, () -> TimeUtil.formatTime(tier.getAutoRespawnTime()))
            .add(TIER_DEATH_REVIVE_COST, () -> {
                Currency currency = EconomyBridge.getCurrency(tier.getReviveCurrency());
                return currency == null ? "0" : currency.format(tier.getReviveCost());
            })
            ;
    }

    @NotNull
    public static PlaceholderMap forPet(@NotNull PetTemplate petTemplate) {
        PlaceholderMap placeholderMap = new PlaceholderMap()
            .add(TEMPLATE_ID, petTemplate::getId)
            .add(TEMPLATE_NAME, petTemplate::getDefaultName)
            .add(TEMPLATE_DEFAULT_NAME, petTemplate::getDefaultName);

        for (Stat attribute : AttributeRegistry.values()) {
            placeholderMap.add(PET_CONFIG_ATTRIBUTE_START.apply(attribute.getId()), () -> NumberUtil.format(petTemplate.getAttributeStart(attribute)));
        }

        return placeholderMap;
    }

    @NotNull
    public static PlaceholderMap forAspect(@NotNull PetAspect aspect) {
        return new PlaceholderMap()
            .add(ASPECT_NAME, aspect::getName);
    }
}
