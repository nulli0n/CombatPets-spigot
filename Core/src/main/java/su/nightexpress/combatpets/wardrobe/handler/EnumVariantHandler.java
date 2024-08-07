package su.nightexpress.combatpets.wardrobe.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.wardrobe.util.VariantHandler;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.List;

public abstract class EnumVariantHandler<E extends Enum<E>> extends VariantHandler<E> {

    protected final Class<E> clazz;

    public EnumVariantHandler(@NotNull Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    @NotNull
    public List<E> values() {
        return Lists.newList(clazz.getEnumConstants());
    }

    @Override
    @Nullable
    public E parse(@NotNull String raw) {
        return StringUtil.getEnum(raw, this.clazz).orElse(null);
    }

    @Override
    @NotNull
    public String getRaw(@NotNull E value) {
        return value.name().toLowerCase();
    }
}
