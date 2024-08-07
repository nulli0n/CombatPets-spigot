package su.nightexpress.combatpets.pet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Stat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class AttributeRegistry {

    public static final String ARMOR                          = "armor";
    public static final String ATTACK_DAMAGE                  = "attack_damage";
    public static final String ATTACK_KNOCKBACK               = "attack_knockback";
    public static final String ATTACK_SPEED                   = "attack_speed";
    //public static final String ATTACK_REACH                   = "attack_reach";
    public static final String KNOCKBACK_RESISTANCE           = "knockback_resistance";
    public static final String FLYING_SPEED                   = "flying_speed";
    public static final String MOVEMENT_SPEED                 = "movement_speed";
    public static final String MAX_HEALTH                     = "max_health";
    public static final String HORSE_JUMP_STRENGTH            = "horse_jump_strength";
    public static final String SCALE                          = "scale";
    public static final String STEP_HEIGHT                    = "step_height";
    public static final String GRAVITY                        = "gravity";
    public static final String SAFE_FALL_DISTANCE             = "safe_fall_distance";
    public static final String FALL_DAMAGE_MULTIPLIER         = "fall_damage_multiplier";
    public static final String BURNING_TIME                   = "burning_time";
    public static final String EXPLOSION_KNOCKBACK_RESISTANCE = "explosion_knockback_resistance";
    public static final String MOVEMENT_EFFICIENCY            = "movement_efficiency";
    public static final String WATER_MOVEMENT_EFFICIENCY      = "water_movement_efficiency";

    public static final String MAX_SATURATION           = "max_saturation";
    public static final String HEALTH_REGENEATION_FORCE = "health_regeneration_force";
    public static final String HEALTH_REGENEATION_SPEED = "health_regeneration_speed";

    private static final Map<String, Stat> ATTRIBUTE_MAP = new HashMap<>();

    @NotNull
    public static Set<Stat> values() {
        return new HashSet<>(ATTRIBUTE_MAP.values());
    }

    @NotNull
    public static Stream<Stat> stream() {
        return values().stream();
    }

    public static void register(@NotNull Stat attribute) {
        ATTRIBUTE_MAP.put(attribute.getId(), attribute);
    }

    @Nullable
    public static Stat getById(@NotNull String id) {
        return ATTRIBUTE_MAP.get(id.toLowerCase());
    }

    public static void clear() {
        ATTRIBUTE_MAP.clear();
    }

//    @NotNull
//    public static PetAttribute register(@NotNull String id) {
//        return register(id, null);
//    }
//
//    @NotNull
//    public static PetAttribute register(@NotNull String id, @Nullable Attribute vanillaMirror) {
//        PetAttribute attribute = new PetAttribute(id, vanillaMirror);
//        ATTRIBUTE_MAP.put(attribute.getId(), attribute);
//        return attribute;
//    }
}
