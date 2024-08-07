package su.nightexpress.combatpets.wardrobe;

import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.Wardrobe;
import su.nightexpress.combatpets.wardrobe.util.EntityVariant;
import su.nightexpress.combatpets.wardrobe.util.VariantRegistry;

import java.util.HashMap;
import java.util.Map;

public class PetWardrobe implements Wardrobe {

    private final Map<String, String> accessories;

    public PetWardrobe() {
        this(new HashMap<>());
    }

    public PetWardrobe(@NotNull Map<String, String> accessories) {
        this.accessories = new HashMap<>(accessories);
    }

    @NotNull
    public Map<String, String> getAccessories() {
        return this.accessories;
    }

    public <T> void addAccessory(@NotNull EntityVariant<T> variant, @NotNull T value) {
        this.addAccessory(variant, variant.getHandler().getRaw(value));
    }

    public void addAccessory(@NotNull EntityVariant<?> variant, @NotNull String value) {
        this.addAccessory(variant.getName(), value);
    }

    public void addAccessory(@NotNull String variant, @NotNull String value) {
        this.accessories.put(variant, value.toLowerCase());
    }

    @Override
    public void dressUp(@NotNull LivingEntity entity) {
        this.getAccessories().forEach(((name, raw) -> {
            EntityVariant<?> variant = VariantRegistry.getVariant(name);
            if (variant == null) return;

            variant.getHandler().apply(entity, raw);
        }));
    }

    @NotNull
    public static PetWardrobe of(@NotNull LivingEntity entity) {
        PetWardrobe customizer = new PetWardrobe();

        VariantRegistry.getVariants().forEach(variant -> {
            String value = variant.getHandler().getRaw(entity);
            if (value == null) return;

            customizer.addAccessory(variant, value);
        });

//        if (pet instanceof Ageable ageable) {
//            customizer.addCustomization(CustomizerType.AGE, AgeType.from(ageable));
//        }

//        if (pet instanceof Creeper creeper) {
//            customizer.addCustomization(CustomizerType.CREEPER_CHARGE, CreeperCharge.from(creeper));
//        }
//        else if (pet instanceof Horse horse) {
//            customizer.addCustomization(CustomizerType.HORSE_STYLE, horse.getStyle());
//            customizer.addCustomization(CustomizerType.HORSE_COLOR, horse.getColor());
//        }
//        else if (pet instanceof Fox fox) {
//            customizer.addCustomization(CustomizerType.FOX_TYPE, fox.getFoxType());
//        }
//        else if (pet instanceof Slime slime) {
//            customizer.addCustomization(CustomizerType.SLIME_SIZE, SlimeSize.from(slime));
//        }
//        else if (pet instanceof Parrot parrot) {
//            customizer.addCustomization(CustomizerType.PARROT_VARIANT, parrot.getVariant());
//        }
//        else if (pet instanceof Llama llama) {
//            customizer.addCustomization(CustomizerType.LLAMA_COLOR, llama.getColor());
//        }
//        else if (pet instanceof Sheep sheep) {
//            customizer.addCustomization(CustomizerType.SHEEP_COLOR, sheep.getColor() == null ? DyeColor.WHITE : sheep.getColor());
//        }
//        else if (pet instanceof Rabbit rabbit) {
//            customizer.addCustomization(CustomizerType.RABBIT_TYPE, rabbit.getRabbitType());
//        }
//        else if (pet instanceof Cat cat) {
//            customizer.addCustomization(CustomizerType.CAT_TYPE, cat.getCatType());
//        }
//        else if (pet instanceof MushroomCow mushroomCow) {
//            customizer.addCustomization(CustomizerType.MUSHROOM_VARIANT, mushroomCow.getVariant());
//        }
//        else if (pet instanceof Villager villager) {
//            customizer.addCustomization(CustomizerType.VILLAGER_PROFESSION, villager.getProfession());
//        }
//        else if (pet instanceof ZombieVillager zombieVillager) {
//            if (zombieVillager.getVillagerProfession() != null) {
//                customizer.addCustomization(CustomizerType.ZOMBIE_VILLAGER_PROFESSION, zombieVillager.getVillagerProfession());
//            }
//        }

        return customizer;
    }
}
