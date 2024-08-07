package su.nightexpress.combatpets.wardrobe.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.function.Function;

public class EntityVariants {

    public static final Function<String, Boolean> PARSER_BOOLEAN = Boolean::parseBoolean;

    @NotNull
    public static <E extends Enum<E>> Function<String, E> enumParser(@NotNull Class<E> clazz) {
        return str -> StringUtil.getEnum(str, clazz).orElse(null);
    }

    @NotNull
    public static <E extends Enum<E>> Function<E, String> enumLocalizer(@NotNull LangEnum<E> langEnum) {
        return langEnum::getLocalized;
    }
}
