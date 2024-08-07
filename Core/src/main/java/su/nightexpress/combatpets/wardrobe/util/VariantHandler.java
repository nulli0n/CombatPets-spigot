package su.nightexpress.combatpets.wardrobe.util;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class VariantHandler<T> {

//    private final Supplier<List<T>>                    supplier;
//    private final Function<T, String>                  localizer;
//    private final Function<String, T>                  parser;
//    private final Function<T, String>                  rawer;
//    private final Function<LivingEntity, T>            reader;
//    private final BiFunction<LivingEntity, T, Boolean> applier;

    public VariantHandler(
//        Supplier<List<T>> supplier,
//                          Function<T, String> localizer,
//                          Function<String, T> parser,
//                          Function<T, String> rawer,
//                          Function<LivingEntity, T> reader,
//                          BiFunction<LivingEntity, T, Boolean> applier
    ) {
//        this.supplier = supplier;
//        this.localizer = localizer;
//        this.parser = parser;
//        this.rawer = rawer;
//        this.reader = reader;
//        this.applier = applier;
    }

    @NotNull
    public abstract List<T> values();

    @Nullable
    public abstract T parse(@NotNull String raw);

    @Nullable
    public abstract T read(@NotNull LivingEntity entity);

    @NotNull
    public abstract String getLocalized(@NotNull T value);

    @NotNull
    public abstract String getRaw(@NotNull T value);

    public abstract boolean apply(@NotNull LivingEntity entity, @Nullable T value);

//    @NotNull
//    public List<T> values() {
//        return this.supplier.get();
//    }

    @NotNull
    public List<String> rawValues() {
        return this.values().stream().map(this::getRaw).toList();
    }



//    @Nullable
//    public T parse(@NotNull String raw) {
//        return this.parser.apply(raw);
//    }

//    @Nullable
//    public T read(@NotNull LivingEntity entity) {
//        return this.reader.apply(entity);
//    }

    @NotNull
    public String getLocalized(@NotNull String raw) {
        T value = this.parse(raw);
        return value == null ? raw : this.getLocalized(value);
    }

//    @NotNull
//    public String getLocalized(@NotNull T value) {
//        return this.localizer.apply(value);
//    }

//    @NotNull
//    public String getRaw(@NotNull T value) {
//        String raw = this.rawer.apply(value);
//        return raw == null ? "null" : raw;
//    }

    @Nullable
    public String getRaw(@NotNull LivingEntity entity) {
        T value = this.read(entity);
        return value == null ? null : this.getRaw(value);
    }

    public boolean apply(@NotNull LivingEntity entity, @NotNull String raw) {
        T value = this.parse(raw);
        return value != null && this.apply(entity, value);
    }

//    public boolean apply(@NotNull LivingEntity entity, @Nullable T value) {
//        return this.applier.apply(entity, value);
//    }

    public boolean alreadyHas(@NotNull LivingEntity entity, @NotNull String raw) {
        return this.alreadyHas(entity, this.parse(raw));
    }

    public boolean alreadyHas(@NotNull LivingEntity entity, @Nullable T value) {
        return this.read(entity) == value;
    }
}
