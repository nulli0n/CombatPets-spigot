package su.nightexpress.combatpets.wardrobe.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VariantRegistry {

    private static final Map<String, EntityVariant<?>> VARIANT_MAP = new HashMap<>();

    public static void register(@NotNull EntityVariant<?> variant) {
        VARIANT_MAP.put(variant.getName(), variant);
    }

    public static void clear() {
        VARIANT_MAP.clear();
    }

    @Nullable
    public static EntityVariant<?> getVariant(@NotNull String name) {
        return VARIANT_MAP.get(name.toLowerCase());
    }

    @NotNull
    public static Set<EntityVariant<?>> getVariants() {
        return new HashSet<>(VARIANT_MAP.values());
    }

    @NotNull
    public static List<String> getVariantNames() {
        return new ArrayList<>(VARIANT_MAP.keySet());
    }
}
