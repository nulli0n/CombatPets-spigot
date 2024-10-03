package su.nightexpress.combatpets.wardrobe.config;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.wrapper.UniParticle;
import su.nightexpress.nightcore.util.wrapper.UniSound;

public class WardrobeConfig {

    public static final ConfigValue<UniSound> WARDROBE_ACCESSORY_APPLY_SOUND = ConfigValue.create("Customizer.Apply_Sound",
        UniSound.of(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        "Sets sound to play when customizer is applied on a pet."
    );

    public static final ConfigValue<UniParticle> WARDROBE_ACCESSORY_APPLY_EFFECT = ConfigValue.create("Customizer.Apply_Effect",
        UniParticle.of(Particle.CLOUD),
        "Sets particle effect to play when customizer is applied on a pet."
    );

    public static final ConfigValue<ItemStack> WARDROBE_ACCESSORY_ITEM = ConfigValue.create("Customizer.Item",
        PetCreator.getDefaultAccessory(),
        "Item used to give and apply pet customizations."
    );

}
