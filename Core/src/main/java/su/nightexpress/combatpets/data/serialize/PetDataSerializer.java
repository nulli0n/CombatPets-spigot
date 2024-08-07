package su.nightexpress.combatpets.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.combatpets.PetAPI;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.wardrobe.PetWardrobe;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.StringUtil;

import java.lang.reflect.Type;
import java.util.*;

public class PetDataSerializer implements JsonSerializer<PetData>, JsonDeserializer<PetData> {

    @Override
    public JsonElement serialize(PetData petData, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        Map<String, String> equipmentRaw = new HashMap<>();
        petData.getEquipment().forEach((slot, itemStack) -> {
            equipmentRaw.put(slot.name(), ItemNbt.compress(itemStack));
        });

        object.addProperty("configId", petData.getConfig().getId());
        object.addProperty("tierId", petData.getTier().getId());
        object.addProperty("name", petData.getName());
        object.addProperty("silent", petData.isSilent());
        object.addProperty("reviveDate", petData.getReviveDate());
        object.addProperty("health", petData.getHealth());
        object.addProperty("saturation", petData.getFoodLevel());
        object.addProperty("level", petData.getLevel());
        object.addProperty("exp", petData.getXP());
        object.addProperty("aspectPoints", petData.getAspectPoints());
        object.add("aspects", context.serialize(petData.getAspects()));
        object.add("inventory", context.serialize(ItemNbt.compress(petData.getInventory())));
        object.add("equipmentSlots", context.serialize(equipmentRaw));
        object.addProperty("state", petData.getCombatMode().name());
        object.add("wardrobe", context.serialize(petData.getWardrobe()));

        return object;
    }

    @Override
    public PetData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        String configId = object.get("configId").getAsString();
        Template config = PetAPI.getPetManager().getTemplate(configId);
        if (config == null) return null;

        String tierId = object.get("tierId").getAsString();
        Tier tier = PetAPI.getPetManager().getTier(tierId);
        if (tier == null) return null;

        String name = object.get("name").getAsString();

        boolean silent = false;
        if (object.get("silent") != null) silent = object.get("silent").getAsBoolean();

        long reviveDate = 0L;
        if (object.get("reviveDate") != null) reviveDate = object.get("reviveDate").getAsLong();

        double health = object.get("health").getAsDouble();
        double saturation = object.get("saturation").getAsDouble();
        int level = object.get("level").getAsInt();
        int xp = object.get("exp").getAsInt();
        int aspectPoints = object.get("aspectPoints").getAsInt();

        Map<String, Integer> aspects = context.deserialize(object.get("aspects"), new TypeToken<Map<String, Integer>>(){}.getType());

        List<String> inventoryRaw = context.deserialize(object.get("inventory"), new TypeToken<List<String>>() {}.getType());

        List<ItemStack> inventory = new ArrayList<>(Arrays.asList(ItemNbt.decompress(inventoryRaw)));

        Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
        JsonElement equipmentOld = object.get("equipment");
        if (equipmentOld != null && object.get("equipmentSlots") == null) {
            equipment.putAll(context.deserialize(object.get("equipment"), new TypeToken<Map<EquipmentSlot, ItemStack>>(){}.getType()));
        }
        else {
            Map<String, String> equipmentRaw = context.deserialize(object.get("equipmentSlots"), new TypeToken<Map<String, String>>(){}.getType());
            equipmentRaw.forEach((slotName, nbt) -> {
                EquipmentSlot slot = StringUtil.getEnum(slotName, EquipmentSlot.class).orElse(null);
                ItemStack itemStack = ItemNbt.decompress(nbt);
                if (slot == null || itemStack == null) return;

                equipment.put(slot, itemStack);
            });
        }

        CombatMode state = StringUtil.getEnum(object.get("state").getAsString(), CombatMode.class).orElse(CombatMode.PASSIVE);

        PetWardrobe wardrobe;
        if (object.get("wardrobe") != null) {
            wardrobe = context.deserialize(object.get("wardrobe"), new TypeToken<PetWardrobe>(){}.getType());
        }
        else wardrobe = new PetWardrobe();

        return new PetData(config, tier, name, silent, reviveDate, health, saturation, level, xp, aspectPoints, aspects, inventory, equipment, state, wardrobe);
    }
}
